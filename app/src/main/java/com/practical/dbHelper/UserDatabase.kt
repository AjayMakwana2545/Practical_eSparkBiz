package com.practical.dbHelper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practical.dao.UsersDao
import com.practical.models.UserDetails

@Database(entities = [UserDetails::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao

    companion object {
        private var instance: UserDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): UserDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, UserDatabase::class.java,
                    "user_details.db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()

            return instance!!

        }

        fun destroyInstance() {
            instance = null
        }
    }
}
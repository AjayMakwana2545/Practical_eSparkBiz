package com.practical.dbHelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.practical.models.UserDetails

class DBHelper(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?
) :
    SQLiteOpenHelper(
        context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val createUserDetailsTable = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY," +
                COL_FIRST_NAME + " TEXT," +
                COL_LAST_NAME + " TEXT," +
                COL_ADDRESS + " TEXT," +
                COL_MOBILE_NUMBER + " TEXT," +
                COL_PROFILE_IMAGE + " TEXT,"
                + ")")
        db.execSQL(createUserDetailsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<UserDetails> {
        val empList: ArrayList<UserDetails> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int?
        var firstName: String?
        var lastName: String?
        var address: String?
        var phoneNumber: String?
        var profileImagePath: String?
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                firstName = cursor.getString(cursor.getColumnIndex(COL_FIRST_NAME))
                lastName = cursor.getString(cursor.getColumnIndex(COL_LAST_NAME))
                address = cursor.getString(cursor.getColumnIndex(COL_ADDRESS))
                phoneNumber = cursor.getString(cursor.getColumnIndex(COL_MOBILE_NUMBER))
                profileImagePath = cursor.getString(cursor.getColumnIndex(COL_PROFILE_IMAGE))
                val emp =
                    UserDetails(id, firstName, lastName, address, phoneNumber, profileImagePath)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    //method to update user details
    fun updateEmployee(user: UserDetails): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID, user.id)
        contentValues.put(COL_FIRST_NAME, user.firstName)
        contentValues.put(COL_LAST_NAME, user.lastName)
        contentValues.put(COL_ADDRESS, user.address)
        contentValues.put(COL_MOBILE_NUMBER, user.phoneNumber)
        contentValues.put(COL_PROFILE_IMAGE, user.profileImagePath)
        // updating user details
        val success = db.update(TABLE_NAME, contentValues, "id=" + user.id, null)
        db.close()
        return success
    }

    //method to delete user details
    fun deleteEmployee(user: UserDetails): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID,user.id)
        // Deleting Row
        val success = db.delete(TABLE_NAME, "id=" + user.id, null)
        db.close()
        return success
    }


    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "USER_DETAILS.db"
        const val TABLE_NAME = "USERS"
        const val COL_ID = "ID"
        const val COL_FIRST_NAME = "FIRST_NAME"
        const val COL_LAST_NAME = "LAST_NAME"
        const val COL_ADDRESS = "ADDRESS"
        const val COL_MOBILE_NUMBER = "MOBILE_NUMBER"
        const val COL_PROFILE_IMAGE = "PROFILE_IMAGE"
    }
}
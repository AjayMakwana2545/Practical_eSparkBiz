package com.practical.dbHelper

import android.content.Context
import androidx.lifecycle.LiveData
import com.practical.dao.UsersDao
import com.practical.models.UserDetails
import com.practical.utils.subscribeOnBackground

class UsersRepository(mContext: Context) {
    private var usersDao: UsersDao
    private var allUserDetails: List<UserDetails>

    private val database = UserDatabase.getInstance(mContext)

    init {
        usersDao = database.usersDao()
        allUserDetails = usersDao.getAllUserDetails()
    }

    fun insertUser(userDetails: UserDetails) {
        subscribeOnBackground {
            usersDao.insert(userDetails)
        }
    }

    fun updateUser(userDetails: UserDetails) {
        subscribeOnBackground {
            usersDao.update(userDetails)
        }
    }

    fun deleteUser(userDetails: UserDetails) {
        subscribeOnBackground {
            usersDao.delete(userDetails)
        }
    }

    fun getAllUsers(): List<UserDetails> {
        return allUserDetails
    }
}

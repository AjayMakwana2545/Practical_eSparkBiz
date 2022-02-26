package com.practical.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.practical.models.UserDetails

@Dao
interface UsersDao {

    @Insert
    fun insert(userDetails: UserDetails)

    @Update
    fun update(userDetails: UserDetails)

    @Delete
    fun delete(userDetails: UserDetails)

    @Query("select * from UserDetails")
    fun getAllUserDetails(): List<UserDetails>
}
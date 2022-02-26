package com.practical.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserDetails")
data class UserDetails(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val firstName: String?,
    val lastName: String?,
    val address: String?,
    val phoneNumber: String?,
    val profileImagePath: String?
)
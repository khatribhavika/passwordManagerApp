package com.example.myapplication.data

data class PasswordEntity (
    val id: Int = 0,
    val accountType: String,
    val username: String,
    val encryptedPassword: String
)
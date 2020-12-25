package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var full_name: String? = "",
    var email: String? = "",
    var password: String? = ""
)
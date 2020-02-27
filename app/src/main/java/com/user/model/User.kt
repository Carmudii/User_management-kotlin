package com.user.model

data class User(
    var id: Int,
    var create_at: String,
    var update_at: String,
    var delete: String,
    var name: String,
    var email: String,
    var password: String,
    var gender: String,
    var address: String,
    var role: String,
    var photo: String
)
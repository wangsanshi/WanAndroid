package com.lei.wanandroid.data.bean

data class User(
    var admin: Boolean,
    var email: String,
    var icon: String,
    var id: Int,
    var nickname: String,
    var password: String,
    var publicName: String,
    var token: String,
    var type: Int,
    var username: String
)
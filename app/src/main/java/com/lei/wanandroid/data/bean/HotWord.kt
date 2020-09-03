package com.lei.wanandroid.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotword")
data class HotWord(
    @PrimaryKey
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)
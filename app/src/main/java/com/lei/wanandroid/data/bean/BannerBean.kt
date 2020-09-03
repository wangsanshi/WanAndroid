package com.lei.wanandroid.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banner")
data class BannerBean(
    val desc: String,
    @PrimaryKey
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)
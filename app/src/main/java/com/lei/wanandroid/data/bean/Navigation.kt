package com.lei.wanandroid.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lei.wanandroid.jetpack.room.ArticleListConvert


/**
 * 导航
 */
@Entity(tableName = "navigation")
@TypeConverters(ArticleListConvert::class)
data class Navigation(
    @PrimaryKey val cid: Int,
    val name: String,
    val articles: List<Article>
)
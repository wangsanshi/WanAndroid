package com.lei.wanandroid.data.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lei.wanandroid.jetpack.room.TreeChildrenListConvert

@Entity(tableName = "tree")
@TypeConverters(TreeChildrenListConvert::class)
data class Tree(
    val children: List<Children>,
    val courseId: Int,
    @PrimaryKey val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
)

data class Children(
    @Ignore
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
)
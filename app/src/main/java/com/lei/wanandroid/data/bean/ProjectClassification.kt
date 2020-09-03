package com.lei.wanandroid.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * 项目分类
 */
@Parcelize
@Entity(tableName = "project_classification")
data class ProjectClassification(
    val courseId: Int,
    @PrimaryKey
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
) : Parcelable
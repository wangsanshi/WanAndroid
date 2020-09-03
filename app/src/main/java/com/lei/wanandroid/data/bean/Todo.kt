package com.lei.wanandroid.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "todo")
data class Todo(
    val completeDate: Long?,
    val completeDateStr: String,
    val content: String,
    val date: Long,
    val dateStr: String,
    @PrimaryKey val id: Int,
    val priority: Int,
    val status: Int,
    val title: String,
    val type: Int,
    val userId: Int
) : Parcelable
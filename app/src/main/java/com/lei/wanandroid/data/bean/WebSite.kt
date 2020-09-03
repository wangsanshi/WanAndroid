package com.lei.wanandroid.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "website")
data class WebSite(
    val desc: String,
    val icon: String,
    @PrimaryKey val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val userId: Int,
    val visible: Int
) : Parcelable
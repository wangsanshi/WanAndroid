package com.lei.wanandroid.data.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 公众号
 */
@Entity(tableName = "wx_public_account")
data class PublicAccount(
    val courseId: Int,
    @PrimaryKey val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
) {
    @Ignore
    var children: List<String> = emptyList()
}

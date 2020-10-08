package com.lei.wanandroid.jetpack.room

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.lei.wanandroid.data.bean.Article
import com.lei.wanandroid.util.sGson

class ArticleListConvert {

    @TypeConverter
    fun string2List(value: String): List<Article> {
        return sGson.fromJson(value, object : TypeToken<List<Article>>() {}.type)
    }

    @TypeConverter
    fun list2String(list: List<Article>): String {
        return sGson.toJson(list)
    }
}

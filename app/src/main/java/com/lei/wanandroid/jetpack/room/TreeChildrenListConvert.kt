package com.lei.wanandroid.jetpack.room

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.lei.wanandroid.data.bean.Children
import com.lei.wanandroid.util.sGson

class TreeChildrenListConvert {

    @TypeConverter
    fun string2List(value: String): List<Children> {
        return sGson.fromJson(value, object : TypeToken<List<Children>>() {}.type)
    }

    @TypeConverter
    fun list2String(list: List<Children>): String {
        return sGson.toJson(list)
    }
}

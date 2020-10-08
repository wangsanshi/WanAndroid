package com.lei.wanandroid.util

import java.text.SimpleDateFormat
import java.util.*

val sdf1 = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat? {
        return SimpleDateFormat("yyyy.MM.dd   E", Locale.CHINESE)
    }
}

val sdf2 = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat? {
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
    }
}

val sdf3 = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat? {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
    }
}

fun getCurrentMillis() = System.currentTimeMillis()

fun getCurrentDate(sdf: SimpleDateFormat): String {
    return getDateByMillis(getCurrentMillis(), sdf)
}

fun getDateByMillis(millis: Long, sdf: SimpleDateFormat): String {
    return sdf.format(Date(millis))
}

fun parseDateToMillis(dateStr: String, sdf: SimpleDateFormat): Long {
    return sdf.parse(dateStr).time
}
package com.lei.wanandroid.util

import java.lang.reflect.ParameterizedType

fun getClassByParameterizedType(type: ParameterizedType, index: Int): Class<*> {
    return type.actualTypeArguments[index] as Class<*>
}
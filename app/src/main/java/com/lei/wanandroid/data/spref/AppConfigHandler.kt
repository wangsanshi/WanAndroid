package com.lei.wanandroid.data.spref

import android.content.Context
import android.content.SharedPreferences
import com.lei.wanandroid.util.sGson
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

object AppConfigHandler {

    @Suppress("UNCHECKED_CAST")
    fun <T> create(context: Context, clazz: Class<T>): T {
        val config = clazz.getAnnotation(Config::class.java)
        if (config == null) {
            throw RuntimeException("请在配置类中标注@Config()注解")
        }
        if (!clazz.isInterface) {
            throw RuntimeException("配置类必须是接口")
        }
        var name = config.value
        if (name.isBlank()) {
            name = clazz.simpleName
        }
        val spref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz),
            ConfigProxy(spref)
        ) as T
    }

    private class ConfigProxy(val spref: SharedPreferences) : InvocationHandler {

        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
            method?.let {
                val methodName = it.name
                if (methodName.equals("clear", true)) {
                    spref.edit().clear().apply()
                } else if (methodName.startsWith("set", true)) {
                    setValue(methodName.replaceFirst("set", "", true), it, args)
                } else if (methodName.startsWith("get", true)) {
                    return getValue(methodName.replaceFirst("get", "", true), method, args)
                } else if (methodName.startsWith("is", true)) {
                    return spref.getBoolean(methodName.replaceFirst("is", "", true), false)
                } else if (methodName.equals("remove", true)) {
                    args?.let {
                        val key = it[0].toString().toUpperCase(Locale.CHINA)
                        spref.edit().remove(key).apply()
                    }
                }
            }

            return null
        }

        private fun setValue(name: String, method: Method, args: Array<out Any>?) {
            args?.let {
                if (it.size != 1) {
                    throw IllegalArgumentException("set方法的方法参数只允许一个")
                }
                val paramType = method.parameterTypes[0]
                val value = it[0]
                val editor = spref.edit()
                when (paramType) {
                    Boolean::class.java -> editor.putBoolean(name, value as Boolean)
                    String::class.java -> editor.putString(name, value as String)
                    Int::class.java -> editor.putInt(name, value as Int)
                    Long::class.java -> editor.putLong(name, value as Long)
                    Float::class.java -> editor.putFloat(name, value as Float)
                    else -> {
                        editor.putString(name, sGson.toJson(value))
                    }
                }
                editor.apply()
            }
        }

        private fun getValue(name: String, method: Method, args: Array<out Any>?): Any? {
            val returnType = method.returnType
            var defaultValue: Any? = null
            args?.let {
                defaultValue = it[0]
            }
            when (returnType) {
                String::class.java -> return spref.getString(name, defaultValue as String)
                Boolean::class.java -> return spref.getBoolean(name, defaultValue as Boolean)
                Int::class.java -> return spref.getInt(name, defaultValue as Int)
                Long::class.java -> return spref.getLong(name, defaultValue as Long)
                Float::class.java -> return spref.getFloat(name, defaultValue as Float)
                else -> {
                    return sGson.fromJson(spref.getString(name, null), returnType)
                }
            }
        }
    }

}
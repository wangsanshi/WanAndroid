package com.lei.wanandroid.data.spref

import com.lei.wanandroid.data.bean.User

@Config(value = "user")
interface IUserConfig {

    fun getLoginUserId(default: Int): Int

    fun setNightMode(value: Boolean)

    fun isNightMode(): Boolean

    fun getLoginUser(): User?

    fun setLoginUser(value: User)
}
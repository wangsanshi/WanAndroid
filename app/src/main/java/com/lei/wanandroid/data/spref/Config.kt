package com.lei.wanandroid.data.spref

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Config(val value: String = "")
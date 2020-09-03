package com.lei.wanandroid.data.repository.base

import androidx.lifecycle.MutableLiveData

open class BaseArticleRepository : BaseRepository() {
    /**
     *因为当前的文章可能是普通文章或者收藏文章，所以这里直接用Any
     */
    val currentArticleLiveData by lazy { MutableLiveData<Any>() }
}
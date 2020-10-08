package com.lei.wanandroid.data.repository

import com.lei.wanandroid.data.bean.ReadArticle
import com.lei.wanandroid.data.repository.base.BaseRepository
import kotlinx.coroutines.withContext

class WebViewRepository : BaseRepository() {

    suspend fun saveReadArticle(article: ReadArticle) {
        withContext(ioDispatcher) {
            if (userContext.isLogin()) {
                localDataSource.getReadArticleDao().saveReadArticle(article)
            }
        }
    }

}
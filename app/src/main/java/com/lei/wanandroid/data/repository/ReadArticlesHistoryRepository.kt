package com.lei.wanandroid.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.lei.wanandroid.data.bean.ReadArticle
import com.lei.wanandroid.data.repository.base.BaseRepository

class ReadArticlesHistoryRepository : BaseRepository() {

    fun getReadArticlesHistory(): LiveData<PagedList<ReadArticle>> {
        val factory = localDataSource.getReadArticleDao().getReadArticlesHistoryDataSourceFactory()
        val livePagedList = getPagedListLiveData(factory)
        return livePagedList
    }

}
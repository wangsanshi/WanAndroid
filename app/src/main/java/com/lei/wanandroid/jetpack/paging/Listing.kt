package com.lei.wanandroid.jetpack.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.lei.wanandroid.jetpack.State

data class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val refreshState: LiveData<State>,
    val loadMoreState: LiveData<State>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)
package com.lei.wanandroid.data.repository.base

import com.lei.wanandroid.data.repository.*
import java.util.concurrent.Executors

object RepositoryProviders {
    private val DISK_IO = Executors.newSingleThreadExecutor()

    private val loginRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { LoginRepository() }
    private val registerRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RegisterRepository() }
    private val myRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MyRepository(DISK_IO) }
    private val homeRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        HomeRepository(
            DISK_IO
        )
    }
    private val todoRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        TodoRepository(
            DISK_IO
        )
    }
    private val webviewRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebViewRepository() }
    private val readArticlesHistoryRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ReadArticlesHistoryRepository() }
    private val projectRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ProjectRepository(
            DISK_IO
        )
    }

    fun provideLoginRepository(): LoginRepository {
        return loginRepository
    }

    fun provideRegisterRepository(): RegisterRepository {
        return registerRepository
    }

    fun provideMyRepository(): MyRepository {
        return myRepository
    }

    fun provideHomeRepository(): HomeRepository {
        return homeRepository
    }

    fun provideTodoRepository(): TodoRepository {
        return todoRepository
    }

    fun provideWebViewRepository(): WebViewRepository {
        return webviewRepository
    }

    fun provideReadArticlesHistoryRepository(): ReadArticlesHistoryRepository {
        return readArticlesHistoryRepository
    }

    fun provideProjectRepository(): ProjectRepository {
        return projectRepository
    }
}
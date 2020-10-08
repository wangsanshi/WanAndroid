package com.lei.wanandroid.data.repository

import androidx.lifecycle.MutableLiveData
import com.lei.wanandroid.data.bean.Todo
import com.lei.wanandroid.data.repository.base.BaseRepository
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import com.lei.wanandroid.jetpack.paging.TodoPageBoundaryCallback
import com.lei.wanandroid.net.DefaultHttpCallback
import com.lei.wanandroid.net.DefaultRetrofitClient
import com.lei.wanandroid.ui.fragments.todo.*
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class TodoRepository(private val ioExecutor: Executor) : BaseRepository() {
    val currentTodoLiveData by lazy { MutableLiveData<Todo>() }

    fun getTodos(status: Int, type: Int, priority: Int, orderby: Int): Listing<Todo> {
        val params =
            mapOf("status" to status, "type" to type, "priority" to priority, "orderby" to orderby)
        val callback = TodoPageBoundaryCallback(
            ioExecutor = ioExecutor,
            handleResponse = this::handleResponse
        ) {
            DefaultRetrofitClient.getService().getTodos(it, params)
        }

        val types = if (type == 0) {
            listOf(TodoType.OTHER.type, TodoType.WORK.type, TodoType.LIFE.type, TodoType.STUDY.type)
        } else {
            listOf(type)
        }
        val prioritys = if (priority == 0) {
            listOf(
                TodoPriority.SERIOUS.priority,
                TodoPriority.IMPORTANT.priority,
                TodoPriority.NORMAL.priority,
                TodoPriority.NONE.priority
            )
        } else {
            listOf(priority)
        }

        val sourceFactory = when (orderby) {
            TODO_ORDERBY_FINISH_DATE -> localDataSource.getTodoDao().getTodoDataSourceByFinishDate(
                status,
                types,
                prioritys
            )
            TODO_ORDERBY_FINISH_DATE_REVERSE -> localDataSource.getTodoDao()
                .getTodoDataSourceByFinishDateReverse(
                    status,
                    types,
                    prioritys
                )
            TODO_ORDERBY_CREATE_DATE -> localDataSource.getTodoDao().getTodoDataSourceByCreateDate(
                status,
                types,
                prioritys
            )
            else -> localDataSource.getTodoDao()
                .getTodoDataSourceByCreateDateReverse(status, types, prioritys)
        }
        val livePagedList = getPagedListLiveData(sourceFactory, callback)

        return Listing(
            pagedList = livePagedList,
            refreshState = callback.refreshState,
            loadMoreState = callback.loadState,
            refresh = { callback.refresh() },
            retry = { callback.retryAllFailed() }
        )
    }

    private suspend fun handleResponse(datas: List<Todo>) {
        localDataSource.getTodoDao().saveTodos(datas)
    }

    suspend fun deleteTodo(id: Int, deleteTodoLiveData: StateLiveData<Any>) {
        withContext(ioDispatcher) {
            deleteTodoLiveData.postLoading()
            remoteDataSource.deleteTodo(id, object : DefaultHttpCallback<Any?>(sErrorCodeHandler) {
                override fun onSuccess(data: Any?) {
                    deleteTodoLiveData.postSuccess(Any())
                    //删除成功后从本地数据库中删除
                    launchIO { localDataSource.getTodoDao().deleteTodo(id) }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    deleteTodoLiveData.postFailure(message)
                }
            })
        }
    }

    suspend fun addTodo(
        title: String,
        content: String,
        date: String,
        type: Int,
        priority: Int,
        addTodoLiveData: StateLiveData<Todo>
    ) {
        withContext(ioDispatcher) {
            addTodoLiveData.postLoading()
            val params = mapOf(
                "title" to title,
                "content" to content,
                "date" to date,
                "type" to type,
                "priority" to priority
            )
            remoteDataSource.addTodo(params, object : DefaultHttpCallback<Todo>(sErrorCodeHandler) {
                override fun onSuccess(data: Todo?) {
                    data?.let {
                        addTodoLiveData.postSuccess(it)
                        launchIO { handleResponse(listOf(it)) }
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    addTodoLiveData.postFailure(message)
                }
            })
        }
    }

    suspend fun updateTodo(
        id: Int,
        title: String,
        content: String,
        date: String,
        type: Int,
        priority: Int,
        status: Int,
        updateTodoLiveData: StateLiveData<Todo>
    ) {
        withContext(ioDispatcher) {
            updateTodoLiveData.postLoading()
            val params = mapOf(
                "title" to title,
                "content" to content,
                "date" to date,
                "type" to type,
                "priority" to priority,
                "status" to status
            )
            remoteDataSource.updateTodo(id, params, object : DefaultHttpCallback<Todo>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: Todo?) {
                    data?.let {
                        updateTodoLiveData.postSuccess(it)
                        launchIO { handleResponse(listOf(it)) }
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    updateTodoLiveData.postFailure(message)
                }
            })
        }
    }

    suspend fun updateTodoStatus(
        id: Int,
        status: Int,
        updateTodoStatusLiveData: StateLiveData<Todo>
    ) {
        withContext(ioDispatcher) {
            updateTodoStatusLiveData.postLoading()
            remoteDataSource.updateTodoStatus(id, status, object : DefaultHttpCallback<Todo>(
                sErrorCodeHandler
            ) {
                override fun onSuccess(data: Todo?) {
                    data?.let {
                        updateTodoStatusLiveData.postSuccess(it)
                        launchIO { handleResponse(listOf(it)) }
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    updateTodoStatusLiveData.postFailure(message)
                }
            })
        }
    }


}
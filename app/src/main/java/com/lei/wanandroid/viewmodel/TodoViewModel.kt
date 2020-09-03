package com.lei.wanandroid.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ActivityUtils
import com.lei.wanandroid.data.bean.Todo
import com.lei.wanandroid.data.repository.base.RepositoryProviders
import com.lei.wanandroid.jetpack.livedata.StateLiveData
import com.lei.wanandroid.jetpack.paging.Listing
import com.lei.wanandroid.ui.activitys.LoginActivity
import com.lei.wanandroid.ui.fragments.todo.TODO_ORDERBY_CREATE_DATE_REVERSE
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : BaseViewModel(application) {
    private val repository = RepositoryProviders.provideTodoRepository()

    val loginUser = repository.loginUserLiveData
    val currentTodoLiveData = repository.currentTodoLiveData

    fun toLoginActivity() {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }

    private val todoListing by lazy { MutableLiveData<Listing<Todo>>() }
    val todoPagedList = todoListing.switchMap { it.pagedList }
    val todoRefreshState = todoListing.switchMap { it.refreshState }
    val todoLoadMoreState = todoListing.switchMap { it.loadMoreState }

    fun initTodos(
        status: Int,
        type: Int = 0,
        priority: Int = 0,
        orderby: Int = TODO_ORDERBY_CREATE_DATE_REVERSE
    ) {
        todoListing.value = repository.getTodos(status, type, priority, orderby)
    }

    fun refreshTodos() {
        todoListing.value?.refresh?.invoke()
    }

    fun retryTodos() {
        todoListing.value?.retry?.invoke()
    }

    //删除todo
    val deleteTodoLiveData by lazy { StateLiveData<Any>() }
    fun deleteTodo(id: Int) {
        viewModelScope.launch { repository.deleteTodo(id, deleteTodoLiveData) }
    }

    //新增todo
    val addTodoLiveData by lazy { StateLiveData<Todo>() }
    fun addTodo(title: String, content: String, date: String, type: Int, priority: Int) {
        viewModelScope.launch {
            repository.addTodo(
                title,
                content,
                date,
                type,
                priority,
                addTodoLiveData
            )
        }
    }

    //更新todo
    val updateTodoLiveData by lazy { StateLiveData<Todo>() }
    fun updateTodo(
        id: Int,
        title: String,
        content: String,
        date: String,
        type: Int,
        priority: Int,
        status: Int
    ) {
        viewModelScope.launch {
            repository.updateTodo(
                id,
                title,
                content,
                date,
                type,
                priority,
                status,
                updateTodoLiveData
            )
        }
    }

    //更新todo status
    val updateTodoStatusLiveData by lazy { StateLiveData<Todo>() }
    fun updateTodoStatus(id: Int, status: Int) {
        viewModelScope.launch { repository.updateTodoStatus(id, status, updateTodoStatusLiveData) }
    }
}
package com.lei.wanandroid.ui.activitys

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.lei.wanandroid.R
import com.lei.wanandroid.base.BaseActivity
import com.lei.wanandroid.data.bean.Todo
import com.lei.wanandroid.databinding.ActivityTodoDetailBinding
import com.lei.wanandroid.jetpack.livedata.IStateCallback
import com.lei.wanandroid.jetpack.livedata.StateOberver
import com.lei.wanandroid.ui.fragments.todo.TODO_STATUS_FINISH
import com.lei.wanandroid.ui.fragments.todo.TODO_STATUS_UNFINISH
import com.lei.wanandroid.ui.fragments.todo.TodoPriority
import com.lei.wanandroid.ui.fragments.todo.TodoType
import com.lei.wanandroid.ui.helper.setViewsEnabledState
import com.lei.wanandroid.util.*
import com.lei.wanandroid.viewmodel.TodoViewModel
import java.util.*

class TodoDetailActivity : BaseActivity<TodoViewModel, ActivityTodoDetailBinding>() {
    private var todo: Todo? = null
    private var isEdit = false

    override fun getLayoutId(): Int {
        return R.layout.activity_todo_detail
    }

    override fun initView(savedInstanceState: Bundle?) {
        todo = viewModel.currentTodoLiveData.value
        initToolbar(mBinding.toolbar)
        initContent()
    }

    private fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { this.onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.id_menu_delete -> showDeleteDialog()
                R.id.id_menu_edit -> ActivityUtils.startActivity(TodoDetailActivity::class.java)
                R.id.id_menu_save -> saveTodo()
            }
            true
        }
    }

    private fun saveTodo() {
        if (mBinding.etTitle.text.isNullOrBlank()) {
            showGravityShortToast("标题不能为空", Gravity.CENTER)
        } else if (todo == null) {
            viewModel.addTodo(
                mBinding.etTitle.text.toString(),
                mBinding.etContent.text.toString(),
                getDateByMillis(
                    parseDateToMillis(mBinding.tvDate.text.toString(), sdf1.get()!!),
                    sdf2.get()!!
                ),
                getType(),
                getPriority()
            )
        } else if (!isEditTodoChanged(todo!!)) {
            showGravityShortToast("该条便签未做任何修改哦", Gravity.CENTER)
        } else if (isJustModifyTodoStatus(todo!!)) {
            viewModel.updateTodoStatus(
                todo!!.id, if (mBinding.finishSwitch.isChecked) {
                    TODO_STATUS_FINISH
                } else {
                    TODO_STATUS_UNFINISH
                }
            )
        } else {
            viewModel.updateTodo(
                todo!!.id,
                mBinding.etTitle.text.toString(),
                mBinding.etContent.text.toString(),
                getDateByMillis(
                    parseDateToMillis(mBinding.tvDate.text.toString(), sdf1.get()!!),
                    sdf2.get()!!
                ),
                getType(),
                getPriority(),
                if (mBinding.finishSwitch.isChecked) {
                    TODO_STATUS_FINISH
                } else {
                    TODO_STATUS_UNFINISH
                }
            )
        }
    }

    /**
     * 判断编辑的todo是否有被修改的内容
     */
    private fun isEditTodoChanged(srcTodo: Todo): Boolean = srcTodo.date != parseDateToMillis(
        mBinding.tvDate.text.toString(),
        sdf1.get()!!
    ) || srcTodo.status != if (mBinding.finishSwitch.isChecked) {
        TODO_STATUS_FINISH
    } else {
        TODO_STATUS_UNFINISH
    } || srcTodo.title != mBinding.etTitle.text.toString()
            || srcTodo.content != mBinding.etContent.text.toString()
            || srcTodo.type != getType()
            || srcTodo.priority != getPriority()

    /**
     * 被编辑的todo是否只修改了状态
     */
    private fun isJustModifyTodoStatus(srcTodo: Todo): Boolean = srcTodo.date == parseDateToMillis(
        mBinding.tvDate.text.toString(),
        sdf1.get()!!
    ) && srcTodo.title == mBinding.etTitle.text.toString()
            && srcTodo.content == mBinding.etContent.text.toString()
            && srcTodo.type == getType()
            && srcTodo.priority == getPriority()
            && srcTodo.status != if (mBinding.finishSwitch.isChecked) {
        TODO_STATUS_FINISH
    } else {
        TODO_STATUS_UNFINISH
    }

    private fun showDeleteDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_delete_todo)
            positiveButton(R.string.dialog_sure) { viewModel.deleteTodo(todo!!.id) }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@TodoDetailActivity)
        }
    }

    private fun showDatePickerDialog() {
        MaterialDialog(this).show {
            datePicker(currentDate = Calendar.getInstance().apply {
                timeInMillis = parseDateToMillis(mBinding.tvDate.text.toString(), sdf1.get()!!)
            }) { _, datetime ->
                mBinding.tvDate.text = getDateByMillis(datetime.timeInMillis, sdf1.get()!!)
            }
            lifecycleOwner(this@TodoDetailActivity)
        }
    }

    private fun initContent() {
        mBinding.tvDate.setOnClickListener { showDatePickerDialog() }
        if (todo != null) {
            val todoInner = todo!!
            setViewsEnabledState(mBinding.llRoot, false, listOf(Toolbar::class.java))
            mBinding.etTitle.setText(todoInner.title)
            mBinding.etContent.setText(todoInner.content)
            mBinding.tvDate.text = getDateByMillis(todoInner.date, sdf1.get()!!)
            when (todoInner.priority) {
                TodoPriority.NORMAL.priority -> mBinding.rbtnPriorityNormal.isChecked = true
                TodoPriority.IMPORTANT.priority -> mBinding.rbtnPriorityImportant.isChecked = true
                TodoPriority.SERIOUS.priority -> mBinding.rbtnPrioritySerious.isChecked = true
            }
            when (todoInner.type) {
                TodoType.STUDY.type -> mBinding.rbtnTypeStudy.isChecked = true
                TodoType.LIFE.type -> mBinding.rbtnTypeLife.isChecked = true
                TodoType.WORK.type -> mBinding.rbtnTypeWork.isChecked = true
            }
            with(mBinding.finishSwitch) {
                setOnCheckedChangeListener { _, isChecked ->
                    text = if (isChecked) {
                        getString(R.string.todo_finish)
                    } else {
                        getString(R.string.todo_unfinish)
                    }
                }
                isChecked = todoInner.status == TODO_STATUS_FINISH
                visibility = View.VISIBLE
            }
        } else {
            mBinding.rbtnPriorityNormal.isChecked = true
            mBinding.rbtnTypeStudy.isChecked = true
            mBinding.tvDate.text = getCurrentDate(sdf1.get()!!)
        }
    }

    override fun initData() {
        viewModel.deleteTodoLiveData.observe(this, StateOberver(object : IStateCallback<Any> {
            override fun onSuccess(value: Any) {
                hideLoading(mBinding.progressBar)
                showShortToast("删除便签成功")
                finish()
            }

            override fun onLoading() {
                showLoading(mBinding.progressBar)
            }

            override fun onFailure(message: String) {
                hideLoading(mBinding.progressBar)
                showShortToast(message)
            }
        }))
        viewModel.addTodoLiveData.observe(this, StateOberver(object : IStateCallback<Todo> {
            override fun onSuccess(value: Todo) {
                hideLoading(mBinding.progressBar)
                showShortToast("添加便签成功")
                finish()
            }

            override fun onLoading() {
                showLoading(mBinding.progressBar)
            }

            override fun onFailure(message: String) {
                hideLoading(mBinding.progressBar)
                showShortToast(message)
            }
        }))
        viewModel.updateTodoLiveData.observe(this, StateOberver(object : IStateCallback<Todo> {
            override fun onSuccess(value: Todo) {
                hideLoading(mBinding.progressBar)
                showShortToast("更新便签成功")
                finish()
            }

            override fun onLoading() {
                showLoading(mBinding.progressBar)
            }

            override fun onFailure(message: String) {
                hideLoading(mBinding.progressBar)
                showShortToast(message)
            }
        }))
        viewModel.updateTodoStatusLiveData.observe(
            this,
            StateOberver(object : IStateCallback<Todo> {
                override fun onSuccess(value: Todo) {
                    hideLoading(mBinding.progressBar)
                    showShortToast("修改便签状态成功")
                    finish()
                }

                override fun onLoading() {
                    showLoading(mBinding.progressBar)
                }

                override fun onFailure(message: String) {
                    hideLoading(mBinding.progressBar)
                    showShortToast(message)
                }
            })
        )
    }

    override fun provideViewModel(): TodoViewModel {
        return ViewModelProviders.of(this).get(TodoViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (todo == null || isEdit) {
            menuInflater.inflate(R.menu.menu_save, menu)
        } else {
            menuInflater.inflate(R.menu.menu_edit, menu)
        }
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        isEdit = true
        invalidateOptionsMenu()
        setViewsEnabledState(mBinding.llRoot, true)
    }

    override fun onBackPressed() {
        if (isEdit) {
            showExitEditDialog()
        } else if (todo == null && (mBinding.etTitle.text.toString()
                .isNotBlank() || mBinding.etContent.text.toString().isNotBlank())
        ) {
            showExitAddDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showExitAddDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_exit_add_todo)
            positiveButton(R.string.dialog_sure) {
                finish()
            }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@TodoDetailActivity)
        }
    }

    private fun showExitEditDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_exit_edit_todo)
            positiveButton(R.string.dialog_sure) {
                finish()
            }
            negativeButton(R.string.dialog_cancel)
            lifecycleOwner(this@TodoDetailActivity)
        }
    }

    private fun getType(): Int = when {
        mBinding.rbtnTypeStudy.isChecked -> {
            TodoType.STUDY.type
        }
        mBinding.rbtnTypeLife.isChecked -> {
            TodoType.LIFE.type
        }
        mBinding.rbtnTypeWork.isChecked -> {
            TodoType.WORK.type
        }
        else -> {
            TodoType.OTHER.type
        }
    }

    private fun getPriority(): Int = when {
        mBinding.rbtnPriorityNormal.isChecked -> {
            TodoPriority.NORMAL.priority
        }
        mBinding.rbtnPriorityImportant.isChecked -> {
            TodoPriority.IMPORTANT.priority
        }
        mBinding.rbtnPrioritySerious.isChecked -> {
            TodoPriority.SERIOUS.priority
        }
        else -> {
            TodoPriority.NONE.priority
        }
    }

}
package com.lei.wanandroid.ui.fragments.todo

import com.blankj.utilcode.util.Utils
import com.lei.wanandroid.R

//---------------------- 获取TODO列表时的状态 ----------------------
const val TODO_STATUS_UNFINISH = 0               //未完成
const val TODO_STATUS_FINISH = 1                 //完成

//---------------------- 获取TODO列表时的顺序 ----------------------
const val TODO_ORDERBY_FINISH_DATE = 1           //完成日期顺序
const val TODO_ORDERBY_FINISH_DATE_REVERSE = 2   //完成日期逆序
const val TODO_ORDERBY_CREATE_DATE = 3           //创建日期顺序
const val TODO_ORDERBY_CREATE_DATE_REVERSE = 4   //创建日期逆序（默认）

@Suppress("DEPRECATION")
enum class TodoType(val type: Int, val des: String, val color: Int) {
    OTHER(
        0,
        Utils.getApp().getString(R.string.todo_type_other),
        Utils.getApp().resources.getColor(R.color.color_todo_type_other)
    ),
    STUDY(
        1,
        Utils.getApp().getString(R.string.todo_type_study),
        Utils.getApp().resources.getColor(R.color.color_todo_type_study)
    ),
    WORK(
        2,
        Utils.getApp().getString(R.string.todo_type_work),
        Utils.getApp().resources.getColor(R.color.color_todo_type_work)
    ),
    LIFE(
        3,
        Utils.getApp().getString(R.string.todo_type_life),
        Utils.getApp().resources.getColor(R.color.color_todo_type_life)
    )
}

enum class TodoPriority(val priority: Int, val des: String) {
    NONE(0, Utils.getApp().getString(R.string.todo_priority_none)),
    NORMAL(1, Utils.getApp().getString(R.string.todo_priority_normal)),
    IMPORTANT(2, Utils.getApp().getString(R.string.todo_priority_important)),
    SERIOUS(3, Utils.getApp().getString(R.string.todo_priority_serious))
}

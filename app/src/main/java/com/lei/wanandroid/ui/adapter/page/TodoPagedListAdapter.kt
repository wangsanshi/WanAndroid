package com.lei.wanandroid.ui.adapter.page

import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.DiffUtil
import com.haozhang.lib.SlantedTextView
import com.lei.wanandroid.R
import com.lei.wanandroid.data.bean.Todo
import com.lei.wanandroid.ui.adapter.base.BasePagedListAdapter
import com.lei.wanandroid.ui.adapter.base.BaseViewHolder
import com.lei.wanandroid.ui.fragments.todo.TodoType

class TodoPagedListAdapter :
    BasePagedListAdapter<Todo, BaseViewHolder>(R.layout.item_todo,
        itemCallback
    ) {

    override fun convert(holder: BaseViewHolder, item: Todo?, position: Int) {
        item?.let { bindTodo(it, holder) }
    }

    private fun bindTodo(data: Todo, holder: BaseViewHolder) {
        holder.setText(R.id.tvTodoTitle, data.title)
        holder.setText(R.id.tvTodoContent, data.content)
        holder.getView<AppCompatRatingBar>(R.id.rbPriority).progress = data.priority
        holder.setText(R.id.tvCreateDate, data.dateStr)
        holder.setText(
            R.id.tvFinishDate, if (data.completeDateStr.isBlank()) {
                "未完成"
            } else {
                data.completeDateStr
            }
        )
        val slantedTextView = holder.getView<SlantedTextView>(R.id.stvLabel)
        val todoType = when (data.type) {
            TodoType.STUDY.type -> TodoType.STUDY
            TodoType.WORK.type -> TodoType.WORK
            TodoType.LIFE.type -> TodoType.LIFE
            else -> TodoType.OTHER
        }
        slantedTextView.text = todoType.des
        slantedTextView.setSlantedBackgroundColor(todoType.color)
    }

    companion object {
        val itemCallback = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.title == newItem.title && oldItem.content == newItem.content
                        && oldItem.date == newItem.date && oldItem.completeDate == newItem.completeDate
                        && oldItem.type == newItem.type && oldItem.priority == newItem.priority
            }
        }
    }

}
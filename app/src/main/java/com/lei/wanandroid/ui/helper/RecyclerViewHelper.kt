package com.lei.wanandroid.ui.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.lei.wanandroid.R
import kotlin.math.round

class DealExceptionLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

class ContainerView(context: Context, attr: AttributeSet? = null, defaultStyle: Int = 0) :
    FrameLayout(context, attr, defaultStyle) {

    companion object {
        private const val TAG = "ContainerView"
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    private var layoutParams = createLayoutParams()

    var emptyView: View? = null
        set(value) {
            if (field != value) {
                field?.let { removeView(it) }
                value?.let {
                    it.visibility = View.GONE
                    addView(it, layoutParams)
                }
                field = value
            } else {
                Log.w(TAG, "the empty view already set.")
            }
        }
    var errorView: View? = null
        set(value) {
            if (field != value) {
                field?.let { removeView(it) }
                value?.let {
                    it.visibility = View.GONE
                    addView(it, layoutParams)
                }
                field = value
            } else {
                Log.w(TAG, "the error view already set.")
            }
        }

    init {
        val t = context.obtainStyledAttributes(attr, R.styleable.ContainerView, defaultStyle, 0)
        val emptyViewId = t.getResourceId(R.styleable.ContainerView_layout_empty, 0)
        val errorViewId = t.getResourceId(R.styleable.ContainerView_layout_error, 0)
        t.recycle()

        if (emptyViewId != 0) {
            emptyView = LayoutInflater.from(context).inflate(emptyViewId, this, false)
        }
        if (errorViewId != 0) {
            errorView = LayoutInflater.from(context).inflate(errorViewId, this, false)
        }
    }

    private fun createLayoutParams(): LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
    }

    fun showEmpty() {
        forEach {
            if (it == emptyView) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }

    fun showError() {
        forEach {
            if (it == errorView) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }

    fun showContent() {
        forEach {
            if (it == emptyView || it == errorView) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
            }
        }
    }

}

class VeriticalItemDecoration(builder: Builder) : RecyclerView.ItemDecoration() {
    private val hasFirstDivider = builder.hasFirstDivider
    private val hasLastDivider = builder.hasLastDivider
    private val startColor = builder.startColor
    private val endColor = builder.endColor
    private val colorOrientation = builder.colorOrientation
    private val marginLeft = builder.marginLeft
    private val marginRight = builder.marginRight
    private val height = builder.height

    private val outBounds = Rect()

    private val dividerDrawable =
        GradientDrawable(colorOrientation, intArrayOf(startColor, endColor))

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (height > 0) {
            outRect.bottom = height
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager
        if (layoutManager != null
            && layoutManager is LinearLayoutManager
            && layoutManager.orientation == RecyclerView.VERTICAL
            && height > 0
            && parent.childCount > 0
        ) {
            drawDivider(c, parent)
        }
    }

    private fun drawDivider(c: Canvas, parent: RecyclerView) {
        c.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft + marginLeft
            right = parent.width - parent.paddingRight - marginRight
        } else {
            left = marginLeft
            right = parent.width - marginRight
        }
        var startIndex = 0
        var endIndex = parent.childCount - 1
        if (!hasFirstDivider) {
            startIndex++
        }
        if (!hasLastDivider) {
            endIndex--
        }
        for (index in startIndex..endIndex) {
            val childView = parent.getChildAt(index)
            parent.getDecoratedBoundsWithMargins(childView, outBounds)
            val bottom = outBounds.bottom + round(childView.translationY)
            val top = bottom - height
            dividerDrawable.setBounds(left, top.toInt(), right, bottom.toInt())
            dividerDrawable.draw(c)
        }
        c.restore()
    }

    class Builder {
        var hasFirstDivider = true
        var hasLastDivider = true
        var startColor = DEFAULT_COLOR
        var endColor = DEFAULT_COLOR
        var colorOrientation = GradientDrawable.Orientation.LEFT_RIGHT
        var marginLeft = 0
        var marginRight = 0
        var height = DEFAULT_HEIGHT

        fun setStartColor(@ColorInt color: Int): Builder {
            startColor = color
            return this
        }

        fun setEndColor(@ColorInt color: Int): Builder {
            endColor = color
            return this
        }

        fun setMarginLeft(@Px marginLeft: Int): Builder {
            this.marginLeft = marginLeft
            return this
        }

        fun setMarginRight(@Px marginRight: Int): Builder {
            this.marginRight = marginRight
            return this
        }

        fun setHeight(@Px height: Int): Builder {
            this.height = height
            return this
        }

        fun hasFirstDivider(hasFirstDivider: Boolean): Builder {
            this.hasFirstDivider = hasFirstDivider
            return this
        }

        fun hasLastDivider(hasLastDivider: Boolean): Builder {
            this.hasLastDivider = hasLastDivider
            return this
        }

        fun setColorOrientation(orientation: GradientDrawable.Orientation): Builder {
            this.colorOrientation = orientation
            return this
        }

        fun build(): VeriticalItemDecoration {
            return VeriticalItemDecoration(this)
        }
    }

    companion object {
        private val DEFAULT_COLOR = Color.parseColor("#DDDDDD")
        private const val DEFAULT_HEIGHT = 2
    }
}

class FirstPageRecyclerViewDecoration : RecyclerView.ItemDecoration() {

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter
        if (adapter != null && adapter is MergeAdapter) {
            val position = parent.getChildAdapterPosition(view)
            val itemType = adapter.getItemViewType(position)
            if (itemType == R.layout.header_top_articles) {
                outRect.top = 0
            } else if (itemType == R.layout.item_top_article) {
                outRect.top = TOP_ARTICLE_DECORATION_HEIGHT
            } else {
                outRect.top = NORMAL_ARTICLE_DECORATION_HEIGHT
            }
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val adapter = parent.adapter
        if (adapter != null && adapter is MergeAdapter) {
            val childCount = parent.childCount
            for (index in 0 until childCount) {
                val childView = parent.getChildAt(index)
                val position = parent.getChildAdapterPosition(childView)
                if (position == 0) {
                    continue
                }
                when (adapter.getItemViewType(position)) {
                    R.layout.item_top_article -> {
                        val top = childView.top - TOP_ARTICLE_DECORATION_HEIGHT
                        val bottom = childView.top
                        mPaint.setColor(Color.parseColor("#DDDDDD"))
                        c.drawRect(
                            childView.left.toFloat(),
                            top.toFloat(),
                            childView.right.toFloat(),
                            bottom.toFloat(),
                            mPaint
                        )
                    }
                    R.layout.item_article -> {
                        val top = childView.top - NORMAL_ARTICLE_DECORATION_HEIGHT
                        val bottom = childView.top
                        mPaint.setColor(Color.TRANSPARENT)
                        c.drawRect(
                            childView.left.toFloat(),
                            top.toFloat(),
                            childView.right.toFloat(),
                            bottom.toFloat(),
                            mPaint
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TOP_ARTICLE_DECORATION_HEIGHT = 1
        private val NORMAL_ARTICLE_DECORATION_HEIGHT = SizeUtils.dp2px(8.toFloat())
    }
}

fun getTransparentItemDecoration(): VeriticalItemDecoration {
    return VeriticalItemDecoration.Builder()
        .setHeight(SizeUtils.dp2px(8.toFloat()))
        .setStartColor(Color.TRANSPARENT)
        .setEndColor(Color.TRANSPARENT)
        .build()
}

fun getLineItemDecoration(): VeriticalItemDecoration {
    return VeriticalItemDecoration.Builder()
        .setHeight(1)
        .setStartColor(Color.parseColor("#99DDDDDD"))
        .setEndColor(Color.parseColor("#99DDDDDD"))
        .build()
}


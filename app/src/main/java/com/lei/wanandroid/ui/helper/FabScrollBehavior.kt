package com.lei.wanandroid.ui.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lei.wanandroid.R


class FabScrollBehavior(context: Context, attrs: AttributeSet) :
    FloatingActionButton.Behavior(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )

        if (dyConsumed > 0) {
            //向上滑动
            animateOut(child)
        } else if (dyConsumed < 0) {
            //向下滑动
            animateIn(child)
        }
    }

    private var isAnimate = false

    private fun animateOut(fab: FloatingActionButton) {
        if (!isAnimate) {
            fab.animate().scaleX(0.0f).scaleY(0.0f).setInterpolator(LinearInterpolator())
                .setDuration(
                    fab.context.applicationContext.resources.getInteger(R.integer.fab_anim_duration)
                        .toLong()
                )
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        isAnimate = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        isAnimate = false
                    }
                }).start()
        }
    }

    private fun animateIn(fab: FloatingActionButton) {
        if (!isAnimate) {
            fab.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(LinearInterpolator())
                .setDuration(
                    fab.context.applicationContext.resources.getInteger(R.integer.fab_anim_duration)
                        .toLong()
                )
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        isAnimate = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        isAnimate = false
                    }
                }).start()
        }
    }
}
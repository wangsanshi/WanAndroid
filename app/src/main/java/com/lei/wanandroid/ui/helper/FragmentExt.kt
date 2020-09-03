package com.lei.wanandroid.ui.helper

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun switchFragment(
    fragmentClass: Class<*>,
    manager: FragmentManager,
    lastShowFragmentTag: String?,
    @IdRes containerViewId: Int
): String {
    val tag = fragmentClass.simpleName
    var fragment = manager.findFragmentByTag(tag)
    val transaction = manager.beginTransaction()
    if (fragment == null) {
        fragment = fragmentClass.newInstance() as Fragment?
        transaction.add(containerViewId, fragment!!, tag)
    }
    lastShowFragmentTag?.let {
        val lastShorFragment = manager.findFragmentByTag(it)
        lastShorFragment?.let { fragment -> transaction.hide(fragment) }
    }
    transaction.show(fragment).commitAllowingStateLoss()
    return tag
}
package com.lei.xpermission

import androidx.fragment.app.FragmentActivity

object XPermission {
    private const val TAG_FRAGMENT = "tag_fragment"

    fun request(
        activity: FragmentActivity,
        vararg permission: String,
        callback: PermissionCallback
    ) {
        val manager = activity.supportFragmentManager
        val existedFragment = manager.findFragmentByTag(TAG_FRAGMENT)
        val fragment = if (existedFragment != null) {
            existedFragment as PermissionFragment
        } else {
            val permissionFragment = PermissionFragment.newInstance()
            manager.beginTransaction().add(permissionFragment, TAG_FRAGMENT).commitNow()
            permissionFragment
        }

        fragment.request(callback, *permission)
    }
}
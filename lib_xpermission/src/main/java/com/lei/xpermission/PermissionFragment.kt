package com.lei.xpermission

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

typealias PermissionCallback = (Boolean, List<String>) -> Unit

private const val REQUEST_CODE = 1

class PermissionFragment : Fragment() {
    private var mCallback: PermissionCallback? = null

    fun request(callback: PermissionCallback, vararg permission: String) {
        mCallback = callback
        requestPermissions(permission, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val denyList = mutableListOf<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    denyList.add(permissions[index])
                }
            }
            mCallback?.let { it(denyList.isEmpty(), denyList) }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PermissionFragment()
    }
}
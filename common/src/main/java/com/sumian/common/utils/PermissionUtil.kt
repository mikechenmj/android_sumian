package com.sumian.common.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

object PermissionUtil {
    val mPerms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    else {
        arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun hasBluetoothPermissions(context: Context): Boolean {
        return EasyPermissions.hasPermissions(context, *mPerms)
    }

    fun isForbidPermissionPopup(fragment: Fragment): Boolean {
        for (permission in mPerms) {
            if (ActivityCompat.checkSelfPermission(fragment.activity!!, permission) != PackageManager.PERMISSION_GRANTED
                    && !fragment.shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }

    fun shouldShowRequestPermissionRationale(fragment: Fragment): Boolean {
        for (permission in mPerms) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }

    fun requestPermissions(fragment: Fragment, code: Int) {
        fragment.requestPermissions(mPerms, code)
    }

    fun showScanPermissionDetail(fragment: Fragment, intent: Intent, code: Int, needResult: Boolean = true) {
        if (needResult) {
            fragment.startActivityForResult(intent, code)
        } else {
            fragment.startActivity(intent)
        }
    }

    fun startSettingPermission(fragment: Fragment): Boolean {
        if (fragment.activity == null) {
            return false
        }
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
        intent.data = Uri.parse("package:${fragment.activity!!.packageName}")
        if (fragment.activity!!.packageManager != null && fragment.activity!!.packageManager.queryIntentActivities(intent, 0).size > 0) {
            fragment.activity!!.startActivity(intent)
            return true
        }
        return false
    }
}
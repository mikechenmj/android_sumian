package com.sumian.devicedemo.device

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.data.DeviceType
import com.sumian.device.data.DeviceVersionInfo
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.DfuCallback
import com.sumian.device.util.MediaUtility
import com.sumian.device.util.VersionUtil
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_upgrade_device.*
import java.io.File

class UpgradeDeviceActivity : BaseActivity() {
    private var mVersionInfo: DeviceVersionInfo? = null
    private var mUpgradeFile: File? = null

    companion object {
        const val REQUEST_UPGRADE = 1993
        private const val KEY_TYPE = "KEY_TYPE"
        const val TYPE_MONITOR = 0
        const val TYPE_SLEEP_MASTER = 1

        /**
         * @param TYPE_MONITOR, TYPE_SLEEP_MASTER
         */
        fun launch(activity: Activity, type: Int) {
            val intent = Intent(activity, UpgradeDeviceActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            activity.startActivity(intent)
        }
    }

    private fun getType(): Int {
        return intent.getIntExtra(
                KEY_TYPE,
                TYPE_MONITOR
        )
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_upgrade_device
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queryVersionInfo()

        bt_download.setOnClickListener { downloadUpgradeFile() }
        bt_upgrade.setOnClickListener {
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, REQUEST_UPGRADE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_UPGRADE -> {
                    var uri = data?.data
                    var file = File(MediaUtility.getPath(this, uri))
                    if (file.exists() && file.isFile) {
                        upgrade(file)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun downloadUpgradeFile() {
        val latestVersionInfo = getLatestVersionInfo()!!
        val dir = getDir("upgrade", 0)
        val file = File(dir, latestVersionInfo.version + "zip")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("固件下载中：")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.max = 100
        progressDialog.progress = 0
        progressDialog.show()
        mUpgradeFile = file
        FileDownloader.setup(this)
        FileDownloader.getImpl().create(latestVersionInfo.url)
                .setPath(file.path)
                .setListener(object : FileDownloadListener() {
                    override fun warn(task: BaseDownloadTask?) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        progressDialog.dismiss()
                        val fileMD5 = FileUtils.getFileMD5ToString(file)
                        if (!fileMD5.equals(getLatestVersionInfo()?.md5, true)) {
                            ToastUtils.showShort("文件损坏，请重新下载")
                            return
                        }
                        bt_download.isVisible = false
                        upgrade(file)
                    }

                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        progressDialog.dismiss()
                        ToastUtils.showShort(e?.message)
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        progressDialog.progress = soFarBytes
                        progressDialog.max = totalBytes
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                })
                .start()

    }

    private fun upgrade(file: File?) {
        if (file == null) {
            return
        }
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("固件升级中：")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.max = 100
        progressDialog.progress = 0
        progressDialog.show()
        DeviceManager.upgrade(
                if (getType() == TYPE_MONITOR) DeviceType.MONITOR else DeviceType.SLEEP_MASTER,
                file.absolutePath,
                object : DfuCallback {
                    override fun onStart() {
                    }

                    override fun onProgressChange(progress: Int) {
                        progressDialog.progress = progress
                    }

                    override fun onSuccess() {
                        ToastUtils.showShort("升级成功")
                        progressDialog.dismiss()
                        iv_top.setImageResource(R.drawable.ic_upgrade_device_success)
                        tv_latest_version.text = getString(R.string.already_latest_version)
                    }

                    override fun onFail(code: Int, msg: String?) {
                        ToastUtils.showShort("升级成功失败：$msg")
                        progressDialog.dismiss()
                    }
                })
    }

    private fun queryVersionInfo() {
        DeviceManager.getLatestVersionInfo(object : AsyncCallback<DeviceVersionInfo> {
            override fun onSuccess(data: DeviceVersionInfo?) {
                if (data == null) return
                mVersionInfo = data
                updateUI()
            }

            override fun onFail(code: Int, msg: String) {
                ToastUtils.showShort(msg)
            }
        })
    }

    private fun updateUI() {
        tv_current_version.text = getString(
                R.string.current_version_s, getCurrentVersion()
        )
        val latestVersion = getLatestVersionInfo()?.version
        if (latestVersion != null) {
            tv_latest_version.text = getString(
                    R.string.latest_version_s,
                    latestVersion
            )
        } else {
            tv_latest_version.text = getString(R.string.already_latest_version)
        }
        bt_download.isVisible = hasNewVersion()
    }

    private fun hasNewVersion(): Boolean {
        return VersionUtil.hasNewVersion(getLatestVersionInfo()?.version, getCurrentVersion())
    }

    private fun getCurrentVersion(): String? {
        return if (getType() == TYPE_MONITOR) {
            DeviceManager.getDevice()?.monitorVersionInfo?.softwareVersion
        } else {
            DeviceManager.getDevice()?.sleepMasterVersionInfo?.softwareVersion
        }
    }

    private fun getLatestVersionInfo(): DeviceVersionInfo.VersionInfo? {
        return if (getType() == TYPE_MONITOR) {
            mVersionInfo?.monitor
        } else {
            mVersionInfo?.sleeper
        }
    }
}

package com.sumian.sd.account.achievement.presenter

import android.app.Activity
import android.os.Environment
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import com.sumian.common.utils.ViewToImageFileListener
import com.sumian.common.utils.viewToImageFile
import com.sumian.sd.account.achievement.contract.MyAchievementShareContract
import com.sumian.sd.app.AppManager
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import java.io.File

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
class MyAchievementSharePresenter private constructor() : MyAchievementShareContract.Presenter {

    companion object {
        private const val TAG = "MyAchievementSharePresenter"
        @JvmStatic
        fun create(): MyAchievementShareContract.Presenter = MyAchievementSharePresenter()
    }

    override fun share(activity: Activity, shareType: SHARE_MEDIA, shareView: ViewGroup, umShareListener: UMShareListener?) {
        val drawToBitmap = shareView.drawToBitmap()
        AppManager.getOpenEngine().shareImage(activity, drawToBitmap, shareType, umShareListener)

    }

    override fun saveShareView(shareView: ViewGroup, listener: ViewToImageFileListener) {
        viewToImageFile(shareView,
                createSaveFile(),
                50, listener)
    }

    private fun createSaveFile(dir: String = "sumian", mediaFileName: String = "sumian_share_${System.currentTimeMillis() / 1000L}.jpg"): File {
        val saveDirFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!saveDirFile.exists()) {
            saveDirFile.mkdir()
        }
        val dirFile = File(saveDirFile, dir)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }

        val file = File(dirFile, mediaFileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

}
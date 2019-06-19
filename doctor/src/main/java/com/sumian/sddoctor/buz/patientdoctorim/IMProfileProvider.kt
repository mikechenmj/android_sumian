package com.sumian.sddoctor.buz.patientdoctorim

import cn.leancloud.chatkit.LCChatKitUser
import cn.leancloud.chatkit.LCChatProfileProvider
import cn.leancloud.chatkit.LCChatProfilesCallBack
import cn.leancloud.chatkit.bean.ImIds
import cn.leancloud.chatkit.bean.ImUser
import com.avos.avoscloud.AVCallback
import com.avos.avoscloud.AVException
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.ResUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/14 11:00
 * desc   :
 * version: 1.0
 */
class IMProfileProvider : LCChatProfileProvider {
    private var mImUserInfoCache = HashMap<String, LCChatKitUser>()

    init {
        initAppUserInfo()
    }

    private fun initAppUserInfo() {
        val doctor = AppManager.getAccountViewModel().getDoctorInfo().value ?: return
        val imId = doctor.imId ?: return
        mImUserInfoCache[imId] = LCChatKitUser(imId, doctor.name, doctor.avatar)
    }

    override fun fetchProfiles(userIdList: MutableList<String>, profilesCallBack: LCChatProfilesCallBack?) {
        // 先看cache中有没有，没有的网络查询，放到cache中，查完返回cache
        val noCacheList = ArrayList<String>()
        for (userId in userIdList) {
            val user = mImUserInfoCache.get(userId)
            if (user == null) {
                noCacheList.add(userId)
            }
        }
        if (noCacheList.isEmpty()) {
            returnProfiles(profilesCallBack)
        } else {
            val call = AppManager.getHttpService().queryImUserInfo(ImIds(userIdList!!))
            call.enqueue(object : BaseSdResponseCallback<Map<String, ImUser?>>() {
                override fun onSuccess(response: Map<String, ImUser?>?) {
                    for (entity in response!!) {
                        val imUser = entity.value ?: continue
                        mImUserInfoCache[entity.key] = LCChatKitUser(imUser.imId, imUser.getNameOrNickname(), imUser.avatar)
                    }
                    returnProfiles(profilesCallBack)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                }
            })
        }
    }

    override fun fetchProfileByConversationType(conversationType: Int, callback: AVCallback<LCChatKitUser>?) {
        when (conversationType) {
            1 -> {
                val user = LCChatKitUser("", App.getAppContext().resources.getString(R.string.cbti_therapist), ResUtil.resIdToUrl(R.drawable.img_consultant))
                callback?.internalDone(user, null)
            }
            else -> callback?.internalDone(null, AVException(1, "wrong type"))
        }
    }

    private fun returnProfiles(profilesCallBack: LCChatProfilesCallBack?) {
        profilesCallBack?.done(ArrayList(mImUserInfoCache.values), null)
    }

    override fun getAllUsers(): MutableList<LCChatKitUser> {
        return ArrayList(mImUserInfoCache.values)
    }
}
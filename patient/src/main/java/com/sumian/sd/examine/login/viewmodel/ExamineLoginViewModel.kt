package com.sumian.sd.examine.login.viewmodel

import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.app.AppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExamineLoginViewModel : BaseExamineViewModel() {

    suspend fun loginByPassword(mobile: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val call = AppManager.getSdHttpService().loginByPassword(mobile, password)
            val response = call.execute()
            mWorkTasks.add(call)
            val isSuccess = response.isSuccessful
            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    AppManager.onLoginSuccess(response.body())
                } else {
                    val errResponse = response.errorBody()?.let { getErrorResponseFromErrorBody(response.code(), it) }
                    ToastUtils.showShort(errResponse?.message ?: "网络错误")
                }
                isSuccess
            }
        }
    }
}
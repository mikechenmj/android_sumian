package com.sumian.sd.buz.account.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.sumian.common.h5.WebViewManger
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.h5.widget.SWebViewLayout
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.h5.H5Uri
import com.tencent.smtt.sdk.WebView
import kotlin.system.exitProcess


class AgreementAndPrivacyDialog : DialogFragment() {

    private lateinit var mRoot: View

    companion object {
        const val AGREEMENT_AND_PRIVACY_DIALOG_NEED_SHOW = "agreement_and_privacy_dialog_need_show"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_MinWidth)
        init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mRoot
    }

    fun init() {
        val inflater = LayoutInflater.from(context)
        mRoot = inflater.inflate(R.layout.layout_agreement_and_privacy_web_dialog, null)
        val webview = mRoot.findViewById<SWebViewLayout>(R.id.sw_agreement_webview)
        webview.setWebListener(object : AgreementWebViewListener {})
        webview.loadRequestUrl(WebViewManger.getInstance().getBaseUrl() + H5Uri.USER_AGREEMENT_AND_PRIVACY_URL)
        val cancel = mRoot.findViewById<AppCompatButton>(R.id.bt_cancel)
        val ok = mRoot.findViewById<AppCompatButton>(R.id.bt_ok)
        cancel.setOnClickListener {
            exitProcess(0)
            dismiss()
        }
        ok.setOnClickListener {
            val sp = AppManager.mApplication.getSharedPreferences(AGREEMENT_AND_PRIVACY_DIALOG_NEED_SHOW, Context.MODE_PRIVATE)
            sp.edit().putBoolean(AGREEMENT_AND_PRIVACY_DIALOG_NEED_SHOW, false).commit()
            dismiss()
        }
    }

    interface AgreementWebViewListener : SWebView.OnWebViewListener {
        override fun onPageStarted(view: WebView?) {
        }

        override fun onProgressChange(view: WebView?, newProgress: Int) {
        }

        override fun onPageFinish(view: WebView?) {
        }

        override fun onRequestErrorCallback(view: WebView?, responseCode: Int) {
        }

        override fun onRequestNetworkErrorCallback(view: WebView?) {
        }

        override fun onReceiveTitle(view: WebView?, title: String?) {
        }
    }
}
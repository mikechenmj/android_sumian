package com.sumian.sleepdoctor.test

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.h5.bean.H5ShowToastData
import com.sumian.sleepdoctor.widget.dialog.SumianImageTextDialog
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        bt_1.setOnClickListener {
            SumianImageTextDialog(this).show(createData())
        }
        bt_2.setOnClickListener {
            val toastData = createData()
            toastData.duration = 0
            SumianImageTextDialog(this).show(toastData)
        }
        bt_3.setOnClickListener {
            val toastData = createData()
            toastData.type = "loading"
            toastData.duration = 0
            SumianImageTextDialog(this).show(toastData)
        }
        bt_4.setOnClickListener {
            val toastData = createData()
            toastData.type = "success"
            toastData.duration = 0
            SumianImageTextDialog(this).show(toastData)
        }
        bt_5.setOnClickListener {
            val toastData = createData()
            toastData.type = "error"
            toastData.duration = 0
            SumianImageTextDialog(this).show(toastData)
        }
        bt_6.setOnClickListener {
            val toastData = createData()
            toastData.type = "warning"
            toastData.duration = 0
            val dialog = SumianImageTextDialog(this)
            dialog.show(toastData)
            mHandler.postDelayed({ dialog.dismiss(1000) }, 1000)

        }
    }

    fun createData(): H5ShowToastData {
        val toastData = H5ShowToastData()
        toastData.type = "text"
//        toastData.message = "hahaha"
//        toastData.duration = 500
        toastData.delay = 0
        return toastData
    }
}

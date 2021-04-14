package com.lkxiaojian.animationeverything

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lkxiaojian.interf.AnimationCompleteListener
import kotlinx.android.synthetic.main.printer_textview.*

class ViewActivity : AppCompatActivity() {
    private var layout: Int = 0
    private val tag = ViewActivity::class.java.name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getStringExtra("type")
        when (type) {
            "circleLoading" -> layout = R.layout.circleloading
            "DoubleCircleLoading" -> layout = R.layout.double_circle_loading
            "PrinterTextView" -> {
                layout = R.layout.printer_textview
            }
        }
        setContentView(layout)
        setJavaSetting(type)
    }

    private fun setJavaSetting(type: String?) {
        when (type) {
            "circleLoading" -> {

            }
            "DoubleCircleLoading" -> {

            }
            "PrinterTextView" -> {
                setPrinterTextView()
            }
        }

    }

    /**
     * TODO 代码设置
     *
     */
    private fun setPrinterTextView() {
        prtv.setDuration(2000).setShowMessage("只让值动画是没什么意义的")
            .setAnimationCompleteListener(object : AnimationCompleteListener {
                override fun animationComplete() {
                }
            }).build()
    }
}
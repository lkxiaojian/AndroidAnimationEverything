package com.lkxiaojian.animationeverything

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lkxiaojian.interf.AnimationCompleteListener
import kotlinx.android.synthetic.main.bar_chart.*
import kotlinx.android.synthetic.main.drag_bubble_view.*
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
            "PrinterTextView" -> layout = R.layout.printer_textview
            "DragBubbleView" -> layout = R.layout.drag_bubble_view
            "BarChart" -> layout = R.layout.bar_chart

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
                prtv.Builder().setDuration(2000).setShowMessage("只让值动画是没什么意义的")
                    .setAnimationCompleteListener(object : AnimationCompleteListener {
                        override fun animationComplete() {
                        }
                    }).build()
            }
            "DragBubbleView" -> {
                bt_qq_view.setOnClickListener {
                    dvqq.Builder().setBubbleText("3").build()
                }
            }
            "BarChart" -> {
                val dataS = arrayListOf<Double>()
                dataS.add(70.0)
                dataS.add(80.0)
                dataS.add(100.0)
                val mDesciption = arrayListOf<String>()
                mDesciption.add("a")
                mDesciption.add("b")
                mDesciption.add("c")
                lbv.Builder()
                    .setAvgData(80.0)
                    .setAvgTitle("平均数：80")
                    .setDatas(dataS, mDesciption)
                    .setShowNumber(6).build()
            }
        }

    }

}
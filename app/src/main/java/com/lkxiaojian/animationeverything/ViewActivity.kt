package com.lkxiaojian.animationeverything

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.circleloading.*

class ViewActivity : AppCompatActivity() {
    private var layout: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getStringExtra("type")
        when (type) {
            "circleLoading" ->layout=R.layout.circleloading
        }
        setContentView(layout)
    }
}
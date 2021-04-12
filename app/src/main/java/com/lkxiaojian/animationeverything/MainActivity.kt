package com.lkxiaojian.animationeverything

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val list= arrayListOf<String>()
        list.add("circleLoading")
        val viewAdapter = ViewAdapter(list)
        rview.adapter=viewAdapter
        rview.layoutManager=LinearLayoutManager(this)
    }
}
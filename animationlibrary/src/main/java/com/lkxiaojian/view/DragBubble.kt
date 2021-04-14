package com.lkxiaojian.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 *create_time : 21-4-14 下午4:36
 *author: lk
 *description： DragBubble 仿qq聊天界面的拖拽气泡
 */
class DragBubble(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //1.静止状态 ，一个小球加消息数据
        //2.连接状态， 一个小球加消息数据 ，贝塞尔曲线，本身位置上的小球，大小可变化
        //3.分离状态， 一个小球家消息数据
        //4.消息状态，爆炸效果
    }


}
package com.lkxiaojian.Utlis

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.roundToInt

/**
 * create_time : 21-4-12 下午2:11
 * author: lk
 * description： UIUtils
 */
@Suppress("DEPRECATION")
class AnimationLibUtils private constructor(context: Context) {
    val horizontalScaleValue: Float
        get() = displayMetricsWidth / STANDARD_WIDTH
    val verticalScaleValue: Float
        get() = displayMetricsHeight / (STANDARD_HEIGHT - stateBarHeight)

    /**
     * 用于得到状态框的高度
     */
    fun getSystemBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        val height = context.resources.getDimensionPixelSize(resourceId)
        return if (height != -1) {
            height
        } else getValue(context, "com.android.internal.R\$dimen", "system_bar_height", 48)
    }

    private fun getValue(
        context: Context,
        dimeClass: String,
        system_bar_height: String,
        defaultValue: Int
    ): Int {
        try {
            val clz = Class.forName(dimeClass)
            val `object` = clz.newInstance()
            val field = clz.getField(system_bar_height)
            val id = field[`object`].toString().toInt()
            return context.resources.getDimensionPixelSize(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultValue
    }

    fun getWidth(width: Int): Int {
        return (width.toFloat() * displayMetricsWidth / STANDARD_WIDTH).roundToInt()
    }

    fun getHeight(height: Int): Int {
        return (height.toFloat() * displayMetricsHeight / (STANDARD_HEIGHT - stateBarHeight)).roundToInt()
    }

    companion object {
        //标准值  正常情况下应该保存在配置文件中
         var STANDARD_WIDTH = 1080f
         var STANDARD_HEIGHT = 1920f
        //实际设备信息
        var displayMetricsWidth = 0f
        var displayMetricsHeight = 0f
        var stateBarHeight = 0f
        private var instance: AnimationLibUtils? = null
        fun getInstance(context: Context): AnimationLibUtils? {
            if (instance == null) {
                instance = AnimationLibUtils(context)
            }
            return instance
        }

//        fun getInstance(): AnimationLibUtils? {
//            if (instance == null) {
//                throw RuntimeException("animationLibrary未进行初始化")
//            }
//            return instance
//        }
    }

    init {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        if (displayMetricsWidth == 0.0f || displayMetricsHeight == 0.0f) {
            //在这里得到设备的真实值
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val systemBarHeight = getSystemBarHeight(context)
            //判断横屏还竖屏
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                displayMetricsWidth = displayMetrics.heightPixels.toFloat()
                displayMetricsHeight = (displayMetrics.widthPixels - systemBarHeight).toFloat()
            } else {
                displayMetricsWidth = displayMetrics.widthPixels.toFloat()
                displayMetricsHeight = (displayMetrics.heightPixels - systemBarHeight).toFloat()
            }
            stateBarHeight = getSystemBarHeight(context).toFloat()
        }
    }
}
package com.lkxiaojian.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView
import com.lkxiaojian.interf.AnimationCompleteListener
import com.lkxiaojian.Utlis.AnimationLibUtils
import com.lkxiaojian.animationlibrary.R

/**
 *create_time : 21-4-13 下午5:00
 *author: lk
 *description： PrinterTextView
 */
@SuppressLint("AppCompatCustomView")
class PrinterTextView(context: Context, attrs: AttributeSet?) : TextView(context, attrs) {
    private var mValueAnimator: ValueAnimator? = null
    private var mAnimatorValue = 0
    private var mListener: AnimationCompleteListener? = null

    //动画时长
    private var duration: Long
    private var textCount: Int = 0
    private var showText: String? = null
    private var stringBuffer = StringBuffer()
    private var datas: List<Char>? = null
    private var currentIndex = -1

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PrinterTextView)
        duration = typedArray.getInt(R.styleable.PrinterTextView_printer_duration, 2000).toLong()
        showText = typedArray.getString(R.styleable.PrinterTextView_printer_show_message)
        showText?.let {
            datas = it.toList()
            textCount = it.length
        }
        AnimationLibUtils.getInstance(context)
        initp()
    }

    private fun initp() {
        if (datas.isNullOrEmpty()) {
            return
        }
        mValueAnimator = ValueAnimator.ofInt(0, textCount)
        mValueAnimator?.addUpdateListener {
            try {
                mAnimatorValue = it.animatedValue as Int
                postInvalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mValueAnimator?.repeatCount = ValueAnimator.REVERSE
        mValueAnimator?.duration = duration //动画时长
        mValueAnimator?.start()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (currentIndex < mAnimatorValue && !datas.isNullOrEmpty() && mAnimatorValue != datas!!.size) {
            stringBuffer.append(datas!![mAnimatorValue])
            text = stringBuffer.toString()
            currentIndex = mAnimatorValue
            if (currentIndex == datas!!.size) {
                mListener?.animationComplete()
            }
        }
    }

    /**
     * TODO 设置动画时长 1000 =1s
     *
     * @param d
     * @return
     */
    fun setDuration(d: Long): PrinterTextView {
        this.duration = d
        return this
    }

    /**
     * TODO 设置要显示的文字
     *
     * @param text
     * @return
     */
    fun setShowMessage(text: String): PrinterTextView {
        this.showText = text
        datas = text.toList()
        textCount = text.length
        return this
    }

    /**
     * TODO设置完成渲染后监听
     *
     * @param listener
     * @return
     */
    fun setAnimationCompleteListener(listener: AnimationCompleteListener): PrinterTextView {
        this.mListener = listener
        return this
    }

    /**
     * TODO 设置完成后要调用此方法 ，不然无效
     */
    fun build() {
        initp()
    }
}
package com.lkxiaojian.animationeverything.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import com.lkxiaojian.animationeverything.R
import kotlin.math.abs

@SuppressLint("ResourceAsColor")
class CircleLoading(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mPath: Path = Path()
    private var dest: Path ?=Path()

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPathMeasure: PathMeasure = PathMeasure(mPath, false)
    private var mCircleColor: Int = R.color.purple_700
    private var radius: Float = 50f
    private var mValueAnimator: ValueAnimator? = null
    private var mAnimatorValue = 0f
    private var mLength=0f


    init {
        initP()
    }

    private fun initP() {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mCircleColor
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 10f
        mPath.addCircle(300f, 300f, radius, Path.Direction.CW)
        mPathMeasure.setPath(mPath, true)
        mLength=mPathMeasure.length
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        mValueAnimator?.addUpdateListener {
            try {
                mAnimatorValue = it.animatedValue as Float
                postInvalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mValueAnimator?.repeatCount = ValueAnimator.INFINITE    //无限循环
        mValueAnimator?.duration = 2000 //动画时长
        mValueAnimator?.start()


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dest?.reset()
        val distance = mLength * mAnimatorValue
        val start =
            (distance - (0.5 - abs(mAnimatorValue - 0.5)) * mLength).toFloat()
        mPathMeasure.getSegment(start, distance, dest, true)
        canvas?.drawPath(dest!!,mPaint)
    }

    /**
     * TODO  color 设置圆环的颜色
     *
     * @param color
     * @return CircleLoading
     */
    fun setCircleColor(color: Int): CircleLoading {
        this.mCircleColor = mCircleColor
        return this
    }


    /**
     *  停止 动画
     */
    fun setStop() {


    }


}
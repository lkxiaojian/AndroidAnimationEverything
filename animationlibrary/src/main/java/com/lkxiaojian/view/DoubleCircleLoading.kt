package com.lkxiaojian.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import com.lkxiaojian.Utlis.AniColor.color_3700B3
import com.lkxiaojian.Utlis.AniColor.color_9696
import com.lkxiaojian.Utlis.AnimationLibUtils
import com.lkxiaojian.animationlibrary.R


/**
 *create_time : 21-4-12 下午3:42
 *author: lk
 *description： DoubleCircleLoading
 */
@SuppressLint("ResourceAsColor", "Recycle")
class DoubleCircleLoading(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var mPath: Path // 圆环的path
    private lateinit var mPaint: Paint //画笔
    private lateinit var mAniPaint: Paint //画笔
    private var mValueAnimator: ValueAnimator? = null
    private var mAnimatorValue = 0f

    //圆环颜色
    private var mCircleColor: Int

    //转动的圆环颜色
    private var mCircleAniColor: Int

    //圆环半径
    private var mRadius: Float

    //圆环 中心点的坐标
    private var centerX: Float
    private var centerY: Float

    //动画时长
    private var duration: Long
    private var paintWith: Float
    private var oval: RectF? = null
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleCircleLoading)
        mRadius = typedArray.getFloat(R.styleable.DoubleCircleLoading_circle_d_radius, 50f)
        centerX = typedArray.getFloat(R.styleable.DoubleCircleLoading_circle_d_centerX, 540f)
        centerY = typedArray.getFloat(R.styleable.DoubleCircleLoading_circle_d_centerY, 960f)
        duration =
            typedArray.getInt(R.styleable.DoubleCircleLoading_circle_d_duration, 2000).toLong()
        paintWith = typedArray.getFloat(R.styleable.DoubleCircleLoading_circle_d_p_with, 10f)
        mCircleColor =
            typedArray.getColor(
                R.styleable.DoubleCircleLoading_circle_d_color,
                color_9696
            )
        mCircleAniColor =
            typedArray.getColor(
                R.styleable.DoubleCircleLoading_circle_ani_color,
                color_3700B3
            )
        AnimationLibUtils.getInstance(context)
        oval = RectF(centerX - mRadius, centerY - mRadius, centerX + mRadius, centerY + mRadius)

        initP()
    }

    private fun initP() {
        mPath = Path()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mCircleColor
        mPaint.isAntiAlias = true //抗锯齿
        mPaint.strokeWidth = paintWith//画笔的宽度
        mPath.addCircle(centerX, centerY, mRadius, Path.Direction.CW)//画圆环

        mAniPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mAniPaint.style = Paint.Style.STROKE
        mAniPaint.color = mCircleAniColor
        mAniPaint.isAntiAlias = true //抗锯齿
        mAniPaint.strokeWidth = paintWith//画笔的宽度

        mValueAnimator = ValueAnimator.ofFloat(0f, 360f)
        mValueAnimator?.addUpdateListener {
            try {
                mAnimatorValue = it.animatedValue as Float
                postInvalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mValueAnimator?.repeatCount = ValueAnimator.INFINITE    //无限循环
        mValueAnimator?.duration = duration //动画时长
        mValueAnimator?.interpolator = LinearInterpolator()
        mValueAnimator?.start()
    }


    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)
        canvas?.drawArc(oval!!, mAnimatorValue, 90f, false, mPaint)
    }

    inner class Builder {
        /**
         * TODO  color 设置圆环的颜色
         *
         * @param color
         * @return CircleLoading
         */
        fun setCircleColor(color: Int): Builder {
            mCircleColor = color
            return this
        }

        /**
         * TODO  color 设置圆环动画的颜色
         *
         * @param color
         * @return CircleLoading
         */
        fun setCircleAniColor(color: Int): Builder {
            mCircleAniColor = color
            return this
        }


        /**
         * TODO 设置圆环的半径
         *
         * @param radius  半径的长度
         * @return CircleLoading
         */
        fun setRadius(radius: Float): Builder {
            mRadius = radius
            return this
        }

        /**
         * TODO 设置圆环的中心点 默认是屏幕的中心点
         *
         * @param x x坐标
         * @param y y坐标
         * @return
         */
        fun setCenterPoint(x: Float, y: Float): Builder {
            centerX = x
            centerY = y
            return this
        }

        /**
         * TODO 动画时长 默认 2秒
         *
         * @param l
         * @return
         */
        fun setDuration(l: Long): Builder {
            duration = l
            return this
        }

        /**
         * TODO 每次设置属性 需要调用，不然无效
         * 不建议使用java 代码设置  需要更改默认属性 可以在xml 中进行设置
         */
        fun build():DoubleCircleLoading {
            initP()
            postInvalidate()
            return this@DoubleCircleLoading
        }


        /**
         *  停止 动画 并隐藏
         */
        fun setStop() {
            mValueAnimator?.cancel()
            visibility = GONE
        }

        /**
         * TODO 停止的动画重新开始，如果未调用 setStop（）方法 ，则不需要调用此方法（因为动画的start 默认已经开启）
         *
         */
        fun start() {
            visibility = VISIBLE
            mValueAnimator?.start()
        }
    }



}
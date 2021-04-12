package com.lkxiaojian.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import com.lkxiaojian.Utlis.AnimationLibUtils.Companion.getInstance
import com.lkxiaojian.animationlibrary.R
import kotlin.math.abs


@SuppressLint("ResourceAsColor")
class CircleLoading(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var mPath: Path
    private lateinit var dest: Path
    private lateinit var mPaint: Paint
    private lateinit var mPathMeasure: PathMeasure
    private var mValueAnimator: ValueAnimator? = null
    private var mAnimatorValue = 0f
    private var mLength = 0f

    //圆环颜色
    private var mCircleColor: Int = R.color.purple_700

    //圆环半径
    private var mRadius: Float = 0f

    //圆环 中心点的坐标
    private var centerX = 0f
    private var centerY = 0f

    //动画时长
    private var duration = 2000L

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleLoading)
        mRadius=typedArray.getFloat(R.styleable.CircleLoading_circle_radius,50f)
        centerX=typedArray.getFloat(R.styleable.CircleLoading_circle_centerX,540f)
        centerY=typedArray.getFloat(R.styleable.CircleLoading_circle_centerY,960f)
        duration=typedArray.getInt(R.styleable.CircleLoading_circle_duration,2000).toLong()
        mCircleColor=typedArray.getColor(R.styleable.CircleLoading_circle_color,R.color.purple_700)
        getInstance(context)
        initP()
    }
    private fun initP() {
        mPath= Path()
        dest= Path()
        mPathMeasure=PathMeasure(mPath, false)
        mPaint= Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mCircleColor
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 10f
        mPath.addCircle(centerX, centerY, mRadius, Path.Direction.CW)
        mPathMeasure.setPath(mPath, true)
        mLength = mPathMeasure.length
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
        mValueAnimator?.duration = duration //动画时长
        mValueAnimator?.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dest.reset()
        val distance = mLength * mAnimatorValue
        val start =
            (distance - (0.5 - abs(mAnimatorValue - 0.5)) * mLength).toFloat()
        mPathMeasure.getSegment(start, distance, dest, true)
        canvas?.drawPath(dest, mPaint)
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
     * TODO 设置圆环的半径
     *
     * @param radius  半径的长度
     * @return CircleLoading
     */
    fun setRadius(radius: Float): CircleLoading {
        this.mRadius = radius
        return this
    }

    /**
     * TODO 设置圆环的中心点 默认是屏幕的中心点
     *
     * @param x x坐标
     * @param y y坐标
     * @return
     */
    fun setCenterPoint(x: Float, y: Float): CircleLoading {
        this.centerX = x
        this.centerY = y
        return this
    }

    /**
     * TODO 动画时长 默认 2秒
     *
     * @param l
     * @return
     */
    fun setDuration(l: Long): CircleLoading {
        this.duration = l
        return this
    }

    /**
     * TODO 每次设置属性 需要调用，不然无效
     * 不建议使用java 代码设置  需要更改默认属性 可以在xml 中进行设置
     */
    fun build(){
        initP()
        postInvalidate()
    }


    /**
     *  停止 动画 并隐藏
     */
    fun setStop() {
        mValueAnimator?.cancel()
        visibility = View.GONE
    }

}
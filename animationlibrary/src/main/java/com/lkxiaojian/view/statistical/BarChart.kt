package com.lkxiaojian.view.statistical

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.lkxiaojian.Utlis.AnimationLibUtils
import com.lkxiaojian.animationlibrary.R
import kotlin.collections.ArrayList

class BarChart(context: Context, attrs: AttributeSet?) : LBaseView (context,attrs){
    private var detector: GestureDetector? = null
    private var mDatas: ArrayList<Double>? = null
    private var mDescription: ArrayList<String>? = null
    private var mAvgData: Double = 0.0
    private var mDataLinePaint: Paint? = null
    private var defaultLineColor = Color.argb(255, 74, 134, 232)
    private var descriptionColor = 0
    private var dataColor = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mShowNumber = 0
    private var perBarW = 0f
    private var maxData: Double = 0.0
    private var mMaxScrollx = 0
    private var defaultBorderColor = Color.argb(1, 0, 0, 0)
    private var xxLineColor = Color.argb(255, 219, 219, 219)
    private var mBorderLinePaint: Paint? = null
    private var mxxLinePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var descriptionTextSize = 0
    private var dataTextSize = 0
    private var avgTextSize = 0
    private var mBottomPadding = 0
    private var mLeftPadding = 0
    private var mTopPadding = 0
    private var textTop = 10f
    private var xLineColor = Color.argb(255, 219, 219, 219)
    private var xAVGLineColor = 0
    private var mXXPaint: Paint? = null
    private var mAVGPaint: Paint? = null
    private var mAVGTextPaint: Paint? = null
    private var barGesture: BarGesture? = null
    private var dragInerfaces: DragInerfaces? = null
    init {
        init(context,attrs)
    }

    @SuppressLint("CustomViewStyleable", "ResourceAsColor")
    private fun init(context: Context, attrs: AttributeSet?) {
        val t = context.obtainStyledAttributes(attrs, R.styleable.barCharts)
        defaultBorderColor = t.getColor(R.styleable.barCharts_bottomLineColor, R.color.black)
        descriptionTextSize = t.getDimension(R.styleable.barCharts_labelTextSize, 20f).toInt()
        dataTextSize = t.getDimension(R.styleable.barCharts_dataTextSize, 20f).toInt()
        descriptionColor = t.getColor(R.styleable.barCharts_descriptionTextColor, Color.GRAY)
        xAVGLineColor = t.getColor(R.styleable.barCharts_avgLineColor, R.color.color_FA6400)
        dataColor = t.getColor(R.styleable.barCharts_dataTextColor, Color.GRAY)
        mShowNumber = t.getInteger(R.styleable.barCharts_barShowNumber, 6)
        avgTextSize = t.getDimension(R.styleable.barCharts_avgTitleTextSize, 20f).toInt()
        canClickAnimation = t.getBoolean(R.styleable.barCharts_isClickAnimation, false)
        t.recycle()
        barGesture = BarGesture()
        detector = GestureDetector(context, barGesture)
        mDatas = ArrayList()
        mDescription = ArrayList()
        mDataLinePaint = Paint()
        mDataLinePaint?.isAntiAlias = true
        mDataLinePaint?.color = defaultLineColor
        mDataLinePaint?.style = Paint.Style.STROKE
        mBorderLinePaint = Paint()
        mBorderLinePaint?.color = defaultBorderColor
        mBorderLinePaint!!.style = Paint.Style.STROKE
        mBorderLinePaint!!.strokeWidth = dp2px(2).toFloat()
        mBorderLinePaint!!.isAntiAlias = true
        val pathEffect = DashPathEffect(floatArrayOf(dp2px(6).toFloat(), dp2px(2).toFloat()), 0f)
        mxxLinePaint = Paint()
        mxxLinePaint!!.color = xxLineColor
        mxxLinePaint!!.style = Paint.Style.FILL_AND_STROKE
        mxxLinePaint!!.strokeWidth = dp2px(1).toFloat()
        mxxLinePaint!!.isAntiAlias = true
        mxxLinePaint!!.pathEffect = pathEffect
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = Color.GRAY
        mTextPaint!!.style = Paint.Style.FILL_AND_STROKE
        mTextPaint!!.textSize = descriptionTextSize.toFloat()
        mTextPaint!!.strokeWidth = 1f
        mTextPaint!!.textSize = descriptionTextSize.toFloat()
        mTextPaint!!.color = descriptionColor
        mXXPaint = Paint()
        mXXPaint!!.isAntiAlias = true
        mXXPaint!!.color = xLineColor
        mXXPaint!!.strokeWidth = 3f
        mXXPaint!!.pathEffect = pathEffect
        mAVGPaint = Paint()
        mAVGPaint!!.isAntiAlias = true
        mAVGPaint!!.color = xAVGLineColor
        mAVGPaint!!.strokeWidth = dp2px(1).toFloat()
        mAVGPaint!!.pathEffect = pathEffect
        mAVGTextPaint = Paint()
        mAVGTextPaint!!.isAntiAlias = true
        mAVGTextPaint!!.color = xAVGLineColor
        mAVGTextPaint!!.strokeWidth = 4f
        textTop = AnimationLibUtils.dp2px(context, 7).toFloat()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        setDataLineWidth()
    }

    @Synchronized
    private fun setDataLineWidth() {
        mDatas?.let {
        if (it.size > 0) {
            mShowNumber = if (mDatas!!.size < 6) {
                mDatas!!.size
            } else {
                6
            }
            mDataLinePaint!!.strokeWidth = mWidth / (mShowNumber * 2).toFloat()
            mMaxScrollx = mWidth / mShowNumber * mDatas!!.size - mWidth
        }
        }
    }

    private val num = 5

    @SuppressLint("ResourceAsColor", "DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        perBarW = mWidth / mShowNumber.toFloat()

        canvas.translate(0f, mHeight - mBottomPadding.toFloat())
        setMaxData()
        val lHeight = mHeight - mTopPadding - mBottomPadding
        for (i in 1 until num + 1) {
            val y = -lHeight / num * i
            canvas.drawLine(
                0f,
                y.toFloat(),
                mMaxScrollx + mWidth.toFloat(),
                y.toFloat(),
                mxxLinePaint!!
            )
        }
        //绘制平均线
        val avgHeight = (-lHeight / maxData * mAvgData).toFloat()
        canvas.drawLine(0f, avgHeight, mMaxScrollx + mWidth.toFloat(), avgHeight, mAVGPaint!!)
        canvas.drawLine(
            0f,
            mBorderLinePaint!!.strokeWidth / 2,
            mMaxScrollx + mWidth.toFloat(),
            mBorderLinePaint!!.strokeWidth / 2,
            mBorderLinePaint!!
        )
        for (i in mDatas!!.indices) {
            val perData = mDatas!![i].toString()
            val x = (i + 0.5f) * perBarW
            val y = ((mHeight - mTopPadding - mBottomPadding) / maxData!! * mDatas!![i]).toFloat()
            val lg: LinearGradient =
                LinearGradient(x, 0f, x, -y * scale, -0xce3d12, -0xff6901, Shader.TileMode.CLAMP)
            mDataLinePaint!!.shader = lg
            canvas.drawLine(x, 0f, x, -y * scale, mDataLinePaint!!)

            canvas.drawText(
                perData,
                x - mTextPaint!!.measureText(perData) / 2,
                -y * scale - dataTextSize,
                mTextPaint!!
            )
            canvas.drawText(
                mDescription!![i],
                x - mTextPaint!!.measureText(mDescription!![i]) / 2,
                descriptionTextSize + textTop,
                mTextPaint!!
            )
        }
    }

   private fun startAnimation() {
        if (canClickAnimation) {
            animator?.start()
        }
    }

    fun setDragInerfaces(dragInerfaces: DragInerfaces?) {
        this.dragInerfaces = dragInerfaces
    }

    fun setBootomDrawPadding(bottomy: Int) {
        mBottomPadding = bottomy
    }

    fun setLeftDrawPadding(left: Int) {
        mLeftPadding = left
    }

    fun setTopDrawPadding(left: Int) {
        mTopPadding = left
    }

    private fun setMaxData() {
//        Collections.max(mDatas);
        maxData = 100.0
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (detector!!.onTouchEvent(motionEvent)) {
            return detector!!.onTouchEvent(motionEvent)
        }
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> endGesture(motionEvent)
            else -> {
            }
        }
        return false
    }

    private fun endGesture(motionEvent: MotionEvent) {}
    private inner class BarGesture : SimpleOnGestureListener() {
        var preScrollX = 0
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            var distanceX = distanceX
            val scrollX = scrollX
            val minScrollX = -scrollX
            if (scrollX > mMaxScrollx && distanceX > 0) {
                distanceX = 0f
                if (dragInerfaces != null && scrollX - preScrollX > 0) {
                    dragInerfaces!!.onEnd()
                }
            } else {
                if (distanceX < minScrollX) {
                    if (dragInerfaces != null && minScrollX != 0) {
                        dragInerfaces!!.onStart()
                    }
                    distanceX = minScrollX.toFloat()
                }
            }
            scrollBy(distanceX.toInt(), 0)
            preScrollX = scrollX
            if (dragInerfaces != null) {
                dragInerfaces!!.preScrollX(preScrollX)
            }
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            startAnimation();
            return super.onSingleTapUp(e)
        }
    }

    fun setDatas(
        mDatas: ArrayList<Double>,
        mDesciption: ArrayList<String>,
        avgData: Double,
        isAnimation: Boolean
    ) {
        this.mDatas?.clear()
        this.mDatas?.addAll(mDatas)
        mDescription = mDesciption
        if (mDatas.size < 6) {
            mShowNumber = mDatas.size
        }
        mAvgData = avgData
        setDataLineWidth()
        if (barGesture!!.preScrollX > 0) {
            scrollBy(-barGesture!!.preScrollX, 0)
            barGesture!!.preScrollX = 0
        }
        if (isAnimation) {
            animator!!.start()
        } else {
            scale = 1f
            postInvalidate()
        }
    }

    fun addEndMoreData(mDatas: ArrayList<Double>, mDesciption: ArrayList<String>) {
        this.mDatas?.addAll(mDatas)
        mDescription?.addAll(mDesciption)
        setDataLineWidth()
        scale = 1f
        postInvalidate()
    }

    private var startX = 0
    fun addStartMoreData(dataS: ArrayList<Double>, mDesciption: ArrayList<String>) {
        this.mDatas?.clear()
        this.mDatas?.addAll(dataS)
        mDescription?.clear()
        mDescription?.addAll(mDesciption)
        startX = mWidth / mShowNumber * this.mDatas!!.size
        setDataLineWidth()
        postInvalidate()
    }
}
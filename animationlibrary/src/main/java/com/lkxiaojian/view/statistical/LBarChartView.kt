package com.lkxiaojian.view.statistical

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lkxiaojian.Utlis.AnimationLibUtils
import com.lkxiaojian.Utlis.AnimationLibUtils.Companion.dp2px
import com.lkxiaojian.animationlibrary.R
import java.util.*
import kotlin.collections.ArrayList

class LBarChartView : FrameLayout {
    private var defaultBorderColor = Color.argb(255, 217, 217, 217)
    private var titleTextColor = Color.argb(255, 0, 0, 0)
    private var xLineColor = Color.argb(255, 219, 219, 219)
    private val blueProgressBarColor = Color.argb(255, 51, 136, 239)
    private val progressBarColor = Color.argb(255, 217, 217, 217)
    private var labelTextColor = 0
    private var mTitleTextSize = 42
    private var mLabelTextSize = 20
    private var avgTitleTextSize = 30
    private var mTitle: String? = null
    private var mAvgTitle: String? = null
    private var mWidth = 0
    private var mHeight = 0
    var mLeftTextSpace = 0
    private var mBottomTextSpace = 0
    private var mTopTextSpace = 0
    private var mBorderLinePaint: Paint? = null
    private var maxData: Double=0.0
    private var mDatas: List<Double>? = null

    /**
     * 备注文本画笔
     */
    private var mTextPaint: Paint? = null
    private var mAvgTextPaint: Paint? = null
    private var progressBar: Paint? = null
    private var blueProgressBar: Paint? = null
    private var srcollBar = 0

    /**
     * 标题文本画笔
     */
    private var mTitleTextPaint: Paint? = null
    private var barChartView: BarChart? = null
    private var dp24 = 70
    private var avgLeft = 70
    private var mShowNumber = 6
    private var perBarW = 0f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet?) {
        mDatas = ArrayList()
        val t = context.obtainStyledAttributes(attrs, R.styleable.barCharts)
        defaultBorderColor = t.getColor(R.styleable.barCharts_borderColor, defaultBorderColor)
        titleTextColor = t.getColor(R.styleable.barCharts_titleTextColor, Color.GRAY)
        mTitleTextSize =
            t.getDimension(R.styleable.barCharts_titleTextSize, mTitleTextSize.toFloat())
                .toInt()
        mLabelTextSize =
            t.getDimension(R.styleable.barCharts_labelTextSize, mLabelTextSize.toFloat())
                .toInt()
        avgTitleTextSize =
            t.getDimension(R.styleable.barCharts_avgTitleTextSize, avgTitleTextSize.toFloat())
                .toInt()
        labelTextColor = t.getColor(R.styleable.barCharts_labelTextColor, Color.GRAY)
        xLineColor = t.getColor(R.styleable.barCharts_xlineColor, xLineColor)
        mLeftTextSpace = t.getDimension(R.styleable.barCharts_leftTextSpace, 30f).toInt()
        mBottomTextSpace = t.getDimension(R.styleable.barCharts_bottomTextSpace, 20f).toInt()
        mTopTextSpace = t.getDimension(R.styleable.barCharts_topTextSpace, 50f).toInt()
        mTitle = t.getString(R.styleable.barCharts_title)
        mShowNumber = t.getInteger(R.styleable.barCharts_barShowNumber, 6)
        t.recycle()
        val dp1 = dp2px(context, 1).toFloat()
        mBorderLinePaint = createPaint(Paint.Style.FILL_AND_STROKE, defaultBorderColor, dp1, null)
        mTextPaint =
            createPaint(Paint.Style.FILL_AND_STROKE, labelTextColor, dp1, mLabelTextSize.toFloat())

        mTitleTextPaint =
            createPaint(Paint.Style.FILL_AND_STROKE, titleTextColor, 3f, mTitleTextSize.toFloat())

        mAvgTextPaint =
            createPaint(Paint.Style.FILL_AND_STROKE, titleTextColor, 2f, avgTitleTextSize.toFloat())
        val dp4 = dp2px(context, 4).toFloat()

        progressBar = createPaint(Paint.Style.FILL_AND_STROKE, progressBarColor, dp4, null)
        progressBar?.strokeCap = Paint.Cap.ROUND

        blueProgressBar = createPaint(Paint.Style.FILL_AND_STROKE, blueProgressBarColor, dp4, null)
        blueProgressBar?.strokeCap = Paint.Cap.ROUND


        dp24 = dp2px(context, 24)
        avgLeft = dp2px(context, 93)

        barChartView = BarChart(context, attrs)
        val parames = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        parames.setMargins(
            mLeftTextSpace + dp4.toInt(),
            mTopTextSpace,
            mLeftTextSpace,
            0
        )
        barChartView?.layoutParams = parames
        barChartView?.setBootomDrawPadding(mBottomTextSpace)
        barChartView?.setLeftDrawPadding(mLeftTextSpace)
        barChartView?.setTopDrawPadding(mTopTextSpace)
        addView(barChartView)
    }

    /**
     * TODO
     *
     * @param style
     * @param color
     * @param strokeWidth
     * @param textSize
     * @return
     */
    private fun createPaint(
        style: Paint.Style,
        color: Int?,
        strokeWidth: Float?,
        textSize: Float?
    ): Paint {
        val m = Paint()
        m.style = style
        m.isAntiAlias = true
        strokeWidth?.let {
            m.strokeWidth = strokeWidth
        }

        color?.let {
            m.color = it
        }
        textSize?.let {
            {
                m.textSize = it
            }
        }
        return m
    }



    private fun setMaxData() {
        Collections.max(mDatas)
        maxData = 100.0
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        perBarW = mWidth / mShowNumber.toFloat()
        if (mTitle != null) {
            canvas.drawText(
                mTitle!!,
                dp24 / 2 - 10.toFloat(),
                mTopTextSpace - mTitleTextSize + mBottomTextSpace / 2 - mTitleTextSize + 10.toFloat(),
                mTitleTextPaint!!
            )
        }
        if (mAvgTitle != null) {
            canvas.drawText(
                mAvgTitle!!, mWidth - avgLeft.toFloat(),
                mTopTextSpace - mTitleTextSize + mBottomTextSpace / 2.toFloat(), mAvgTextPaint!!
            )
        }
        canvas.translate(mLeftTextSpace.toFloat(), mHeight - mBottomTextSpace.toFloat())
        mTextPaint!!.style = Paint.Style.FILL_AND_STROKE
        for (i in 0..5) {
//            String v = String.valueOf(maxData / 5 * i);
            val v = "" + (maxData / 5 * i).toInt()
            val y = (-mHeight + mBottomTextSpace + mTopTextSpace) / 6 * i
            canvas.drawText(
                v, -mLeftTextSpace.toFloat(),
                y.toFloat(),
                mTextPaint!!
            )
            //            if (i != 0) {
//                canvas.drawLine(0, -y, mWidth, -y, mXXPaint);
//            }
        }
        val withX = mWidth - mLeftTextSpace * 2
        canvas.drawLine(0f, dp24.toFloat(), withX.toFloat(), dp24.toFloat(), progressBar!!)
        if (mDatas!!.size <= mShowNumber) {
            canvas.drawLine(
                -10f,
                dp24.toFloat(),
                withX + 10.toFloat(),
                dp24.toFloat(),
                blueProgressBar!!
            )
        } else {
            val i = (srcollBar / perBarW * 1.5).toInt() + mShowNumber
            var stopX = withX * i / mDatas!!.size
            if (stopX > withX) {
                stopX = withX
            }
            canvas.drawLine(
                -10f,
                dp24.toFloat(),
                stopX + 10.toFloat(),
                dp24.toFloat(),
                blueProgressBar!!
            )
        }
    }

    fun setScroBall(scroBall: Int) {
        srcollBar = scroBall
        postInvalidate()
    }

    fun setDatas(
        mDatas: ArrayList<Double>,
        mDesciption: ArrayList<String>,
        avgData: Double,
        title: String?,
        isAnimation: Boolean
    ) {
        this.mDatas = mDatas
        setMaxData()
        mTitle = title
        mAvgTitle = "平均分数:$avgData"
        mShowNumber = if (mDatas.size < 6) {
            mDatas.size
        } else {
            6
        }
        postInvalidate()
        barChartView!!.setDatas(mDatas, mDesciption, avgData, isAnimation)
    }

    fun setDragInerfaces(dragInerfaces: DragInerfaces?) {
        barChartView!!.setDragInerfaces(dragInerfaces)
    }

    fun addEndMoreData(mDatas: ArrayList<Double>, mDesciption: ArrayList<String>) {
        barChartView!!.addEndMoreData(mDatas, mDesciption)
    } //    public void addStartMoreData(List<Double> mDatas, List<String> mDesciption) {
    //        barChartView.addStartMoreData(mDatas,mDesciption);
    //    }
}
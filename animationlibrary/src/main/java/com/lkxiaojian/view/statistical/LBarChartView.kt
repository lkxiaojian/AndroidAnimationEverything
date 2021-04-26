package com.lkxiaojian.view.statistical

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lkxiaojian.Utlis.AnimationLibUtils.Companion.dp2px
import com.lkxiaojian.animationlibrary.R
import java.util.*
import kotlin.collections.ArrayList

class LBarChartView : FrameLayout {
    private var titleTextColor = Color.argb(255, 0, 0, 0)
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
    private var mLeftTextSpace = 0
    private var mBottomTextSpace = 0
    private var mTopTextSpace = 0
    private var maxData: Double = 0.0
    private var mDatas: ArrayList<Double>? = null

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

    //右侧等级字体大小
    private var leftLabelTextSize = 0f

    //右侧等级字体颜色
    private var leftLabelTextColor = 0

    //底部滑动条是否显示
    private var scrollLine = false

    // 标题是否居中
    private var titleCenter = false

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

    @SuppressLint("CustomViewStyleable", "ResourceAsColor")
    private fun init(context: Context, attrs: AttributeSet?) {
        mDatas = ArrayList()
        val t = context.obtainStyledAttributes(attrs, R.styleable.barCharts)
        titleTextColor = t.getColor(R.styleable.barCharts_titleTextColor, R.color.black)
        leftLabelTextSize = t.getDimension(R.styleable.barCharts_leftLabelTextSize, 12f)
        leftLabelTextColor = t.getColor(R.styleable.barCharts_leftLabelTextColor, R.color.black)
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
        mLeftTextSpace = t.getDimension(R.styleable.barCharts_leftTextSpace, 30f).toInt()
        mBottomTextSpace = t.getDimension(R.styleable.barCharts_bottomTextSpace, 20f).toInt()
        mTopTextSpace = t.getDimension(R.styleable.barCharts_topTextSpace, 50f).toInt()
        mTitle = t.getString(R.styleable.barCharts_title)
        mShowNumber = t.getInteger(R.styleable.barCharts_barShowNumber, 6)
        scrollLine = t.getBoolean(R.styleable.barCharts_scrollLine, false)
        titleCenter = t.getBoolean(R.styleable.barCharts_titleCenter, false)
        t.recycle()

        mTextPaint =
            createPaint(Paint.Style.FILL_AND_STROKE, leftLabelTextColor, 1f, leftLabelTextSize)
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

            m.textSize = it

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

            val x: Float = if (titleCenter) {
                (width / 2).toFloat()
            } else {
                dp24 / 2 - 10.toFloat()
            }
            canvas.drawText(
                mTitle!!,
                x,
                (mTopTextSpace - mTitleTextSize / 2).toFloat(),
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
        for (i in 0..5) {
            val v = "" + (maxData / 5 * i).toInt()
            val y = (-mHeight + mBottomTextSpace + mTopTextSpace) / 6 * i
            canvas.drawText(
                v, -mLeftTextSpace.toFloat(),
                y.toFloat(),
                mTextPaint!!
            )
        }

        if (scrollLine) {
            val withX = mWidth - mLeftTextSpace * 2
            val dp20 = dp2px(context, 28).toFloat()
            canvas.drawLine(0f, dp20, withX.toFloat(), dp20, progressBar!!)
            if (mDatas!!.size <= mShowNumber) {
                canvas.drawLine(
                    -5f,
                    dp20,
                    withX + 5.toFloat(),
                    dp20,
                    blueProgressBar!!
                )
            } else {
                val i = (srcollBar / perBarW * 1.5).toInt() + mShowNumber
                var stopX = withX * i / mDatas!!.size
                if (stopX > withX) {
                    stopX = withX
                }
                canvas.drawLine(
                    -5f,
                    dp20,
                    stopX + 5.toFloat(),
                    dp20,
                    blueProgressBar!!
                )
            }
        }
    }

    inner class Builder {
        private var mDesciption: ArrayList<String>? = null
        private var avgData: Double = 0.0
        private var isAnimation = true

        /**
         * TODO  设置滑动的监听
         *
         * @param dragInerfaces
         */
        fun setDragInerfaces(dragInerfaces: DragInerfaces): Builder {
            barChartView?.setDragInerfaces(dragInerfaces)
            return this
        }

        /**
         * TODO 追加 显示的柱状图
         *
         * @param mDatas
         * @param mDesciption
         */
        fun addEndMoreData(mDatas: ArrayList<Double>, mDesciption: ArrayList<String>): Builder {
            barChartView?.addEndMoreData(mDatas, mDesciption)
            return this
        }

        /**
         * TODO  设置滑动条的滑动的距离
         *
         * @param scroBall
         */
        fun setScroBall(scroBall: Int): Builder {
            srcollBar = scroBall
            return  this
        }

        /**
         * TODO 设置柱状图的 数字 和底部显示的内容
         *
         * @param dataS
         * @param desciptionS
         */
        fun setDatas(
            dataS: ArrayList<Double>,
            desciptionS: ArrayList<String>
        ): Builder {
            mDatas = dataS
            mDesciption = desciptionS
            setMaxData()
            return  this
        }

        /**
         * TODO 设置标题
         *
         * @param title
         */
        fun setTitle(title: String): Builder {
            mTitle = title
            return  this
        }

        /**
         * TODO 设置avg title
         *
         * @param title
         */
        fun setAvgTitle(title: String): Builder {
            mAvgTitle = title
            return  this
        }

        /**
         * TODO 设置平均数
         *
         * @param avg
         */
        fun setAvgData(avg: Double): Builder {
            avgData = avg
            return  this
        }

        /**
         * TODO 设置 是否显示动画
         *
         * @param f
         */
        fun setCanAnimation(f: Boolean): Builder {
            isAnimation = f
            return  this
        }

        /**
         * TODO 设置每页显示的柱状图的数量
         *
         * @param num
         */
        fun setShowNumber(num:Int): Builder {
            mShowNumber=num
            return  this
        }

        fun build() {
            if (mDatas != null && mDesciption != null) {
                barChartView?.setDatas(mDatas!!, mDesciption!!, avgData, isAnimation)
                postInvalidate()
            }

        }
    }


//    fun setDatas(
//        mDatas: ArrayList<Double>,
//        mDesciption: ArrayList<String>,
//        avgData: Double,
//        title: String?,
//        isAnimation: Boolean
//    ) {
//        this.mDatas = mDatas
//        setMaxData()
//        mTitle = title
//        mAvgTitle = "平均分数:$avgData"
//        mShowNumber = if (mDatas.size < 6) {
//            mDatas.size
//        } else {
//            6
//        }
//        postInvalidate()
//        barChartView!!.setDatas(mDatas, mDesciption, avgData, isAnimation)
//    }


}
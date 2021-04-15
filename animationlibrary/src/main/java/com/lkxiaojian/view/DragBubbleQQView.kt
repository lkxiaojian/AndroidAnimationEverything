package com.lkxiaojian.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*

import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import com.lkxiaojian.animationlibrary.R
import kotlin.math.hypot


/**
 * create_time : 21-4-15 下午3:19
 * author: lk
 * description： DragBubbleQQView
 */
class DragBubbleQQView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /**
     * 气泡默认状态--静止
     */
    private val BUBBLE_STATE_DEFAULT = 0

    /**
     * 气泡相连
     */
    private val BUBBLE_STATE_CONNECT = 1

    /**
     * 气泡分离
     */
    private val BUBBLE_STATE_APART = 2

    /**
     * 气泡消失
     */
    private val BUBBLE_STATE_DISMISS = 3

    /**
     * 气泡半径
     */
    private var mBubbleRadius = 0f

    /**
     * 气泡颜色
     */
    private var mBubbleColor = 0

    /**
     * 气泡消息文字
     */
    private var mTextStr: String? = null

    /**
     * 气泡消息文字颜色
     */
    private var mTextColor = 0

    /**
     * 气泡消息文字大小
     */
    private var mTextSize = 0f

    /**
     * 不动气泡的半径
     */
    private var mBubFixedRadius = 0f

    /**
     * 可动气泡的半径
     */
    private var mBubMovableRadius = 0f

    /**
     * 不动气泡的圆心
     */
    private var mBubFixedCenter: PointF? = null

    /**
     * 可动气泡的圆心
     */
    private var mBubMovableCenter: PointF? = null

    /**
     * 气泡的画笔
     */
    private  var mBubblePaint: Paint

    /**
     * 贝塞尔曲线path
     */
    private var mBezierPath: Path
    private var mTextPaint: Paint

    //文本绘制区域
    private var mTextRect: Rect

    private var mBurstPaint: Paint

    //爆炸绘制区域
    private var mBurstRect: Rect

    /**
     * 气泡状态标志
     */
    private var mBubbleState = BUBBLE_STATE_DEFAULT

    /**
     * 两气泡圆心距离
     */
    private var mDist = 0f

    /**
     * 气泡相连状态最大圆心距离
     */
    private var mMaxDist = 0f

    /**
     * 手指触摸偏移量
     */
    private var MOVE_OFFSET = 0f

    /**
     * 气泡爆炸的bitmap数组
     */
    private var mBurstBitmapsArray: Array<Bitmap?>



    /**
     * 当前气泡爆炸图片index
     */
    private var mCurDrawableIndex = 0

    /**
     * 气泡爆炸的图片id数组
     */
    private val mBurstDrawablesArray = intArrayOf(
        R.drawable.burst_1,
        R.drawable.burst_2,
        R.drawable.burst_3,
        R.drawable.burst_4,
        R.drawable.burst_5
    )


    init {
        val array: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.DragBubbleQQView)
        mBubbleRadius = array.getDimension(R.styleable.DragBubbleQQView_bubble_radius, mBubbleRadius)
        mBubbleColor = array.getColor(R.styleable.DragBubbleQQView_bubble_color, Color.RED)
        mTextStr = array.getString(R.styleable.DragBubbleQQView_bubble_text)
        mTextSize = array.getDimension(R.styleable.DragBubbleQQView_bubble_textSize, mTextSize)
        mTextColor = array.getColor(R.styleable.DragBubbleQQView_bubble_textColor, Color.WHITE)
        array.recycle()

        //两个圆半径大小一致
        mBubFixedRadius = mBubbleRadius
        mBubMovableRadius = mBubFixedRadius
        mMaxDist = 8 * mBubbleRadius
        MOVE_OFFSET = mMaxDist / 4

        //抗锯齿
        mBubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBubblePaint.color = mBubbleColor
        mBubblePaint.style = Paint.Style.FILL
        mBezierPath = Path()

        //文本画笔
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize
        mTextRect = Rect()

        //爆炸画笔
        mBurstPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBurstPaint.isFilterBitmap = true
        mBurstRect = Rect()
        mBurstBitmapsArray = arrayOfNulls(mBurstDrawablesArray.size)
        for (i in mBurstDrawablesArray.indices) {
            //将气泡爆炸的drawable转为bitmap
            val bitmap = BitmapFactory.decodeResource(getResources(), mBurstDrawablesArray[i])
            mBurstBitmapsArray[i] = bitmap
        }
        init()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init(w, h)
    }

    private fun init(w: Int, h: Int) {
        mBubbleState = BUBBLE_STATE_DEFAULT

        //设置固定气泡圆心初始坐标
        if (mBubFixedCenter == null) {
            mBubFixedCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        } else {
            mBubFixedCenter!![w / 2.toFloat()] = h / 2.toFloat()
        }
        //设置可动气泡圆心初始坐标
        if (mBubMovableCenter == null) {
            mBubMovableCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        } else {
            mBubMovableCenter!![w / 2.toFloat()] = h / 2.toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //1. 连接情况绘制贝塞尔曲线  2.绘制圆背景以及文本 3.另外端点绘制一个圆
        //1. 静止状态  2，连接状态 3，分离状态  4，消失
        if (mBubbleState == BUBBLE_STATE_CONNECT) {
            //绘制静止的气泡
            canvas.drawCircle(
                mBubFixedCenter!!.x,
                mBubFixedCenter!!.y,
                mBubFixedRadius,
                mBubblePaint
            )
            //计算控制点的坐标
            val iAnchorX = (mBubMovableCenter!!.x + mBubFixedCenter!!.x) / 2
            val iAnchorY = (mBubMovableCenter!!.y + mBubFixedCenter!!.y) / 2
            val sinTheta = (mBubMovableCenter!!.y - mBubFixedCenter!!.y) / mDist
            val cosTheta = (mBubMovableCenter!!.x - mBubFixedCenter!!.x) / mDist

            //D
            val iBubFixedStartX = mBubFixedCenter!!.x - mBubFixedRadius * sinTheta
            val iBubFixedStartY = mBubFixedCenter!!.y + mBubFixedRadius * cosTheta
            //C
            val iBubMovableEndX = mBubMovableCenter!!.x - mBubMovableRadius * sinTheta
            val iBubMovableEndY = mBubMovableCenter!!.y + mBubMovableRadius * cosTheta

            //A
            val iBubFixedEndX = mBubFixedCenter!!.x + mBubFixedRadius * sinTheta
            val iBubFixedEndY = mBubFixedCenter!!.y - mBubFixedRadius * cosTheta
            //B
            val iBubMovableStartX = mBubMovableCenter!!.x + mBubMovableRadius * sinTheta
            val iBubMovableStartY = mBubMovableCenter!!.y - mBubMovableRadius * cosTheta
            mBezierPath?.run {
                reset()
                moveTo(iBubFixedStartX, iBubFixedStartY)
                quadTo(
                    iAnchorX,
                    iAnchorY,
                    iBubMovableEndX,
                    iBubMovableEndY
                )
                lineTo(iBubMovableStartX, iBubMovableStartY)
                quadTo(iAnchorX, iAnchorY, iBubFixedEndX, iBubFixedEndY)
                close()
            }
            canvas.drawPath(mBezierPath, mBubblePaint)
        }

        //静止，连接，分离状态都需要绘制圆背景以及文本
        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            canvas.drawCircle(
                mBubMovableCenter!!.x,
                mBubMovableCenter!!.y,
                mBubMovableRadius,
                mBubblePaint
            )

            mTextPaint.getTextBounds(mTextStr, 0, mTextStr!!.length, mTextRect)
            canvas.drawText(
                mTextStr!!,
                mBubMovableCenter!!.x - mTextRect.width() / 2,
                mBubMovableCenter!!.y + mTextRect.height() / 2,
                mTextPaint
            )
        }

        // 认为是消失状态，执行爆炸动画
        if (mBubbleState == BUBBLE_STATE_DISMISS && mCurDrawableIndex < mBurstBitmapsArray.size) {
            mBurstRect.set(
                (mBubMovableCenter!!.x - mBubMovableRadius).toInt(),
                (mBubMovableCenter!!.y - mBubMovableRadius).toInt(),
                (mBubMovableCenter!!.x + mBubMovableRadius).toInt(),
                (mBubMovableCenter!!.y + mBubMovableRadius).toInt()
            )
            canvas.drawBitmap(
                mBurstBitmapsArray[mCurDrawableIndex]!!,
                null,
                mBurstRect,
                mBubblePaint
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (mBubbleState != BUBBLE_STATE_DISMISS) {
                mDist = hypot(
                    event.x - mBubFixedCenter!!.x.toDouble(),
                    event.y - mBubFixedCenter!!.y.toDouble()
                )
                    .toFloat()
                mBubbleState = if (mDist < mBubbleRadius + MOVE_OFFSET) {
                    //加上MOVE_OFFSET是为了方便拖拽
                    BUBBLE_STATE_CONNECT
                } else {
                    BUBBLE_STATE_DEFAULT
                }
            }
            MotionEvent.ACTION_MOVE -> if (mBubbleState != BUBBLE_STATE_DEFAULT) {
                mDist = hypot(
                    event.x - mBubFixedCenter!!.x.toDouble(),
                    event.y - mBubFixedCenter!!.y.toDouble()
                )
                    .toFloat()
                mBubMovableCenter!!.x = event.x
                mBubMovableCenter!!.y = event.y
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    if (mDist < mMaxDist - MOVE_OFFSET) {
                        mBubFixedRadius = mBubbleRadius - mDist / 8
                    } else {
                        mBubbleState = BUBBLE_STATE_APART
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> if (mBubbleState == BUBBLE_STATE_CONNECT) {
                // 橡皮筋动画
                startBubbleRestAnim()
            } else if (mBubbleState == BUBBLE_STATE_APART) {
                if (mDist < 2 * mBubbleRadius) {
                    //反弹动画
                    startBubbleRestAnim()
                } else {
                    // 爆炸动画
                    startBubbleBurstAnim()
                }
            }
        }
        return true
    }

    /**
     * 连接状态下松开手指，执行类似橡皮筋动画
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startBubbleRestAnim() {
        val anim = ValueAnimator.ofObject(
            PointFEvaluator(),
            PointF(mBubMovableCenter!!.x, mBubMovableCenter!!.y),
            PointF(mBubFixedCenter!!.x, mBubFixedCenter!!.y)
        )
        anim.duration = 200
        anim.interpolator = OvershootInterpolator(5f)
        anim.addUpdateListener { animation ->
            mBubMovableCenter = animation.animatedValue as PointF
            invalidate()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mBubbleState = BUBBLE_STATE_DEFAULT
            }
        })
        anim.start()
    }

    /**
     * 爆炸动画
     */
    private fun startBubbleBurstAnim() {
        //将气泡改成消失状态
        mBubbleState = BUBBLE_STATE_DISMISS
        val animator = ValueAnimator.ofInt(0, mBurstBitmapsArray.size)
        animator.interpolator = LinearInterpolator()
        animator.duration = 500
        animator.addUpdateListener { animation ->
            mCurDrawableIndex = animation.animatedValue as Int
            invalidate()
        }
        animator.start()
    }

    fun init() {
        init(width, height)
        invalidate()
    }


}
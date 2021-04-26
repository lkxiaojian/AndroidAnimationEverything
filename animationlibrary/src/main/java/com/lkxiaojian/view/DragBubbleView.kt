package com.lkxiaojian.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.lkxiaojian.animationlibrary.R
import kotlin.math.hypot


/**
 *create_time : 21-4-14 下午4:36
 *author: lk
 *description： DragBubble 仿qq聊天界面的拖拽气泡
 */
@SuppressLint("Recycle", "ResourceAsColor")
class DragBubbleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var mPaintBall: Paint
    private lateinit var mPaintNum: Paint
    private var BALL_STATE = 1// 1静止状态  2 手指滑动 3 消息状态
    private var BALL_LINK_STATE = 1//1链接状态 2分离状态
    private var BALL_DISMISS_STATE = 1 //1 显示 2 消失
    private var showNum: String = ""
    private var ballColor: Int //小球颜色 默认红色
    private var numColor: Int //显示的字体颜色 默认白色
    private var ballRadius: Float
    private var centerX: Float
    private var centerY: Float
    private var centerXEvent: Float = 0f
    private var centerYEvent: Float = 0f
    private var mDistance: Double = 0.0
    private var length = 200f//拖拽距离多少后断开
    private var lastDistance = 0.0
    private var ballRadiusS: Float
    private var ballRadiusF: Float
    private var mBezierPath: Path? = null
    private val SCBL = 0.6f

    init {
        val obtainStyledAttributes =
            context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView)
        val showNumTmp = obtainStyledAttributes.getString(R.styleable.DragBubbleView_dbv_show_num)
        if (!showNumTmp.isNullOrEmpty()) {
            showNum = showNumTmp
        }
        ballColor =
            obtainStyledAttributes.getColor(R.styleable.DragBubbleView_dbv_ball_color, Color.RED)
        numColor =
            obtainStyledAttributes.getColor(R.styleable.DragBubbleView_dbv_num_color,Color.WHITE)
        ballRadius =
            obtainStyledAttributes.getFloat(R.styleable.DragBubbleView_dbv_ball_radius, 30f)
        centerX =
            obtainStyledAttributes.getFloat(R.styleable.DragBubbleView_dbv_ball_centerX, 600f)
        centerY =
            obtainStyledAttributes.getFloat(R.styleable.DragBubbleView_dbv_ball_centerY, 1100f)
        ballRadiusS = ballRadius
        ballRadiusF = ballRadius
        length = ballRadiusS * 8
        initP()
    }

    private fun initP() {

        mPaintBall = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintBall.color = ballColor
        mPaintBall.style = Paint.Style.FILL

        mBezierPath = Path()

        mPaintNum = Paint()
        mPaintNum.color = numColor
        mPaintNum.style = Paint.Style.FILL
        mPaintNum.isAntiAlias = true
        mPaintNum.textAlign = Paint.Align.CENTER
        mPaintNum.strokeWidth = ballRadius
        mPaintNum.textSize = ballRadius
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (BALL_DISMISS_STATE == 2) {
            return
        }
        //1.静止状态 ，一个小球加消息数据
        if (BALL_STATE == 1) {
            canvas.drawCircle(centerX, centerY, ballRadius, mPaintBall)
            val rectF = RectF(
                centerX - ballRadius,
                centerY - ballRadius,
                centerX + ballRadius,
                centerY + ballRadius
            )

            val fontMetrics: Paint.FontMetrics = mPaintNum.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline = rectF.centerY() + distance
            canvas.drawText(showNum, rectF.centerX(), baseline, mPaintNum)
        }
        //2.连接状态， 一个小球加消息数据 ，贝塞尔曲线，本身位置上的小球，大小可变化
        if (BALL_STATE == 2 && BALL_LINK_STATE == 1) {

            if (lastDistance < mDistance) {
                ballRadiusS += SCBL
                ballRadiusF -= SCBL
            } else {
                ballRadiusS -= SCBL
                ballRadiusF += SCBL
            }
            canvas.drawCircle(centerX, centerY, ballRadiusF, mPaintBall)
            val rectF = RectF(
                centerXEvent - ballRadius,
                centerYEvent - ballRadius,
                centerXEvent + ballRadius,
                centerYEvent + ballRadius
            )
            canvas.drawCircle(centerXEvent, centerYEvent, ballRadiusS, mPaintBall)
            val fontMetrics: Paint.FontMetrics = mPaintNum.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline = rectF.centerY() + distance
            canvas.drawText(showNum, rectF.centerX(), baseline, mPaintNum)

            //计算贝塞尔曲线的点（二阶曲线 起点 终点 控制点）
            val cosA = (centerXEvent - centerX) / mDistance
            val sinA = (centerYEvent - centerY) / mDistance
            //A
            val ax = (centerX - sinA * ballRadiusF).toFloat()
            val ay = (centerY + cosA * ballRadiusF).toFloat()
            //B
            val bx = (centerX + sinA * ballRadiusF).toFloat()
            val by = (centerY - cosA * ballRadiusF).toFloat()

            //C
            val cx = (centerXEvent - sinA * ballRadiusS).toFloat()
            val cy = (centerYEvent + cosA * ballRadiusS).toFloat()

            //D
            val dx = (centerXEvent + sinA * ballRadiusS).toFloat()
            val dy = (centerYEvent - cosA * ballRadiusS).toFloat()

            //E 控制点
            val ex = (centerX + centerXEvent) / 2
            val ey = (centerY + centerY) / 2
            mBezierPath?.reset()
            mBezierPath?.moveTo(cx, cy)
            mBezierPath?.quadTo(ex, ey, ax, ay)
            mBezierPath?.lineTo(dx, dy)
            mBezierPath?.quadTo(ex, ey, bx, by)
            mBezierPath?.close()
            canvas.drawPath(mBezierPath!!, mPaintBall)
            lastDistance = mDistance

        }
        //3.分离状态， 一个小球家消息数据
        if (BALL_STATE == 2 && BALL_LINK_STATE == 2) {

        }

        //4.消息状态，爆炸效果
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.e("tag-->","tag-->$centerXEvent  $centerYEvent")

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //气泡还在显示的状态
                if (BALL_DISMISS_STATE == 1) {
                    mDistance =
                        hypot((event.x - centerX).toDouble(), (event.y - centerY).toDouble())
                    if (mDistance < ballRadius) {
                        BALL_STATE = 2
                        BALL_LINK_STATE = 1
                    } else {
                        BALL_STATE = 1
                        BALL_LINK_STATE = 1
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {
                //小球显示状态
                if (BALL_DISMISS_STATE == 1) {
                    mDistance =
                        hypot((event.x - centerX).toDouble(), (event.y - centerY).toDouble())
                    //小球链接
                    if (BALL_LINK_STATE == 1) {
                        //拖拽距离大于规定值 ，把链接状态变成2 未连接状态
                        if (mDistance > length) {
                            BALL_LINK_STATE = 2
                        }
                    } else {
                        //小球未链接状态
                        BALL_LINK_STATE = 2
                    }
                    //改变小球的位置和文字的位置
                    centerXEvent = event.x
                    centerYEvent = event.y
                    invalidate()
                }


            }
            MotionEvent.ACTION_UP -> {
                if (BALL_DISMISS_STATE == 1) {
                    mDistance =
                        hypot((event.x - centerX).toDouble(), (event.y - centerY).toDouble())
                    if (mDistance > length) {
                        //小球消失动画
                        BALL_DISMISS_STATE = 2
                        BALL_STATE = 1
                    } else {
                        //小球复原
                        BALL_STATE = 1
                        BALL_DISMISS_STATE = 1
                    }
                    invalidate()
                }

            }
        }


        return true
    }
}
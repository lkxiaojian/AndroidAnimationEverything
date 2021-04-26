package com.lkxiaojian.view.statistical

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

open class LBaseView : View {
//    var mWidth = 0f
//    var mHeight = 0f

    @JvmField
    protected var scale = 0.5f

    @JvmField
    protected var canClickAnimation = false

    @JvmField
    protected var animator: ValueAnimator? = null

    constructor(context: Context?) : super(context) {
        initAnimation()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAnimation()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAnimation()
    }

    private fun initAnimation() {
        animator = ValueAnimator.ofFloat(0.2f, 1f)
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.duration = 600
        animator?.repeatCount = 0
        animator?.addUpdateListener(AnimatorUpdateListener { animation ->
            scale = animation.animatedValue as Float
            postInvalidate()
        })
    }

    protected fun dp2px(dpValue: Int): Int {
        return context.resources.displayMetrics.density.toInt() * dpValue
    }

    fun setCanAnimation(canClickAnimation: Boolean) {
        this.canClickAnimation = canClickAnimation
    }
}
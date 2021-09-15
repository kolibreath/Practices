package com.kolibreath.miweather

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.lang.Math.abs

class RotateTextView(
    private val mContext: Context,
    private val mAttributeSet: AttributeSet
): View(mContext, mAttributeSet) {

    private val mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.gunner, null)
    private val mCamera = Camera()
    private val mPaint = Paint().apply {
        isAntiAlias = true
    }

    private val mMatrix = Matrix()
    private lateinit var mScaledBitmap: Bitmap

    override fun onDraw(canvas: Canvas) {
      rotateView(canvas)
    }

    private var mViewWidth = 0f
    private var mViewHeight = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 将结果变成一个正方形
        mViewWidth = measuredWidth.toFloat()
        mViewHeight = measuredHeight.toFloat()
        mViewWidth = mViewHeight.coerceAtMost(mViewWidth)
        mViewHeight = mViewWidth

        mCenterX = mViewWidth/2
        mCenterY = mViewHeight/2

        // 通过onMeasure获取的View自动是px
        mScaledBitmap = mBackgroundBitmap
            .scaleBitmap(matrix = mMatrix, contxt = mContext,
                afterWidth = mViewWidth, afterHeight = mViewHeight)
    }

    // onDraw一定在onMeasure之后，我们在这里获取View的宽高，将View中心平移到原点，轴对称旋转
    private fun rotateView(canvas: Canvas){
        mCamera.save()
        canvas.save()

        mCamera.rotateY(mDegree)
        canvas.translate(mCenterX,mCenterY)
        mCamera.applyToCanvas(canvas)
        canvas.translate(-mCenterX,-mCenterY)
        mCamera.restore()

        canvas.drawBitmap(mScaledBitmap, 0f,0f,mPaint)
        canvas.restore()
    }

    private var mFirstDown = 0f
    private val MAX_MOVE = 500f // 应该设置为屏幕五分之一的宽度
    private var mDegree = 0f
//    private var m
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                mFirstDown = event.rawX
            }
            MotionEvent.ACTION_MOVE ->{
                val curMove = event.rawX
                val delta = mFirstDown - curMove
                val scale = delta/MAX_MOVE
                mDegree = 180f*scale
                invalidate()
            }
            // 恢复View的位置
            MotionEvent.ACTION_UP ->{
                playResetAnimation().start()
            }
        }
        return true
    }

    // 当抬起手指时使用动画效果恢复View的状态
    private fun playResetAnimation(): Animator{
        return ValueAnimator.ofFloat(1f,0f).apply {
            duration = 1000
            addUpdateListener {
                mDegree *= (it.animatedValue as Float)
                invalidate()
            }
        }
    }
}


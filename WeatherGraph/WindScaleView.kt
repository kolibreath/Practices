package com.kolibreath.miweather

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class WindScaleView(
    private val contxt: Context,
    private val attributeSet: AttributeSet
): View(contxt, attributeSet) {

    // 获取测量的父View的长度
    private var mParentWidth = 0
    private var mParentHeight = 0

    private val mStartX = 0
    private val mStartY = 0
    // 我们需要处理下载的Bitmap的大小
    // 设置的目标大小应该是5dp*5dp
    private val mMatrix = Matrix()
    private val mArrowBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_up_arrow, null)

    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
    }

    // 外部控制变量
    var mDegree = 0f
    var mWindScaleText = "4级"



    private val mTextRect = Rect()
    override fun onDraw(canvas: Canvas) {

        // 定义缩放后的bitmap+bitmap文字间距+文字的总长度
        val scaledBitmap = mArrowBitmap.scaleBitmap(matrix = mMatrix,
                                                    contxt = contxt,
                                                    afterWidth = 20f,
                                                    afterHeight = 20f, toPx = true)
        val scaledWidth = scaledBitmap.width.toFloat()
        val scaledHeight = scaledBitmap.height.toFloat()
        val dividerSize = scaledWidth*0.3
        val metrics = mPaint.fontMetrics
        mPaint.getTextBounds(mWindScaleText, 0, mWindScaleText.length, mTextRect)
        val textLength = mTextRect.width()
        val wholeLength = scaledWidth + dividerSize + textLength

        // 计算Bitmap 移动到的位置
        val tranX = (mParentWidth - wholeLength)/2
        val tranY = (mParentHeight - scaledHeight)/2

        // Bitmap 会根据风向进行调整旋转角度
        mMatrix.postRotate(mDegree, scaledWidth/2, scaledHeight/2)
        mMatrix.postTranslate(tranX.toFloat(), tranY)
        canvas.drawBitmap(scaledBitmap, mMatrix, mPaint)

        // 绘制风向标边上的文字
        // 绘制文字的对齐方式默认为left
        mPaint.textSize = 32f
        mPaint.strokeWidth = 0f
        mPaint.style = Paint.Style.FILL

        // 确定绘制的baseLine
        val baseline = (mParentHeight-metrics.top-metrics.bottom)/2
        canvas.drawText(mWindScaleText,
                        (mParentWidth-tranX- textLength).toFloat(),
                        baseline, mPaint )
    }

    // 获取的长度是ViewGroup的长度 因为继承自View
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mParentWidth = MeasureSpec.getSize(widthMeasureSpec)
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec)
    }

}
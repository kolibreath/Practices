package com.kolibreath.miweather

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import java.util.Collections.min

class PointView(
    private val mContext: Context,
    private val attrSet: AttributeSet
    ): View(mContext, attrSet) {

    // 考虑当minValue小于0 之类的情况
    // 从外部设置的相关参数
    var curMaxValue = 0
    var lastMaxValue = 0
    var nextMaxValue = 0
    
    var curMinValue = 0
    var lastMinValue = 0
    var nextMinValue = 0
    
    var maxValue = 0
    var minValue = 0
    var mostLeft = false
    var mostRight = false

    private var mTextX = 0f

    // 和点的坐标有关的参数
 
    private var mMaxValueY = 0f
    private var mMinValueY = 0f
    private var mSegmentHeight = 0f


    // Paint设置是ARGB
    private val mLinePaint = Paint().apply{
        isAntiAlias = true
        strokeWidth = 5f
        style = Paint.Style.FILL
    }

    private val mTextPaint = Paint(mLinePaint)
    private val mDotPaint = Paint(mLinePaint)

    // 这个方法一定需要在onMeasure之后调用
    private fun computeY(value: Int): Float {
        // 将整个View分成5段，minValue对应最下面，maxValue的点对应最上面

        return ((maxValue  - value).toFloat() / (maxValue - minValue).toFloat()) *
                (mMinValueY - mMaxValueY) + mSegmentHeight
    }

    // 绘制当前点和其左右的线条
    private fun drawDotAndLine(canvas: Canvas, curValue: Int, lastValue: Int, nextValue: Int){
        val curY = computeY(curValue)
        canvas.drawCircle(mTextX, curY,15f,mDotPaint)

        // 如果既不是最左边也不是最右边的线条
        if(!mostLeft && !mostRight){
            drawLeftLine(canvas, curValue, lastValue)
            drawRightLine(canvas, curValue, nextValue)
        }else if(mostLeft){
            drawRightLine(canvas, curValue, nextValue)
        }else if(mostRight){
            drawLeftLine(canvas, curValue, lastValue)
        }
    }

    private val mTextRect = Rect()
    private fun drawText(canvas: Canvas,  minValue:Int, maxValue:Int){
        val textX = measuredWidth.toFloat()/2
        val minY = computeY(minValue)
        val maxY = computeY(maxValue)
        mTextPaint.textSize = 30f
        // 将数字温度转化成摄氏度°
        val minTemp = "$minValue°"
        val maxTemp = "$maxValue°"
        val maxBaseline = maxY - mContext.dp2px(15f)
        // 需要考虑字体高度的问题
        mTextPaint.getTextBounds(minTemp, 0, minTemp.length, mTextRect)
        var minBaseline = minY + mContext.dp2px(15f) + mTextRect.height()
        // 这里分情况讨论，如果minBaseLine超过了height，字体可能会写到View外边，所以需要写在点上面
        if(minBaseline > measuredHeight)
            minBaseline = minY - mContext.dp2px(15f)
        
        mTextPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(minTemp, textX, minBaseline, mTextPaint)
        canvas.drawText(maxTemp, textX, maxBaseline, mTextPaint)
    }

    private fun drawLeftLine(canvas: Canvas, curValue:Int, lastValue: Int){
        val lastY = computeY(lastValue)
        val curY = computeY(curValue)
        val middleY = (lastY + curY)/2
        canvas.drawLine(0f, middleY, mTextX, curY, mLinePaint)
    }

    private fun drawRightLine(canvas: Canvas, curValue: Int, nextValue: Int){
        val nextY = computeY(nextValue)
        val curY = computeY(curValue)
        val middleY = (nextY + curY)/2
        canvas.drawLine(mTextX, curY, measuredWidth.toFloat(), middleY, mLinePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        mokTest()

        mSegmentHeight = measuredHeight.toFloat() / 5
        mMaxValueY = mSegmentHeight
        mMinValueY = mSegmentHeight * 4f
        mTextX = measuredWidth.toFloat()/2
    }

    override fun onDraw(canvas: Canvas) {
        // 绘制最大值的曲线
        drawDotAndLine(canvas, curValue = curMaxValue,
            lastValue = lastMaxValue, nextValue =  nextMaxValue)
        // 绘制最小值的曲线
        drawDotAndLine(canvas, curValue =  curMinValue, 
            lastValue = lastMinValue, nextValue = nextMinValue)
        
        drawText(canvas, minValue = curMinValue, maxValue = curMaxValue)
    }

    // mock value 测试
    private fun mockTest(){
        // 全局最小
        minValue = 0
        maxValue = 30

        curMaxValue = 19
        curMinValue = 13

        lastMaxValue = 30
        lastMinValue = 12

        nextMaxValue = 30
        nextMinValue = 3
    }

}
package com.example.yishutansuo

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import java.lang.Math.abs

class ScrollerLayoutKt(
    private val contxt: Context,
    private val attributeSet: AttributeSet
): ViewGroup(contxt, attributeSet) {

    private var leftBorder  = 0
    private var rightBorder = 0

    //被认为可以的最小滑动距离
    private val mTouchSlop = ViewConfiguration.get(contxt).scaledTouchSlop

    private val mScroller = Scroller(contxt)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 计算每一个控件的大小
        val count = childCount
        for(i in 0 until count){
            val view = getChildAt(i)
            measureChild(view,widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // 完成测量之后可以获取到view的长度，同时初始化边界信息
        // 进行每一个子View的布局
        if (changed) {
            val count = childCount
            for (i in 0 until count) {
                val view = getChildAt(i)
                // 水平方向上布局，排排队，后面的view可能超出屏幕之外
                val left = measuredWidth * i
                val top = 0
                val right = measuredWidth * (i + 1)
                val bottom = view.measuredHeight
                view.layout(left, top, right, bottom)
            }
            // 在我们的例子中，由于ScrollerLayout放在左上角，leftBorder = 0
            // rightBorder = count * width
            leftBorder = getChildAt(0).left
            rightBorder = getChildAt(count - 1).right
        }
    }

    private var mDownX = 0
    private var mMoveX = 0
    private var mLastMoveX = 0

    // 拦截点击事件
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action){
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.rawX.toInt()
                // 可能的情况是点击之后立马滑动，所以要赋值
                mLastMoveX = mDownX
            }
            MotionEvent.ACTION_MOVE -> {
                mMoveX = ev.rawX.toInt()
                val deltaX = kotlin.math.abs(mMoveX - mLastMoveX)
                mLastMoveX = mMoveX
                if (deltaX > mTouchSlop) return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_MOVE ->{
                mMoveX = event.rawX.toInt()
                // 滑动距离
                val scrolledX = mLastMoveX - mMoveX
                // 边界控制
                // 如果滑动距离太长，甚至小于了左边界
                if (scrolledX + scrollX < leftBorder) {
                    scrollTo(leftBorder, 0)
                    return true
                }
                // 如果滑动距离太长，甚至大于于了右边界
                else if(scrolledX + scrollX + width> rightBorder) {
                    scrollTo(rightBorder - width, 0)
                    return true
                }

                scrollBy(scrolledX, 0)
                mLastMoveX = mMoveX
            }
            MotionEvent.ACTION_UP ->{
                val childIndex = (scrollX + width / 2) / width
                val deltaX = childIndex * width - scrollX
                mScroller.startScroll(scrollX, 0 , deltaX, 0)
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
    

    override fun computeScroll() {

        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }
}
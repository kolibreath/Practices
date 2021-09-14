package com.kolibreath.miweather.custom_recycler_view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.core.view.forEach
import com.kolibreath.miweather.R
import java.lang.IllegalArgumentException
import java.lang.Math.abs
import java.lang.Math.min
import java.lang.RuntimeException

// 问题:
// 1. MyRecyclerView为什么clickable要设置为true?
class MyRecyclerView(
    private val mContext: Context,
    private val mAttributeSet: AttributeSet
): ViewGroup(mContext, mAttributeSet) {

    // 需要重新布局的场景：
    // 1. RecyclerView刚刚初始化
    // 2. 填充了新的数据： 设置了新的适配器
    private var mNeedRelayout = true
    // 缓存出现在屏幕上的数据
    private val mItemViewList: ArrayList<View> = ArrayList()
    private var mAdapter: MyRecyclerAdapter? = null

    private var mItemCount = 0
    private lateinit var mHeights: IntArray

    // RecyclerView的宽高
    private var mWidth = 0
    private var mHeight = 0

    // 不同的手机因为dpi不同，所以最小像素不同
    // 最小滑动距离
    private var mTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop

    private lateinit var mRecycler: Recycler
    fun setAdapter(adapter: MyRecyclerAdapter){
        mNeedRelayout = true
        mAdapter = adapter
        mItemCount = adapter.getItemCount()
        mHeights = IntArray(mItemCount) { 0 }

        // 实例化Recycler
        mRecycler = Recycler(adapter.getItemViewTypeCount())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    // 实现ViewGroup肯定要重写onLayout
    // 注意要点： ViewGroup View的三大方法中的最好不要实例化变量
    // onLayout的业务逻辑:
    // 1. 负责对于测量完成的子View的位置确定
    //  1.1  如果上一次布局完成，但是需要重新布局
    //  1.2 如果上次没有布局 这一次是全新的布局
    // 2. 获取View的实例  从回收池 还是新实例化？
    // 3. 确定第一屏的加载数量
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(mNeedRelayout || changed) { // 如果父容器发生变化或者需要重新布局
            mNeedRelayout = false
            mItemViewList.clear()
            removeAllViews() // 清除所有的子View
            if (mAdapter != null) {
               // 子View布局的确定需要知道View的高度（应该在测量阶段能获取
                for(i in 0 until mItemCount){
                    mHeights[i] += mAdapter!!.getItemHeight(i)
                }

                // QUESTION 为什么RecyclerView的宽高在这里被确定？
                mWidth  = r - l
                mHeight = b - t

                // 当前遍历到的View的top和bottom
                var itemViewTop = 0
                var itemViewBottom = 0

                for(i in 0 until mItemCount){
                    itemViewBottom = itemViewTop + mHeights[i]
                    // 这里假设每一个View之间紧密相连
                    val itemView = makeAndSetupView(i, l,itemViewTop,r,itemViewBottom)
                    itemViewTop = itemViewBottom
                    mItemViewList.add(itemView)
                    addView(itemView, 0) // 注意addView的问题
                    // 下一个view的高度超过RecyclerView的Height
                    if(itemViewTop > mHeight) break
                }
            }
        }
    }

    private fun makeAndSetupView(
        index: Int, // 数据的index和row是相同的
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): View{
        val itemView = obtain(index, right-left, bottom-top)
        itemView.layout(left, top, right, bottom)
        return itemView
    }

    // 先访问缓存 如果缓存
    // QUESTION 使用LayoutInflater 不会产生宽高？
    // 从Recycler中 取出View
    private fun obtain(row: Int, width: Int, height: Int): View{

        val recycledView: View? =  mRecycler.reuse(mAdapter!!.getItemViewType(row))
        val desiredView = if(recycledView == null)
            mAdapter!!.onCreateViewHolder(row, recycledView, this)
        else
            mAdapter!!.onBindViewHolder(row, recycledView, this)

        val viewType = mAdapter!!.getItemViewType(row)
        // 这里的Key必须一定在xml中
        desiredView
            .setTag(R.id.tag_view_type, viewType ) // 这里不能够直接调用View.setTag(Object)
        // QUESTION 为什么在obtain中测量
        desiredView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )

//        if(recycledView!=null) {
//            val textView: View?
//            if (row % 2 == 1) {
//                textView = desiredView.findViewById<TextView>(R.id.tv_item1)
//                Log.d("TEST", "case#${row % 2} ${textView.text} ")
//            } else {
//                textView = desiredView.findViewById<TextView>(R.id.tv_item0)
//                Log.d("TEST", "case#${row % 2} ${textView.text} ")
//
//            }
//            Log.d("TEST", "case#${row % 2} ${textView.width} ${textView.height} ")
//        }


        return desiredView
    }

    // 向RecyclerView中存放View
    override fun removeView(view: View?) {
        super.removeView(view)
        val viewType = view!!.getTag(R.id.tag_view_type) as Int
        mRecycler.save(viewType, view)
    }

    // 滑动和点击的区别使用TouchSlop
    // 这里View的布局是竖向布局，所以我们应该计算y轴上的delta
    // 如果子View的item可以消费滑动事件的实现
    private var mLastMoveY = 0
    private var deltaY = 0
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = false
        when(ev.action){
            MotionEvent.ACTION_DOWN -> {
                val curY = ev.rawY
                mLastMoveY = curY.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                val curY = ev.rawY
                // 大于0 说明向下滑动
                // 小于0 说明向上滑动
                deltaY = (mLastMoveY - curY).toInt()
                Log.d("TEST", deltaY.toString())
                intercept = kotlin.math.abs(deltaY) > mTouchSlop
                mLastMoveY = curY.toInt()
            }
        }
        return intercept
    }

    private var mFirstRow = 0 // 第一个可见元素在数据中的索引
    private var mScrollY = 0  // 第一个可见元素距离屏幕最上方的位置

    // 计算从start之后count个View的高度
    private fun sumViewHeights(start: Int, count: Int): Int {
        val end = start + count
        var sum = 0
        for(i in start until end){
            sum += mHeights[i]
        }
        return sum
    }

    // 屏幕上的所有view都包含在ViewList中
    // 获取屏幕上的最后一个View（这个View可能没有完全显示在屏幕上）的bottom到屏幕最顶上的距离
    private fun getViewHeights() = sumViewHeights(mFirstRow, mItemViewList.size) - mScrollY

    // 计算当ScrollY不同的时候的的边界情况
    // 计算核心就是判断【剩余部分】能不能填充屏幕的空白
    // 1. 如果是向下滑动mScrollY < 0， mFirst到第一个数据（View）的高度，如果这个高度小于 mScrollY
    // 说明不足以滑动这么长的距离
    // 2. 如果是向上滑动 计算屏幕之外的长度，屏幕之外的长度等于mfirst到最后一个数据长度减去屏幕长
    private fun scrollBounds(): Int{
        val absoluteScrollY = kotlin.math.abs(mScrollY)
        if(mScrollY < 0){ // 向下滑动
            val restHeight = sumViewHeights(0, mFirstRow) // 正
            if(restHeight < absoluteScrollY) mScrollY = -restHeight // 取负
        }else{
            // 等于0 或者向上滑动
            val restHeight = sumViewHeights(mFirstRow, mAdapter!!.getItemCount()-mFirstRow)
            val outsideHeight = restHeight - mHeight // 减去屏幕高度 得到真实的填充大小
            mScrollY = if(outsideHeight < 0) 0 // 已经没有可以填充的高度了
            else min(outsideHeight, mScrollY)
        }
        return mScrollY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_MOVE ->{
                val curY = event.rawY
                deltaY = ( mLastMoveY-curY).toInt()
                scrollBy(0, deltaY)
            }
        }
        return super.onTouchEvent(event)
    }

    // 思路： 需要重写scrollBy方法，因为原本的方法是处理Canvas的位置
    // 首先要进行边界处理
    // 1. 当向上滑动时  填充下方的位置 并且移除出上界的View
    // 2. 当向下滑动时 填充上方的位置，移除出下界的View
    override fun scrollBy(x: Int, deltaY: Int) {
        mScrollY += deltaY
        mScrollY = scrollBounds()

        if(mScrollY >= 0){ // 向上滑动
            // 如果滑动的高度大于一个Item的高度，将它从ViewList中移除
            while(mHeights[mFirstRow] < mScrollY) {
                if (mItemViewList.isNotEmpty()) {
                    val removedView: View = mItemViewList.removeFirst()
                    removeView(removedView)
                    mScrollY -= mHeights[mFirstRow]
                    mFirstRow++
                }
            }

            // 同时还需要负责添加新的View
            while(getViewHeights() < mHeight){
                // 找到屏幕之外的第一个View
                val firstOutsideIndex = mFirstRow + mItemViewList.size
                val view = obtain(firstOutsideIndex, mWidth, mHeight)
                mItemViewList.add(view)
                addView(view,0)
                Log.d("TEST", ((view as ViewGroup).getChildAt(0) as TextView).text.toString())
                Log.d("TEST", ((view as ViewGroup).getChildAt(0) as TextView).height.toString())
            }
        }else{ // 向下滑动，如果最后一个Item被划出去了
            while(getViewHeights() - mHeight > mHeights[mFirstRow+mItemViewList.size-1]) {
                if (mItemViewList.isNotEmpty()) {
                    val removedView = mItemViewList.removeLast()
                    removeView(removedView)
                }
            }

            // 填充新的view
            while(0>mScrollY){
                mFirstRow --
                val view = obtain(mFirstRow, mWidth, mHeight)
                mItemViewList.add(0, view)
                addView(view, 0)
                mScrollY += mHeights[mFirstRow+1]
            }
        }
        relayoutViews()
    }

    private fun relayoutViews(){
        val left = 0
        var top = -mScrollY
        val right = mWidth
        var bottom = 0

        var i = 0
        for(view in mItemViewList){
            bottom = top + mHeights[i++]
            view.layout(left, top, right, bottom)
            top = bottom
        }
    }


}
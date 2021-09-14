package com.kolibreath.miweather.custom_recycler_view

import android.view.View
import android.view.ViewGroup

// 通常的Adapter需要什么功能：
// 1. onCreateViewHolder 加载对应的布局
// 2. onBindViewHolder 绑定布局
// 3. getItemType 返回布局类型
// 4. getItemCount 返回子View数量
interface MyRecyclerAdapter {

    fun onCreateViewHolder(row: Int, convertView: View?, parent: ViewGroup): View
    fun onBindViewHolder(row: Int, convertView: View?, parent: ViewGroup): View
    fun getItemViewType(row: Int): Int // 获取具体的ViewType类型
    fun getItemViewTypeCount(): Int  // 获取的具体的ViewType类型数量
    fun getItemCount(): Int

    // 暂时使用默认的高度，之后实现对于子View的测量
    fun getItemHeight(row:Int): Int
}
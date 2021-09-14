package com.kolibreath.miweather.custom_recycler_view

import android.view.View
import java.util.*


class Recycler(viewTypeCount: Int) {

    private val mViewStacks = Array<Stack<View>>(viewTypeCount){ Stack() }

    fun save(viewType: Int, view: View){
        mViewStacks[viewType].push(view)
    }

    // 需要先判断Stack中有无内容
//    fun reuse(viewType: Int):View =  mViewStacks[viewType].pop()
    fun reuse(viewType: Int): View?{
        return try{
            mViewStacks[viewType].pop()
        }catch(e: Exception){
            null
        }
    }
}
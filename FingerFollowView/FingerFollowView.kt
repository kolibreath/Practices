package com.example.yishutansuo

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller

class FingerFollowView(
    val contxt: Context,
    val attributeSet: AttributeSet
): View(contxt, attributeSet) {

    private var lastX = 0
    private var lastY = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        val deltaX = x-lastX
        val deltaY = y-lastY

        when(event.action){
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {
                val translationX = translationX + deltaX
                val translationY = translationY + deltaY

                setTranslationX(translationX)
                setTranslationY(translationY)
            }
            MotionEvent.ACTION_UP -> {}
            else ->{}
        }
        lastX = x
        lastY = y
        return true
    }
}
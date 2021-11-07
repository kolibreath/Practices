package com.kolibreath.apttest

import android.app.Activity
import android.view.View
import com.kolibreath.annotations.OnClick
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// 实际上执行构造器
class ButterKnife {
    companion object {
        fun bind(activity: Activity) {
            val binderName = "${activity::class.java.name}\$ViewBinder"
            val clazz = Class.forName(binderName)
            val constructor = clazz.getConstructor(activity::class.java)
            constructor.newInstance(activity)

            injectListener(activity)
        }

        private fun injectListener(activity: Activity) {
            // 获取当前Activity所有注解了OnClick的方法对象
            val declaredMethods = activity::class.java.declaredMethods
            for(declaredMethod in declaredMethods) {
                val annotations = declaredMethod.annotations
                for(annotation in annotations) {
                    if(annotation !is OnClick) continue

                    val listenerType = annotation.listenerType
                    val listenerSetter = annotation.listenerSetter
                    val values = annotation.values

                    // 通过控件Id实例化View
                    for(id in values) {
                        // 通过findViewById方法实例化View
                        val findViewById = activity::class.java.getMethod(
                            "findViewById",
                            Int::class.java
                        )
                        val view = findViewById.invoke(activity, id)

                        val invocationHandler =
                            InvocationHandler { _, method, args -> declaredMethod.invoke(activity, *(args ?: emptyArray())) }

                        val listener = Proxy.newProxyInstance(
                            listenerType::class.java.classLoader,
                            arrayOf(listenerType.javaObjectType),
                            invocationHandler
                        )

                        // view.setOnClickListener
                        val listenerMethod = view::class.java.getMethod(listenerSetter, listenerType.javaObjectType)
                        listenerMethod.invoke(view, listener)

                    }
                }
            }
        }
    }
}
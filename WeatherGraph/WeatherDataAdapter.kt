package com.kolibreath.miweather

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherDataAdapter(
    private var mContext: Activity,
    mData: DailyWeatherData
)
    : RecyclerView.Adapter<WeatherDataAdapter.WeatherViewHolder>() {

    // 从api获取到的数据要分成两个部分，一个是最高温度的变化曲线，一个是最低温度的变化曲线
    private val mMinTempData: ArrayList<Int> = ArrayList()
    private val mMaxTempData: ArrayList<Int> = ArrayList()

    private var mEverydayData: ArrayList<Daily> = mData.daily as ArrayList<Daily>

    init {
        mEverydayData.forEach {
            // Integer.parseInt
            mMinTempData.add(it.tempMin.toInt())
            mMaxTempData.add(it.tempMax.toInt())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = mContext.layoutInflater.inflate(R.layout.item_weather,null)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        // 给View绑定数据
        // 之后使用DataBinding省去这些样板代码
        val view = holder.itemView
        val tvDay = view.findViewById<TextView>(R.id.tv_day)         // 昨天 今天 周一 周二  ...
        val tvDate = view.findViewById<TextView>(R.id.tv_date)       // 具体的时间日期
        val ivDay = view.findViewById<ImageView>(R.id.iv_icon_day)
        val pv = view.findViewById<PointView>(R.id.pv)
        val ivNight = view.findViewById<ImageView>(R.id.iv_icon_night)
        val tvWeatherDay = view.findViewById<TextView>(R.id.tv_day_weather) // 晴 阴 等
        val tvWeatherNight = view.findViewById<TextView>(R.id.tv_night_weather)
        val wv = view.findViewById<WindScaleView>(R.id.wv)

        // 初始化pointView
        initPointView(pv, position)

        val daily = mEverydayData[position]
        val curDate = long2Date(fxDate2long(daily.fxDate))
        tvDate.text = curDate

        val curDay = long2Weekday(fxDate2long(daily.fxDate))
        tvDay.text = curDay

        initIcons(ivDay, ivNight, daily)

        tvWeatherDay.text = daily.textDay
        tvWeatherNight.text = daily.textNight

        wv.mWindScaleText = daily.windScaleDay
        wv.mDegree = daily.wind360Day.toFloat()
    }

    // 根据返回的iconDay和iconNight确定ImageView加载的内容
    private fun initIcons(ivDay: ImageView, ivNight: ImageView, daily: Daily){
        ivDay.setBackgroundResource(mContext.drawableId("w${daily.iconDay}"))
        ivNight.setBackgroundResource(mContext.drawableId("w${daily.iconNight}"))
    }

    private fun initPointView(pv: PointView, position: Int){
        // 设置曲线数据
        if (position == mMaxTempData.size - 1) pv.mostRight = true
        if (position == 0) pv.mostLeft = true
        pv.maxValue = mMaxTempData.maxOf { it }
        pv.minValue = mMinTempData.maxOf { it }
        pv.curMinValue = mEverydayData[position].tempMin.toInt()
        pv.curMaxValue = mEverydayData[position].tempMax.toInt()
        if (position != 0) {
            pv.lastMinValue = mMinTempData[position - 1]
            pv.lastMaxValue = mMaxTempData[position - 1]
        }

        if (position != mMaxTempData.size - 1) {
            pv.nextMinValue = mMinTempData[position + 1]
            pv.nextMaxValue = mMaxTempData[position + 1]
        }
    }

    override fun getItemCount(): Int = mEverydayData.size

    class WeatherViewHolder(private val mItemView: View):
        RecyclerView.ViewHolder(mItemView)
}
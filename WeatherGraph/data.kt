package com.kolibreath.miweather

import com.google.gson.Gson

// 从Api获取的数据不能看昨天的数据
// 第二天打开App的时候做持久化处理
data class DailyWeatherData(
    val code: String,
    val daily: List<Daily>,
    val fxLink: String,
    val refer: Refer,
    val updateTime: String
)

data class Daily(
    val cloud: String,
    val fxDate: String,
    val humidity: String,
    val iconDay: String,
    val iconNight: String,
    val moonPhase: String,
    val moonrise: String,
    val moonset: String,
    val precip: String,
    val pressure: String,
    val sunrise: String,
    val sunset: String,
    val tempMax: String,
    val tempMin: String,
    val textDay: String,
    val textNight: String,
    val uvIndex: String,
    val vis: String,
    val wind360Day: String,
    val wind360Night: String,
    val windDirDay: String,
    val windDirNight: String,
    val windScaleDay: String,
    val windScaleNight: String,
    val windSpeedDay: String,
    val windSpeedNight: String
)

data class Refer(
    val license: List<String>,
    val sources: List<String>
)

//mock data 本地测试
private val mockData = "{\n" +
        "    \"code\": \"200\",\n" +
        "    \"updateTime\": \"2021-09-08T18:35+08:00\",\n" +
        "    \"fxLink\": \"http://hfx.link/2ax1\",\n" +
        "    \"daily\": [\n" +
        "        {\n" +
        "            \"fxDate\": \"2021-09-08\",\n" +
        "            \"sunrise\": \"05:48\",\n" +
        "            \"sunset\": \"18:36\",\n" +
        "            \"moonrise\": \"06:46\",\n" +
        "            \"moonset\": \"19:34\",\n" +
        "            \"moonPhase\": \"峨眉月\",\n" +
        "            \"tempMax\": \"28\",\n" +
        "            \"tempMin\": \"19\",\n" +
        "            \"iconDay\": \"100\",\n" +
        "            \"textDay\": \"晴\",\n" +
        "            \"iconNight\": \"101\",\n" +
        "            \"textNight\": \"多云\",\n" +
        "            \"wind360Day\": \"180\",\n" +
        "            \"windDirDay\": \"南风\",\n" +
        "            \"windScaleDay\": \"1-2\",\n" +
        "            \"windSpeedDay\": \"3\",\n" +
        "            \"wind360Night\": \"225\",\n" +
        "            \"windDirNight\": \"西南风\",\n" +
        "            \"windScaleNight\": \"1-2\",\n" +
        "            \"windSpeedNight\": \"3\",\n" +
        "            \"humidity\": \"93\",\n" +
        "            \"precip\": \"0.0\",\n" +
        "            \"pressure\": \"1001\",\n" +
        "            \"vis\": \"25\",\n" +
        "            \"cloud\": \"14\",\n" +
        "            \"uvIndex\": \"6\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"fxDate\": \"2021-09-09\",\n" +
        "            \"sunrise\": \"05:49\",\n" +
        "            \"sunset\": \"18:34\",\n" +
        "            \"moonrise\": \"07:57\",\n" +
        "            \"moonset\": \"20:01\",\n" +
        "            \"moonPhase\": \"峨眉月\",\n" +
        "            \"tempMax\": \"27\",\n" +
        "            \"tempMin\": \"18\",\n" +
        "            \"iconDay\": \"305\",\n" +
        "            \"textDay\": \"小雨\",\n" +
        "            \"iconNight\": \"305\",\n" +
        "            \"textNight\": \"小雨\",\n" +
        "            \"wind360Day\": \"180\",\n" +
        "            \"windDirDay\": \"南风\",\n" +
        "            \"windScaleDay\": \"1-2\",\n" +
        "            \"windSpeedDay\": \"3\",\n" +
        "            \"wind360Night\": \"0\",\n" +
        "            \"windDirNight\": \"北风\",\n" +
        "            \"windScaleNight\": \"1-2\",\n" +
        "            \"windSpeedNight\": \"3\",\n" +
        "            \"humidity\": \"66\",\n" +
        "            \"precip\": \"1.0\",\n" +
        "            \"pressure\": \"1004\",\n" +
        "            \"vis\": \"25\",\n" +
        "            \"cloud\": \"55\",\n" +
        "            \"uvIndex\": \"3\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"fxDate\": \"2021-09-10\",\n" +
        "            \"sunrise\": \"05:50\",\n" +
        "            \"sunset\": \"18:33\",\n" +
        "            \"moonrise\": \"09:08\",\n" +
        "            \"moonset\": \"20:29\",\n" +
        "            \"moonPhase\": \"峨眉月\",\n" +
        "            \"tempMax\": \"28\",\n" +
        "            \"tempMin\": \"20\",\n" +
        "            \"iconDay\": \"100\",\n" +
        "            \"textDay\": \"晴\",\n" +
        "            \"iconNight\": \"101\",\n" +
        "            \"textNight\": \"多云\",\n" +
        "            \"wind360Day\": \"180\",\n" +
        "            \"windDirDay\": \"南风\",\n" +
        "            \"windScaleDay\": \"1-2\",\n" +
        "            \"windSpeedDay\": \"3\",\n" +
        "            \"wind360Night\": \"90\",\n" +
        "            \"windDirNight\": \"东风\",\n" +
        "            \"windScaleNight\": \"1-2\",\n" +
        "            \"windSpeedNight\": \"3\",\n" +
        "            \"humidity\": \"76\",\n" +
        "            \"precip\": \"0.0\",\n" +
        "            \"pressure\": \"1005\",\n" +
        "            \"vis\": \"24\",\n" +
        "            \"cloud\": \"8\",\n" +
        "            \"uvIndex\": \"6\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"refer\": {\n" +
        "        \"sources\": [\n" +
        "            \"QWeather\",\n" +
        "            \"NMC\",\n" +
        "            \"ECMWF\"\n" +
        "        ],\n" +
        "        \"license\": [\n" +
        "            \"no commercial use\"\n" +
        "        ]\n" +
        "    }\n" +
        "}"

fun getMockData(): DailyWeatherData{
    val gson = Gson()
    return gson.fromJson(mockData, DailyWeatherData::class.java) as DailyWeatherData
}
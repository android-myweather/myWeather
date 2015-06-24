package pku.ss.kevin.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import pku.ss.kevin.bean.Weather;
import pku.ss.kevin.bean.WeatherInfo;

public class ParseUtil {

    public static WeatherInfo queryTodayWeather(String xmlData) {
        WeatherInfo todayWeather = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            int fengliCount = 0;
            int fengxiangCount = 0;
            int dateCount = 0;
            int lowCount = 0;
            int highCount = 0;
            int typeCount = 0;
            int zhishuNameCount=0;
            int zhishuValueCount=0;
            int zhishuDetailCount=0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = xmlPullParser.getName();
                        switch (tag) {
                            case "resp":
                                todayWeather = new WeatherInfo();
                                break;
                            case "city":
                                todayWeather.setCity(xmlPullParser.nextText());
                                break;
                            case "updatetime":
                                todayWeather.setUpdateTime(xmlPullParser.nextText());
                                break;
                            case "wendu":
                                todayWeather.setTemperature(xmlPullParser.nextText());
                                break;
                            case "fengli":
                                if (fengliCount == 0)
                                    todayWeather.setWindStrength(xmlPullParser.nextText());
                                fengliCount++;
                                break;
                            case "shidu":
                                todayWeather.setHumidity(xmlPullParser.nextText());
                                break;
                            case "fengxiang":
                                if (fengxiangCount == 0)
                                    todayWeather.setWindDirection(xmlPullParser.nextText());
                                fengxiangCount++;
                                break;
                            case "pm25":
                                todayWeather.setPm25(xmlPullParser.nextText());
                                break;
                            case "quality":
                                todayWeather.setQuality(xmlPullParser.nextText());
                                break;
                            case "date":
                                if (dateCount == 0)
                                    todayWeather.setDate(xmlPullParser.nextText());
                                dateCount++;
                                break;
                            case "low":
                                if (lowCount == 0)
                                    todayWeather.setLow(xmlPullParser.nextText().substring(3));
                                lowCount++;
                                break;
                            case "high":
                                if (highCount == 0)
                                    todayWeather.setHigh(xmlPullParser.nextText().substring(3));
                                highCount++;
                                break;
                            case "type":
                                if (typeCount == 0)
                                    todayWeather.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 1)
                                    todayWeather.setNightType(xmlPullParser.nextText());
                                typeCount++;
                                break;
                            case "name":
                                if (zhishuNameCount<5)
                                    todayWeather.setName(zhishuNameCount,xmlPullParser.nextText());
                                zhishuNameCount++;
                                break;
                            case "value":
                                if (zhishuValueCount<5)
                                    todayWeather.setValue(zhishuValueCount,xmlPullParser.nextText());
                                zhishuValueCount++;
                                break;
                            case "detail":
                                if (zhishuDetailCount<5)
                                    todayWeather.setDetail(zhishuDetailCount,xmlPullParser.nextText());
                                zhishuDetailCount++;
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    public static Weather queryYesterdayWeather(String xmlData) {
        Weather yesterday = new Weather();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            int fengliCount = 0;
            int fengxiangCount = 0;
            int typeCount = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = xmlPullParser.getName();
                        switch (tag) {
                            case "fx_1":
                                if (fengxiangCount == 0)
                                    yesterday.setWindDirection(xmlPullParser.nextText());
                                fengxiangCount++;
                                break;
                            case "fl_1":
                                if (fengliCount == 0)
                                    yesterday.setWindStrength(xmlPullParser.nextText());
                                fengliCount++;
                                break;
                            case "date_1":
                                yesterday.setDate(xmlPullParser.nextText());
                                break;
                            case "high_1":
                                yesterday.setHigh(xmlPullParser.nextText().substring(3));
                                break;
                            case "low_1":
                                yesterday.setLow(xmlPullParser.nextText().substring(3));
                                break;
                            case "type_1":
                                if (typeCount == 0)
                                    yesterday.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 1)
                                    yesterday.setNightType(xmlPullParser.nextText());
                                typeCount++;
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yesterday;
    }

    public static Weather[] query4dayWeather(String xmlData) {
        Weather day1 = new Weather();
        Weather day2 = new Weather();
        Weather day3 = new Weather();
        Weather day4 = new Weather();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            int fengliCount = 0;
            int fengxiangCount = 0;
            int dateCount = 0;
            int lowCount = 0;
            int highCount = 0;
            int typeCount = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = xmlPullParser.getName();
                        switch (tag) {
                            case "date":
                                if (dateCount == 1)
                                    day1.setDate(xmlPullParser.nextText());
                                else if (dateCount == 2)
                                    day2.setDate(xmlPullParser.nextText());
                                else if (dateCount == 3)
                                    day3.setDate(xmlPullParser.nextText());
                                else if (dateCount == 4)
                                    day4.setDate(xmlPullParser.nextText());
                                dateCount++;
                                break;
                            case "high":
                                if (highCount == 1)
                                    day1.setHigh(xmlPullParser.nextText().substring(3));
                                else if (highCount == 2)
                                    day2.setHigh(xmlPullParser.nextText().substring(3));
                                else if (highCount == 3)
                                    day3.setHigh(xmlPullParser.nextText().substring(3));
                                else if (highCount == 4)
                                    day4.setHigh(xmlPullParser.nextText().substring(3));
                                highCount++;
                                break;
                            case "low":
                                if (lowCount == 1)
                                    day1.setLow(xmlPullParser.nextText().substring(3));
                                else if (lowCount == 2)
                                    day2.setLow(xmlPullParser.nextText().substring(3));
                                else if (lowCount == 3)
                                    day3.setLow(xmlPullParser.nextText().substring(3));
                                else if (lowCount == 4)
                                    day4.setLow(xmlPullParser.nextText().substring(3));
                                lowCount++;
                                break;
                            case "fengli":
                                if (fengliCount == 2)
                                    day1.setWindStrength(xmlPullParser.nextText());
                                else if (fengliCount == 4)
                                    day2.setWindStrength(xmlPullParser.nextText());
                                else if (fengliCount == 6)
                                    day2.setWindStrength(xmlPullParser.nextText());
                                else if (fengliCount == 8)
                                    day2.setWindStrength(xmlPullParser.nextText());
                                fengliCount++;
                                break;
                            case "fengxiang":
                                if (fengxiangCount == 2)
                                    day1.setWindDirection(xmlPullParser.nextText());
                                else if (fengxiangCount == 4)
                                    day2.setWindDirection(xmlPullParser.nextText());
                                else if (fengxiangCount == 6)
                                    day3.setWindDirection(xmlPullParser.nextText());
                                else if (fengxiangCount == 8)
                                    day4.setWindDirection(xmlPullParser.nextText());
                                fengxiangCount++;
                                break;
                            case "type":
                                if (typeCount == 2)
                                    day1.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 3)
                                    day1.setNightType(xmlPullParser.nextText());
                                else if (typeCount == 4)
                                    day2.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 5)
                                    day2.setNightType(xmlPullParser.nextText());
                                else if (typeCount == 6)
                                    day3.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 7)
                                    day3.setNightType(xmlPullParser.nextText());
                                else if (typeCount == 8)
                                    day4.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 9)
                                    day4.setNightType(xmlPullParser.nextText());
                                typeCount++;
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Weather[]{day1, day2, day3, day4};
    }

    public static WeatherInfo queryWeatherInfo(String xmlData) {
        WeatherInfo weatherInfo = queryTodayWeather(xmlData);
        weatherInfo.setYesterday(queryYesterdayWeather(xmlData));
        Weather[] days = query4dayWeather(xmlData);
        weatherInfo.setDay1(days[0]);
        weatherInfo.setDay2(days[1]);
        weatherInfo.setDay3(days[2]);
        weatherInfo.setDay4(days[3]);
        return weatherInfo;
    }
}

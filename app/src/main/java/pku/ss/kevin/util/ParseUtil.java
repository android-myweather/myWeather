package pku.ss.kevin.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import pku.ss.kevin.bean.TodayWeather;

public class ParseUtil {

    // 这个得改
    public static TodayWeather queryTodayWeather(String xmlData) {
        TodayWeather todayWeather = null;
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
                            case "resp":
                                todayWeather = new TodayWeather();
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

}

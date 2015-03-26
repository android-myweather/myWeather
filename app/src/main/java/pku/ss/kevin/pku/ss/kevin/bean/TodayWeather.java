package pku.ss.kevin.pku.ss.kevin.bean;

/**
 * Created by Kevin on 2015/3/20.
 */
public class TodayWeather {
    private String city;
    private String updateTime;
    private String temperature;
    private String humidity;
    private String pm25;
    private String quality;
    private String windDirection;
    private String windStrength;
    private String date;
    private String high;
    private String low;
    private String dayType;
    private String nightType;

    @Override
    public String toString() {
        return "city:" + city + " updateTime:" + updateTime + " temperature:"
                + temperature + " humidity:" + humidity + " pm2.5:"
                + pm25 + " quality:" + quality + " windDirection:"
                + windDirection + " windStrength:" + windStrength + " date:" + date + " low:"
                + low + " high:" + high + " day_type:" + dayType + " night_type:" + nightType;
    }

    public String getCity() {
        return city;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPm25() {
        return pm25;
    }

    public String getQuality() {
        return quality;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindStrength() {
        return windStrength;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getDayType() {
        return dayType;
    }

    public String getNightType() {
        return nightType;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setWindStrength(String windStrength) {
        this.windStrength = windStrength;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public void setNightType(String nightType) {
        this.nightType = nightType;
    }
}

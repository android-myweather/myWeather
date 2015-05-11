package pku.ss.kevin.bean;

public class TodayWeather extends Weather{

    protected String city;
    protected String updateTime;
    protected String temperature;
    protected String humidity;
    protected String pm25;
    protected String quality;

    public TodayWeather() {
        super();
        city = "N/A";
        updateTime = "N/A";
        temperature = "N/A";
        humidity = "N/A";
        pm25 = "N/A";
        quality = "N/A";
    }

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

}

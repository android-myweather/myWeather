package pku.ss.kevin.bean;

public class TodayWeather extends Weather{

    protected String city;
    protected String updateTime;
    protected String temperature;
    protected String humidity;
    protected String pm25;
    protected String quality;
    protected String name[];
    protected String value[];
    protected String detail[];

    public TodayWeather() {
        super();
        city = "N/A";
        updateTime = "N/A";
        temperature = "N/A";
        humidity = "N/A";
        pm25 = "N/A";
        quality = "N/A";
        name=new String[5];
        name[0]= "N/A";
        name[1]= "N/A";
        name[2]= "N/A";
        name[3]= "N/A";
        name[4]= "N/A";
        value=new String[5];
        value[0]= "N/A";
        value[1]= "N/A";
        value[2]= "N/A";
        value[3]= "N/A";
        value[4]= "N/A";
        detail=new String[5];
        detail[0]="N/A";
        detail[1]="N/A";
        detail[2]="N/A";
        detail[3]="N/A";
        detail[4]="N/A";
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

    public String getName(int i) {
        return name[i];
    }

    public String getValue(int i) {
        return value[i];
    }

    public String getDetail(int i) { return detail[i]; }

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

    public void setName(int i,String name) {
        this.name[i] = name;
    }

    public void setValue(int i,String value) {
        this.value[i] = value;
    }

    public void setDetail(int i,String detail) {
        this.detail[i] = detail;
    }

}

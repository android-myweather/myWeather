package pku.ss.kevin.bean;

public class Weather {

    protected String windDirection;
    protected String windStrength;
    protected String date;
    protected String high;
    protected String low;
    protected String dayType;
    protected String nightType;

    public Weather() {
        windDirection = "N/A";
        windStrength = "N/A";
        date = "N/A";
        high = "N/A";
        low = "N/A";
        dayType = "N/A";
        nightType = "N/A";
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

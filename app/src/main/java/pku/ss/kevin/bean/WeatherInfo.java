package pku.ss.kevin.bean;

public class WeatherInfo extends TodayWeather{

    private Weather yesterday;
    private Weather day1;
    private Weather day2;
    private Weather day3;
    private Weather day4;

    public WeatherInfo() {
        super();
        yesterday = new Weather();
        day1 = new Weather();
        day2 = new Weather();
        day3 = new Weather();
        day4 = new Weather();
    }

    public Weather getYesterday() {
        return yesterday;
    }

    public Weather getDay1() {
        return day1;
    }

    public Weather getDay2() {
        return day2;
    }

    public Weather getDay3() {
        return day3;
    }

    public Weather getDay4() {
        return day4;
    }

    public void setYesterday(Weather yesterday) {
        this.yesterday = yesterday;
    }

    public void setDay1(Weather day1) {
        this.day1 = day1;
    }

    public void setDay2(Weather day2) {
        this.day2 = day2;
    }

    public void setDay3(Weather day3) {
        this.day3 = day3;
    }

    public void setDay4(Weather day4) {
        this.day4 = day4;
    }
}

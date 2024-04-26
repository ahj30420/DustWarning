package dto;

import java.time.LocalDateTime;

public class AlertInfo {

    //날짜
    private LocalDateTime date;

    //측정소(구별)
    private String station;

    //측정소코드
    private int code;

    //PM10
    private int pm10;

    //PM2.5
    private int pm2_5;

    public AlertInfo(LocalDateTime date, String station, int code, int pm10, int pm2_5) {
        this.date = date;
        this.station = station;
        this.code = code;
        this.pm10 = pm10;
        this.pm2_5 = pm2_5;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPm10() {
        return pm10;
    }

    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }

    public int getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(int pm2_5) {
        this.pm2_5 = pm2_5;
    }
}

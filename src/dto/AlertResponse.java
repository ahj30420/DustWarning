package dto;

import java.time.LocalDateTime;

public class AlertResponse {

    private String station;

    private String level;

    private LocalDateTime date;

    public AlertResponse(String station, String level, LocalDateTime date) {
        this.station = station;
        this.level = level;
        this.date = date;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}

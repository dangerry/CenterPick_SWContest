package ddwucom.contest.centerpick.subway;

import java.io.Serializable;

public class Item implements Serializable {
    String subwayStationName;
    String subwayStationId;
    String subwayRouteName;
    double stationX;
    double stationY;

    public String getSubwayStationName() {
        return subwayStationName;
    }
    public void setSubwayStationName(String subwayStationName) {
        this.subwayStationName = subwayStationName;
    }

    public String getSubwayStationId() {
        return subwayStationId;
    }
    public void setSubwayStationId(String subwayStationId) {
        this.subwayStationId = subwayStationId;
    }

    public String getSubwayRouteName() {
        return subwayRouteName;
    }
    public void setSubwayRouteName(String subwayRouteName) {
        this.subwayRouteName = subwayRouteName;
    }

    public double getStationX() {
        return stationX;
    }
    public void setStationX(double stationX) {
        this.stationX = stationX;
    }

    public double getStationY() {
        return stationY;
    }
    public void setStationY(double stationY) {
        this.stationY = stationY;
    }

    public String toString(){
        String rslt = "";
        rslt += subwayStationName +", "+subwayStationId+", "+stationX+", "+stationY+", "+subwayRouteName;
        return rslt;
    }
}

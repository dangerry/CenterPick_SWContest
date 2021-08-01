package ddwucom.contest.centerpick.data;

public class TimeData {
    private String start_address; //시작 주소
    private int take_time; //걸리는 시간
    private long _id;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public TimeData(String start_address, int take_time) {
        this.start_address = start_address;
        this.take_time = take_time;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    public int getTake_time() {
        return take_time;
    }

    public void setTake_time(int take_time) {
        this.take_time = take_time;
    }
}

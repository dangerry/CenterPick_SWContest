package ddwucom.contest.centerpick.data;

public class UserData {

    private long _id;
    private String address_name;
    private String address_number;


    public UserData(String address_number) {
        this.address_number = address_number;
    }

//    public UserData(String address_name, String address_number)
//    {
//        this.address_name = address_name;
//        this.address_number = address_number;
//    }

    public long get_id(){
        return _id;
    }
    public String getAddress_name(){
        return this.address_name;
    }
    public String getAddress_number(){ return this.address_number; }

    public void set_id(long _id) {
        this._id = _id; }

    public void setAddress_name(String address_name) {
        this.address_name = address_name; }

    public void setAddress_number(String address_number) {
        this.address_number = address_number; }
}
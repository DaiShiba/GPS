package tigerapplication2.yomogi.co.jp.gps.Json;

/**GSON Parse用のクラス*/
public class RestaurantInfo {
    public String id;
    public String update_date;
    public String name;
    public String category;
    public String latitude;
    public String longitude;
    public String address;

    public RestaurantInfo(String id, String update_date, String name, String category,
                          String latitude, String longitude, String address) {
        this.id = id;
        this.update_date = update_date;
        this.name = name;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    @Override
    public String toString() {
        return "id;" + id + ";update_date;" + update_date + ";name;" + name + ";category;" + category +
                ";latitude;" + latitude + "longitude" + longitude + "address" + address;
    }
}

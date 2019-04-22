package tigerapplication2.yomogi.co.jp.gps;

/**
 * Geofewnce登録用の座標とコンテンツの登録Mockデータ
 *
 * Geofence登録時のユニークなキーに本クラスのNameを使う
 * Geofence受信時にキーをもとに本クラスのコンテンツを取得する
 */
public enum GeofenceEnumMock {
    COMODO(1,35.4583256,139.6209998,"コモド横浜サウス","会社の寮"),
    LANDMARK_TOWER(2,35.455024,139.631524,"横浜ランドマークタワー","麻婆豆腐が美味しい☆"),
    YOKOHAMA_STATION(3,35.465836,139.621890,"横浜駅","美味しいお店がたくさん");

    public final int REQUEST_ID;    /**固有ID */
    public final double LATITUDE;   /**緯度 */
    public final double LONGITUDE;  /**経度 */
    public final String NAME;       /**建物名 */
    public final String DETAILS;    /**表示するコンテンツ */

    GeofenceEnumMock(int request_code, double latitude, double longitude, String shop_name, String unique) {
        REQUEST_ID = request_code;
        LATITUDE = latitude;
        LONGITUDE = longitude;
        NAME = shop_name;
        DETAILS = unique;
    }

    /**Enumの固有名を返却 */
    public String getName() { return this.name();}

    /**受け取ったEnumの固有名から該当するEnumを返却 */
    public static GeofenceEnumMock getEnum(String str) {
        GeofenceEnumMock[] enumArray = GeofenceEnumMock.values();
        for(GeofenceEnumMock anEnum : enumArray) {
            // 引数とenum型の文字列部分を比較し、一致するものを返却
            if (str.equals(anEnum.getName())){
                return anEnum;
            }
        }
        return null;
    }
}

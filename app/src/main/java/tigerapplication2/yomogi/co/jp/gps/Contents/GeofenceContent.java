package tigerapplication2.yomogi.co.jp.gps.Contents;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantEnum;

/**
Geofence情報一覧用のコンテンツ
 */
public class GeofenceContent {
    private static final String LOG_TAG = GeofenceContent.class.getSimpleName();
    public static List<GeofenceItem> geofenceItemArrayList = new ArrayList<>();

    public GeofenceContent(Cursor cursor) {
        geofenceItemArrayList = new ArrayList<>();

        boolean mov = cursor.moveToFirst();
        while (mov) {
            Log.d(LOG_TAG,"GeofenceItem");
            Log.d(LOG_TAG,"ID;" + cursor.getString(RestaurantEnum.ID.getColumnId()));
            Log.d(LOG_TAG,"UPDATE;" + cursor.getString(RestaurantEnum.UPDATE.getColumnId()));
            Log.d(LOG_TAG,"NAME;" + cursor.getString(RestaurantEnum.NAME.getColumnId()));
            Log.d(LOG_TAG,"CATEGORY:" + cursor.getString(RestaurantEnum.CATEGORY.getColumnId()));
            Log.d(LOG_TAG,"LATITUDE:" + cursor.getString(RestaurantEnum.LATITUDE.getColumnId()));
            Log.d(LOG_TAG,"LONGITUDE:" + cursor.getString(RestaurantEnum.LONGITUDE.getColumnId()));
            Log.d(LOG_TAG,"ADDRESS:" + cursor.getString(RestaurantEnum.ADDRESS.getColumnId()));

            geofenceItemArrayList.add(new GeofenceItem(
                            cursor.getString(RestaurantEnum.ID.getColumnId()),
                            cursor.getString(RestaurantEnum.UPDATE.getColumnId()),
                            cursor.getString(RestaurantEnum.NAME.getColumnId()),
                            cursor.getString(RestaurantEnum.CATEGORY.getColumnId()),
                            cursor.getString(RestaurantEnum.LATITUDE.getColumnId()),
                            cursor.getString(RestaurantEnum.LONGITUDE.getColumnId()),
                            cursor.getString(RestaurantEnum.ADDRESS.getColumnId())
            ));
            mov = cursor.moveToNext();
        }
    }

    /**Geofence一覧用アイテム*/
    public static class GeofenceItem {
        public String id;
        public String update_date;
        public String name;
        public String category;
        public String latitude;
        public String longitude;
        public String address;

        public GeofenceItem(String id, String update_date, String name, String category,
                            String latitude, String longitude, String address) {
            this.id = id;
            this.update_date = update_date;
            this.name = name;
            this.category = category;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }
    }
}

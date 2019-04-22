package tigerapplication2.yomogi.co.jp.gps.Contents;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static tigerapplication2.yomogi.co.jp.gps.Database.DatabaseDefineEnum.*;

/**
 *位置情報一覧に詰め込むアイテム
 */
public class LocationContent {

    public static List<LocationItem> ITEMS = new ArrayList<LocationItem>();
    private static int id = 1;

    //Cursorを引数にLocationInfoリストを生成
    public LocationContent(Cursor cursor){
        //最終更新日時用のフォーマッタ
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ITEMS = new ArrayList<>();
        id = 1;

        boolean mov = cursor.moveToFirst();
        while (mov) {
            //String型のエポックミリ秒を変換
            String beforeDate = cursor.getString(UPDATE_DATE.getColumnId());
            Date date = new Date(Long.valueOf(beforeDate));
            String afterDate = fmt.format(date);

            //LocationItemリストを生成
            addItem(new LocationItem(String.valueOf(id++), afterDate,
                            cursor.getString(LATITUDE.getColumnId()),
                            cursor.getString(LONGITUDE.getColumnId()),
                            cursor.getString(ALTITUDE.getColumnId())));
            mov = cursor.moveToNext();
        }
    }

    /**リストに追加*/
    private static void addItem(LocationItem item) {
        ITEMS.add(item);
    }

    /**リストを返却*/
    public List<LocationItem> getItems(){return ITEMS;}

    /**LocationContentsの内部クラス*/
    public static class LocationItem {
        public final String id;
        public final String time;
        public final String latitude;
        public final String longitude;
        public final String altitude;

        //LocationItemコンストラクタ
        private LocationItem(String id, String time, String latitude , String longitude , String altitude) {
            this.id = id;
            this.time = time;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }

        @Override
        public String toString() {
            return time;
        }
    }
}

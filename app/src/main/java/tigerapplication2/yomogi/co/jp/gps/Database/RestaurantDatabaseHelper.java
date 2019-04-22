package tigerapplication2.yomogi.co.jp.gps.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import tigerapplication2.yomogi.co.jp.gps.Json.RestaurantInfo;

/**Geofence登録用レストラン情報DB*/
public class RestaurantDatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = RestaurantDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "RestaurantInfo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Shop";

    private static RestaurantDatabaseHelper sSingleton = null;
    private static SQLiteDatabase sqLiteDatabase;

    //シングルトン
    public static synchronized RestaurantDatabaseHelper getInstance(Context context) {
        Log.d(LOG_TAG,"getInstance Called");
        if (sSingleton == null) {
            sSingleton = new RestaurantDatabaseHelper(context);
            sqLiteDatabase = sSingleton.getWritableDatabase();
            Log.d(LOG_TAG,"Instance Created");
        }
        return sSingleton;
    }

    //コンストラクタ
    private RestaurantDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //DBの生成
    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + RestaurantEnum.ID.getColumnKey() + " TEXT NOT NULL,"
                + RestaurantEnum.UPDATE.getColumnKey() + " TEXT,"
                + RestaurantEnum.NAME.getColumnKey() + " TEXT,"
                + RestaurantEnum.CATEGORY.getColumnKey() + " TEXT,"
                + RestaurantEnum.LATITUDE.getColumnKey() + " TEXT,"
                + RestaurantEnum.LONGITUDE.getColumnKey() + " TEXT,"
                + RestaurantEnum.FLG.getColumnKey() + " INTEGER,"
                + RestaurantEnum.ADDRESS.getColumnKey() + " TEXT);");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }

    /**指定されたカテゴリーをもとに一致するGeofence情報をDBから取得*/


    /**新規で取得したレストラン情報を保存*/
    public void insertInformation(ArrayList<RestaurantInfo> restaurantInfoArrayList) {
        Log.d(LOG_TAG,"insertInformation Called");
        Log.d(LOG_TAG,"restaurantInfoArrayList size;" + restaurantInfoArrayList.size());
        for(RestaurantInfo restaurantInfo : restaurantInfoArrayList) {

            //保存済みレストラン情報テーブルからIDが一致するものを検索
            Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                    null,
                    RestaurantEnum.ID.getColumnKey() + " = ?",
                    new String[] {restaurantInfo.id},
                    null,
                    null,
                    null,
                    null);

            Log.d(LOG_TAG,"getCount" + cursor.getCount());

            //すでに保存済みのレストラン情報であれば、スキップ
            if(cursor.getCount() > 0) {
                Log.d(LOG_TAG,"SKIP info Insert");
            } else {
                if(!restaurantInfo.latitude.isEmpty() && !restaurantInfo.longitude.isEmpty()){
                    Log.d(LOG_TAG,"Start info Insert");
                    Log.d(LOG_TAG,restaurantInfo.toString());
                    //DB登録用ContentValuesの生成
                    ContentValues values = new ContentValues();
                    values.put(RestaurantEnum.ID.getColumnKey(),restaurantInfo.id);
                    values.put(RestaurantEnum.UPDATE.getColumnKey(),restaurantInfo.update_date);
                    values.put(RestaurantEnum.NAME.getColumnKey(),restaurantInfo.name);
                    values.put(RestaurantEnum.CATEGORY.getColumnKey(),restaurantInfo.category);
                    values.put(RestaurantEnum.LATITUDE.getColumnKey(),restaurantInfo.latitude);
                    values.put(RestaurantEnum.LONGITUDE.getColumnKey(),restaurantInfo.longitude);
                    values.put(RestaurantEnum.FLG.getColumnKey(),1);
                    values.put(RestaurantEnum.ADDRESS.getColumnKey(), restaurantInfo.address);

                    //DB Insert
                    long id = sqLiteDatabase.insert(TABLE_NAME,null, values);
                    Log.d(LOG_TAG,"InfoInserted id ;" + id);
                }else{
                    Log.e(LOG_TAG,"Field null(Latitude or Longitude)");

                }
            }
        }

        Cursor cur = sqLiteDatabase.query(TABLE_NAME,
                RestaurantEnum.getColumnKeyList(),
                null,
                null,
                null,
                null,
                null,
                null);
        Log.d(LOG_TAG,"現在のDB保存状況：" + cur.getCount());

        while(cur.moveToNext()){
            Log.d(LOG_TAG,cur.getString(RestaurantEnum.NAME.columnId));
        }
    }

    //指定されたカテゴリに部分一致する情報を返却
    public Cursor getRestaurantInfo(String category) {
        Log.d(LOG_TAG, "getRestaurantInfo Called");

        String selection;
        String[] selectionArgs;

        if(category.equals("")) {
            //カテゴリー指定なしの場合
            selection = null;
            selectionArgs = null;
        } else {
            //カテゴリー指定ありの場合
            selection = RestaurantEnum.CATEGORY.getColumnKey() + " like ?";
            //部分一致にするため成型
            category = "%" + category + "%";
            selectionArgs = new String[] {category};
        }

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                RestaurantEnum.getColumnKeyList(),
                selection,
                selectionArgs,
                null,
                null,
                null,
                null);

        return cursor;
    }

    //指定されたIDに一致する情報を返却
    public Cursor getRestaurantInfoByID(String id) {
        Log.d(LOG_TAG, "getRestaurantInfoByID Called");

        String selection;
        String[] selectionArgs;

        selection = RestaurantEnum.ID.getColumnKey() + " = ?";
        selectionArgs = new String[] {id};

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                RestaurantEnum.getColumnKeyList(),
                selection,
                selectionArgs,
                null,
                null,
                null,
                null);

        return cursor;
    }

}

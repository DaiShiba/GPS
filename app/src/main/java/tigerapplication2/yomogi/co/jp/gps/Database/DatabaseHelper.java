package tigerapplication2.yomogi.co.jp.gps.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

/**DatabaseHelper*/
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "LocationInfo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "History";
    private static final String COLUMN_KEY_DATE;
    private static final String COLUMN_KEY_LATITUDE;
    private static final String COLUMN_KEY_LONGITUDE;
    private static final String COLUMN_KEY_ALTITUDE;
    private static DatabaseHelper sSingleton = null;
    private static SQLiteDatabase sqLiteDatabase;

    //データベースの各カラム名を定義
    static {
        COLUMN_KEY_DATE = DatabaseDefineEnum.UPDATE_DATE.getColumnKey();
        COLUMN_KEY_LATITUDE = DatabaseDefineEnum.LATITUDE.getColumnKey();
        COLUMN_KEY_LONGITUDE = DatabaseDefineEnum.LONGITUDE.getColumnKey();
        COLUMN_KEY_ALTITUDE = DatabaseDefineEnum.ALTITUDE.getColumnKey();
    }

    //シングルトン
    public static synchronized DatabaseHelper getInstance(Context context) {
        Log.d(LOG_TAG,"getInstance Called");
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context);
            sqLiteDatabase = sSingleton.getWritableDatabase();
            Log.d(LOG_TAG,"Instance Created");
        }
        return sSingleton;
    }

    //コンストラクタ
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //DBの生成
    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_KEY_DATE + " TEXT NOT NULL,"
                + COLUMN_KEY_LATITUDE + " TEXT,"
                + COLUMN_KEY_LONGITUDE + " TEXT,"
                + COLUMN_KEY_ALTITUDE + " TEXT);");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }

    /**位置情報履歴を更新日時が新しい順に DATA_COUNT件数分 取得*/
    public Cursor getHistory(final String dataCount) {
        Log.d(LOG_TAG,"getHistory Called");
        return sqLiteDatabase.query(TABLE_NAME,
                DatabaseDefineEnum.getColumnKeyList(),
                null,
                null,
                null,
                null,
                COLUMN_KEY_DATE + " DESC",
                dataCount);
    }

    /**位置情報登録*/
    public void insertLocation(LocationResult locationResult) {
        Log.d(LOG_TAG,"insertLocation Called");
        // 計測日時・緯度・経度・高度を取得
        long time = locationResult.getLastLocation().getTime();
        double latitude = locationResult.getLastLocation().getLatitude();
        double longitude = locationResult.getLastLocation().getLongitude();
        double altitude = locationResult.getLastLocation().getAltitude();

        //DB登録用ContentValuesの生成
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEY_DATE,String.valueOf(time));
        values.put(COLUMN_KEY_LATITUDE,String.valueOf(latitude));
        values.put(COLUMN_KEY_LONGITUDE,String.valueOf(longitude));
        values.put(COLUMN_KEY_ALTITUDE,String.valueOf(altitude));

        //DB Insert
        sqLiteDatabase.insert(TABLE_NAME,null, values);
    }
}

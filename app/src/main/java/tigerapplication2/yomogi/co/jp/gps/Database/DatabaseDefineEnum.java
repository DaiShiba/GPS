package tigerapplication2.yomogi.co.jp.gps.Database;

/**位置情報管理用DBの定義用Enumクラス*/
public enum DatabaseDefineEnum {

    //DBのカラム名 Activity表示用ラベル
    UPDATE_DATE("time","最終更新日時",0),
    LATITUDE("latitude","緯度",1),
    LONGITUDE("longitude","経度",2),
    ALTITUDE("altitude","高度",3);

    //DB KeyName
    public final String columnKey;
    //表示用ラベル名
    public final String label;
    //DB ColumnID
    public final int columnId;

    //コンストラクタ
    DatabaseDefineEnum(String columnKey, String label ,int columnId) {
        this.columnKey = columnKey;
        this.label = label;
        this.columnId = columnId;
    }

    //DBのカラム名を配列で取得
    public static String[] getColumnKeyList(){
        return new String[]{ UPDATE_DATE.getColumnKey(), LATITUDE.getColumnKey(),
                             LONGITUDE.getColumnKey(), ALTITUDE.getColumnKey()}; }
    //DBのカラム名を取得
    public String getColumnKey(){return columnKey;}
    //Activity表示用ラベルを戻す
    public String getLabel(){return label;}
    //DBのカラム番号を返却
    public int getColumnId(){return columnId;}
}

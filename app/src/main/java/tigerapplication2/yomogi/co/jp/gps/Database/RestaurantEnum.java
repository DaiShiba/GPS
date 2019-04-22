package tigerapplication2.yomogi.co.jp.gps.Database;

/**レストラン情報DB定義用Enum*/
public enum RestaurantEnum {

    ID("id",0),
    UPDATE("time",1),
    NAME("name",2),
    CATEGORY("category",3),
    LATITUDE("latitude",4),
    LONGITUDE("longitude",5),
    FLG("flg",6),
    ADDRESS("address",7);

    //DB KeyName
    public final String columnKey;
    //DB ColumnID
    public final int columnId;

    //コンストラクタ
    RestaurantEnum(String columnKey,int columnId) {
        this.columnKey = columnKey;
        this.columnId = columnId;
    }

    //DBのカラム名を配列で取得
    public static String[] getColumnKeyList(){
        return new String[]{ ID.getColumnKey(), UPDATE.getColumnKey(),
                NAME.getColumnKey(), CATEGORY.getColumnKey(),
                LATITUDE.getColumnKey(), LONGITUDE.getColumnKey(), FLG.getColumnKey(), ADDRESS.getColumnKey()}; }
    //DBのカラム名を取得
    public String getColumnKey(){return columnKey;}
    //DBのカラム番号を返却
    public int getColumnId(){return columnId;}
}

package tigerapplication2.yomogi.co.jp.gps.Json;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class RestaurantJsonParser {
    private static final String LOG_TAG = RestaurantJsonParser.class.getSimpleName();

    public ArrayList<RestaurantInfo> jsonRestParser(String result) {
        ArrayList<RestaurantInfo> restaurantInfoArrayList = new ArrayList<>();

        //JsonObject {}
        JsonObject jsonObj = new Gson().fromJson(result, JsonObject.class);

        //JsonArray []
        JsonArray jsonAry = jsonObj.get("rest").getAsJsonArray();
        Log.d(LOG_TAG,"jsonAry : " + jsonAry);
        Log.d(LOG_TAG,"----------RestaurantInfo Ary----------");
        for(JsonElement jsonElement : jsonAry) {
            RestaurantInfo restaurantInfo = new Gson().fromJson(jsonElement, RestaurantInfo.class);
            Log.d(LOG_TAG,"RestaurantInfo；" + restaurantInfo.toString());
            restaurantInfoArrayList.add(restaurantInfo);
        }

        Log.d(LOG_TAG,"restaurantInfoArrayList.size；" + restaurantInfoArrayList.size());
        return restaurantInfoArrayList;
    }
}

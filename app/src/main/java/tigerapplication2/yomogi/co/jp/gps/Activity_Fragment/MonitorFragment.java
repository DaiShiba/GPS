package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import tigerapplication2.yomogi.co.jp.gps.Preference.LastLocationPreference;
import tigerapplication2.yomogi.co.jp.gps.Preference.SettingsPreference;
import tigerapplication2.yomogi.co.jp.gps.R;

/**位置情報検知のTOP Fragment*/
public class MonitorFragment extends Fragment{

    private MonitorViewModel mViewModel;
    private static boolean isGPSEnabled = false;
    private Activity activity;
    private static final String LOG_TAG = MonitorFragment.class.getSimpleName();

    public static MonitorFragment newInstance() {
        return new MonitorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView Called");
        return inflater.inflate(R.layout.monitor_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated Called");
        mViewModel = ViewModelProviders.of(getActivity()).get(MonitorViewModel.class);
        activity = getActivity();

        // 緯度・経度・高度をSharedPrefernceから取得
        SharedPreferences locationSharedPreferences = LastLocationPreference.getThisPreference(activity);
        long lastDate = locationSharedPreferences.getLong(LastLocationPreference.UPDATE_DATE.name(), 0);
        double lastLatitude = Double.longBitsToDouble(locationSharedPreferences.
                getLong(LastLocationPreference.LATITUDE.name(), 0));
        double lastLongitude = Double.longBitsToDouble(locationSharedPreferences.
                getLong(LastLocationPreference.LONGITUDE.name(), 0));
        double lastAltitude = Double.longBitsToDouble(locationSharedPreferences.
                getLong(LastLocationPreference.ALTITUDE.name(), 0));

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TextView textView = activity.findViewById(R.id.fragmentTopText);
        if(lastDate != 0){
            if (textView != null) {
                textView.setText("更新日時" + fmt.format(new Date(lastDate)) +
                        "\nLatitude     : N" + String.valueOf(lastLatitude).replace(".","°") +
                        "\nLongitude  : N" + String.valueOf(lastLongitude).replace(".","°") +
                        "\nAltitude      :  " + String.valueOf(lastAltitude));
            }
        }

        //GPSのON/OFFボタン
        //GPSの設定値をSharedPreferenceより取得
        SharedPreferences sharedPreferences = SettingsPreference.getThisPreference(activity);
        isGPSEnabled = sharedPreferences.getBoolean(SettingsPreference.LOCATION_ENABLED.name(),false);

        final ImageButton imageButton =  activity.findViewById(R.id.imageButton);

        if (isGPSEnabled) {
            imageButton.setImageResource(R.drawable.map_enabled);
        }else {
            imageButton.setImageResource(R.drawable.map_disabled);
        }

        //TOPのImageButton(1秒の連打防止)
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        imageButton.setEnabled(true);
                    }
                }, 1000L);
                isGPSEnabled = !isGPSEnabled;

                SharedPreferences sharedPreferences = SettingsPreference.getThisPreference(activity);
                ImageButton imageButton =  activity.findViewById(R.id.imageButton);

                if(isGPSEnabled){
                    Toast.makeText(activity,"有効にしました",Toast.LENGTH_LONG).show();
                    sharedPreferences.edit().putBoolean(SettingsPreference.LOCATION_ENABLED.name(), true).apply();
                    mViewModel.location.postValue(true);
                    imageButton.setImageResource(R.drawable.map_enabled);
                }else{
                    Toast.makeText(activity,"無効にしました",Toast.LENGTH_LONG).show();
                    sharedPreferences.edit().putBoolean(SettingsPreference.LOCATION_ENABLED.name(), false).apply();
                    mViewModel.location.postValue(false);
                    imageButton.setImageResource(R.drawable.map_disabled);
                }
            }
        });
    }

}

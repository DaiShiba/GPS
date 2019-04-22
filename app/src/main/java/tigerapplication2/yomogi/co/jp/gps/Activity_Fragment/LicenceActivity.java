package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tigerapplication2.yomogi.co.jp.gps.Intent.IntentUtility;
import tigerapplication2.yomogi.co.jp.gps.Preference.SettingsPreference;
import tigerapplication2.yomogi.co.jp.gps.R;

/**利用許諾Activity */
public class LicenceActivity extends AppCompatActivity {
    private final String LOG_TAG = LicenceActivity.class.getSimpleName();
    private final String TOP_FILE_NAME = "top_map.png";
    private final int REQUEST_CODE_FINE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);
        Log.d(LOG_TAG,"onCreate Start");

        //アプリの初回起動判定
        if(AppLaunchChecker.hasStartedFromLauncher(getApplicationContext())){
            //2回目以降の起動の場合はPermissionの許諾情報を確認

            //Permission確認
            int permissionCheck = ContextCompat.checkSelfPermission(LicenceActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                //許諾済みの場合、NavigationTop画面に遷移
                startNavigation();
            }
        }else{
            //初回起動の場合は、起動済みFlagを立てる
            AppLaunchChecker.onActivityCreate(this);
        }

        //利用許諾画面のTOP画像をAssetsより取得
        ImageView topImage = findViewById(R.id.topPanel);
        Log.d(LOG_TAG,"Bitmap Create Start");
        try(InputStream is = this.getAssets().open(TOP_FILE_NAME)){
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            topImage.setImageBitmap(bitmap);
            Log.d(LOG_TAG,"Bitmap Created Success");
        }catch (IOException e){
            Log.d(LOG_TAG,e.getMessage());
        }

        //ButterKnifeアノテーションを付けたものを、ActivityにBind
        ButterKnife.bind(this);

    }

    /**同意するボタンタップ*/
    @OnClick(R.id.button)
    void click(View view){
        int permissionCheck = ContextCompat.checkSelfPermission(LicenceActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(LicenceActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_FINE);
        }else{
            //許諾済みの場合、NavigationTop画面に遷移
            /** 本アプリ起動前に設定アプリより権限付与しているユースケース */
            startNavigation();
        }
    }

    /**Permissionダイアログの結果受け取り*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("requestCode").append(requestCode)
                .append("permissions").append(permissions)
                .append("grantResults").append(grantResults.toString());

        int permissionCheck = ContextCompat.checkSelfPermission(LicenceActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            //同意ボタン押下契機で位置情報取得をONにする
            SharedPreferences sharedPreferences = SettingsPreference.getThisPreference(this);
            sharedPreferences.edit().putBoolean(SettingsPreference.LOCATION_ENABLED.name(),true).apply();

            /** 権限付与ができた場合のユースケース */
            startNavigation();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(LOG_TAG,"true");
            }else{
                ConstraintLayout layout = findViewById(R.id.ConstraintLayout);
                final Snackbar snackbar = Snackbar.make(layout, "権限の許諾が必要です", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(Color.GRAY);
                TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.CYAN);
                snackbar.setAction("『設定アプリを開く』", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentUtility.openSettings(LicenceActivity.this);
                    }
                });
                snackbar.show();
            }
        }
        Log.d(LOG_TAG,stringBuilder.toString());
    }

    /** NavigationTOP画面に遷移する処理の共通化 */
    private void startNavigation() {
        Log.d(LOG_TAG, "startNavigation Called");

        Intent intent = new Intent(LicenceActivity.this,NavigationTopActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

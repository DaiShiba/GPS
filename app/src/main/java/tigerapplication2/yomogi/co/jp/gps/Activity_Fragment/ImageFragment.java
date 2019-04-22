package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import tigerapplication2.yomogi.co.jp.gps.R;


/**アプリ内DBに登録済みGeofenceがない場合のエラーページ*/
public class ImageFragment extends Fragment {
    private static final String LOG_TAG = ImageFragment.class.getSimpleName();
    private static final String ARG_FILE_PATH = "filePath";
    private String mFilePath;

    public ImageFragment() { }

    public static ImageFragment newInstance(String param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilePath = getArguments().getString(ARG_FILE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    //指定されたファイルパスの画像を表示する
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView imageView = getActivity().findViewById(R.id.fragmentTopImage);
        Log.d(LOG_TAG,"Bitmap Create Start");
        Log.d(LOG_TAG,"FilePath:" + mFilePath);
        try(InputStream is = getActivity().getAssets().open(mFilePath)){
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            Log.d(LOG_TAG,"Bitmap Created Success");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

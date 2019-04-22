package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import tigerapplication2.yomogi.co.jp.gps.R;


/**
OSSライセンス表示用のFragment
 */
public class OSSLicenceFragment extends Fragment {
    private final static String HTML_PATH = "file:///android_asset/license.html";

    public OSSLicenceFragment() {
    }

    public static OSSLicenceFragment newInstance() {
        return new OSSLicenceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_osslicence, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //WebViewにローカルで保持しているHtmlを設定
        WebView webView = getActivity().findViewById(R.id.licence_web_view);
        webView.loadUrl(HTML_PATH);
    }
}

package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tigerapplication2.yomogi.co.jp.gps.Adapter.LocationListViewAdapter;
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent;
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent.LocationItem;
import tigerapplication2.yomogi.co.jp.gps.Database.DatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.Adapter.MyLocationInfoRecyclerViewAdapter;
import tigerapplication2.yomogi.co.jp.gps.R;

/**
 * 位置情報一覧Fragment
 */
public class LocationInfoFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListener;
    private static final String LOG_TAG = LocationInfoFragment.class.getSimpleName();
    private static String data_count;
    private static int count;

    private static LocationListViewAdapter listViewAdapter;
    private static RecyclerView.Adapter recycleAdapter;

    public LocationInfoFragment() {
    }

    @SuppressWarnings("unused")
    public static LocationInfoFragment newInstance(int columnCount) {
        LocationInfoFragment fragment = new LocationInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLUMN_COUNT, String.valueOf(columnCount));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //リストの最大項目数を取得
        if (getArguments() != null) {
            data_count = getArguments().getString(ARG_COLUMN_COUNT);
        }
    }

    boolean typeMock = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        if (typeMock) {

            // listView の設定
            view = inflater.inflate(R.layout.fragment_locationinfo_list, container, false);
        }else {
            view = inflater.inflate(R.layout.fragment_locationinfo_list, container, false);
        }

        if (view instanceof RecyclerView) {
            //DBより最新履歴をmColumnCount件取得
            //履歴管理DBより更新日時が新しい順に取得
            DatabaseHelper mDatabaseHelper = DatabaseHelper.getInstance(getActivity());
            Cursor cursor = mDatabaseHelper.getHistory(data_count);
            Log.d(LOG_TAG,"DB取得件数：" + cursor.getCount());
            count = cursor.getCount();

            LocationContent locationContent = new LocationContent(cursor);

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            //RecycleViewに下線を追加
            RecyclerView.ItemDecoration itemDecoration =
                    new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            //アダプタにリストとリスナーを設定
            recycleAdapter = new MyLocationInfoRecyclerViewAdapter(locationContent.getItems(), mListener);
            recyclerView.setAdapter(recycleAdapter);
        }
        return view;
    }

    public static void oreoreNotify() {
        Log.e(LOG_TAG,"oreoreNotify Called");
        if (recycleAdapter != null) {
            recycleAdapter.notifyItemInserted(0);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(LocationItem item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //検知した位置情報がない場合、Snackbarでユーザへ通知
        if(count == 0) {
            RecyclerView layout = getActivity().findViewById(R.id.list);
            final Snackbar snackbar = Snackbar.make(layout, "取得した位置情報は現在0件です。", Snackbar.LENGTH_LONG);
            TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
        }
    }

}

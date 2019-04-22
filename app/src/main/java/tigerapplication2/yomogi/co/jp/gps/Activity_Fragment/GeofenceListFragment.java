package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tigerapplication2.yomogi.co.jp.gps.Contents.GeofenceContent;
import static tigerapplication2.yomogi.co.jp.gps.Contents.GeofenceContent.GeofenceItem;
import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantDatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.Adapter.MyGeofenceRecyclerViewAdapter;
import tigerapplication2.yomogi.co.jp.gps.R;

/**
 * 登録済みGeofence一覧Fragment
 */
public class GeofenceListFragment extends Fragment {
    private static final String LOG_TAG = GeofenceListFragment.class.getSimpleName();

    private static final String CATEGORY_NAME = "category-name";
    private String mCategoryName = "";
    private OnListFragmentInteractionListener mListener;

    public GeofenceListFragment() {
    }

    @SuppressWarnings("unused")
    public static GeofenceListFragment newInstance(String category) {
        Log.d(LOG_TAG,"newInstance Called");
        GeofenceListFragment fragment = new GeofenceListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_NAME, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"onCreate Called");

        if (getArguments() != null) {
            mCategoryName = getArguments().getString(CATEGORY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView Called");
        View view = inflater.inflate(R.layout.fragment_geofence_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.d(LOG_TAG,"view instanceof RecyclerView：true");
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            RestaurantDatabaseHelper restaurantDatabaseHelper =
                    RestaurantDatabaseHelper.getInstance(getActivity());
            Cursor cursor = restaurantDatabaseHelper.getRestaurantInfo(mCategoryName);
            Log.d(LOG_TAG,"cursor.getCount():" + cursor.getCount());

            GeofenceContent geofenceContent = new GeofenceContent(cursor);

            //RecycleViewに下線を追加
            RecyclerView.ItemDecoration itemDecoration =
                    new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            //Geofence登録一覧を表示する
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyGeofenceRecyclerViewAdapter(geofenceContent.geofenceItemArrayList, mListener));
        }
        return view;
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
        void onListFragmentInteraction(GeofenceItem item);
    }
}

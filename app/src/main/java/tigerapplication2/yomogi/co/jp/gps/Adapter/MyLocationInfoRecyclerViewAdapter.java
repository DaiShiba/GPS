package tigerapplication2.yomogi.co.jp.gps.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tigerapplication2.yomogi.co.jp.gps.Activity_Fragment.LocationInfoFragment.OnListFragmentInteractionListener;
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent.LocationItem;
import tigerapplication2.yomogi.co.jp.gps.R;

import static tigerapplication2.yomogi.co.jp.gps.Database.DatabaseDefineEnum.*;

import java.util.List;

/**
 * 位置情報一覧用のアダプター
 */
public class MyLocationInfoRecyclerViewAdapter extends RecyclerView.Adapter<MyLocationInfoRecyclerViewAdapter.ViewHolder> {

    private final List<LocationItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private static final String LOG_TAG = "RecyclerViewAdapter";

    //ListとListenerを引数に初期化
    public MyLocationInfoRecyclerViewAdapter(List<LocationItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG,"onCreateViewHolder Called");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_locationinfo, parent, false);
        return new ViewHolder(view);
    }

    //各Viewにテキストを設定
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(LOG_TAG,"onBindViewHolder Called");
        Log.d(LOG_TAG,"mValues.get(position)" + mValues.get(position));
        Log.d(LOG_TAG,"mValues.get(position).id" + mValues.get(position).id);
        Log.d(LOG_TAG,"mValues.get(position).time" + mValues.get(position).time);
        Log.d(LOG_TAG,"mValues.get(position).latitude" + mValues.get(position).latitude);
        Log.d(LOG_TAG,"mValues.get(position).longitude" + mValues.get(position).longitude);
        Log.d(LOG_TAG,"mValues.get(position).altitude" + mValues.get(position).altitude);

        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mTimeView.setText(mValues.get(position).time);
        holder.mLatitudeView.setText(LATITUDE.getLabel() + mValues.get(position).latitude);
        holder.mLongitude.setText(LONGITUDE.getLabel() + mValues.get(position).longitude);
        holder.mAltitude.setText(ALTITUDE.getLabel() + mValues.get(position).altitude);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mTimeView;
        public final TextView mLatitudeView;
        public final TextView mLongitude;
        public final TextView mAltitude;
        public LocationItem mItem;

        public ViewHolder(View view) {
            super(view);
            Log.d(LOG_TAG,"ViewHolder Called");
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mTimeView = view.findViewById(R.id.time);
            mLatitudeView = view.findViewById(R.id.latitude);
            mLongitude = view.findViewById(R.id.longitude);
            mAltitude = view.findViewById(R.id.altitude);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTimeView.getText() + "'";
        }
    }
}

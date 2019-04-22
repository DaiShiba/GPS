package tigerapplication2.yomogi.co.jp.gps.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tigerapplication2.yomogi.co.jp.gps.Contents.GeofenceContent.GeofenceItem;
import tigerapplication2.yomogi.co.jp.gps.Activity_Fragment.GeofenceListFragment.OnListFragmentInteractionListener;
import tigerapplication2.yomogi.co.jp.gps.R;

import java.util.List;

/**
 * Geofence一覧作成用アダプタ
 */
public class MyGeofenceRecyclerViewAdapter extends RecyclerView.Adapter<MyGeofenceRecyclerViewAdapter.ViewHolder> {

    private final List<GeofenceItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyGeofenceRecyclerViewAdapter(List<GeofenceItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_geofence, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("ID：" + mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).name);
        holder.mDescription.setText("カテゴリ：" + mValues.get(position).category);
        holder.mAddress.setText("住所：" + mValues.get(position).address);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
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
        public final TextView mContentView;
        public final TextView mDescription;
        public final TextView mAddress;
        public GeofenceItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            mDescription = view.findViewById(R.id.category);
            mAddress = view.findViewById(R.id.address);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

package tigerapplication2.yomogi.co.jp.gps.Adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import tigerapplication2.yomogi.co.jp.gps.Activity_Fragment.LocationInfoListFragment.OnListFragmentInteractionListener
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent.LocationItem
import tigerapplication2.yomogi.co.jp.gps.R

import tigerapplication2.yomogi.co.jp.gps.Database.DatabaseDefineEnum.*

/**
 * 位置情報一覧用のアダプター
 */
class LocationAdapter//ListとListenerを引数に初期化
 (private val mValues: List<LocationItem>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(LOG_TAG, "onCreateViewHolder Called")
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_locationinfo, parent, false)
        return ViewHolder(view)
    }

    //各Viewにテキストを設定
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].id
        holder.mTimeView.text = mValues[position].time
        holder.mLatitudeView.text = LATITUDE.getLabel() + mValues[position].latitude
        holder.mLongitude.text = LONGITUDE.getLabel() + mValues[position].longitude
        holder.mAltitude.text = ALTITUDE.getLabel() + mValues[position].altitude

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mTimeView: TextView
        val mLatitudeView: TextView
        val mLongitude: TextView
        val mAltitude: TextView
        var mItem: LocationItem? = null

        init {
            Log.d(LOG_TAG, "ViewHolder Called")
            mIdView = mView.findViewById(R.id.item_number)
            mTimeView = mView.findViewById(R.id.time)
            mLatitudeView = mView.findViewById(R.id.latitude)
            mLongitude = mView.findViewById(R.id.longitude)
            mAltitude = mView.findViewById(R.id.altitude)
        }

        override fun toString(): String {
            return super.toString() + " '" + mTimeView.text + "'"
        }
    }

    companion object {
        private val LOG_TAG = "RecyclerViewAdapter"
    }
}

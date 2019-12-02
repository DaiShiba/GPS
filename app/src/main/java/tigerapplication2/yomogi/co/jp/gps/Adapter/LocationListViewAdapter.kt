package tigerapplication2.yomogi.co.jp.gps.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import tigerapplication2.yomogi.co.jp.gps.Activity_Fragment.LocationInfoListFragment.OnListFragmentInteractionListener
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent.LocationItem
import android.widget.TextView
import tigerapplication2.yomogi.co.jp.gps.R

/**
 * 位置情報一覧用のアダプター
 */
class LocationListViewAdapter(private val context: Context, private val mValues: MutableList<out LocationItem>) : BaseAdapter() {

    companion object { private val LOG_TAG = LocationListViewAdapter::class.java.name }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View? {
        var convert = convertView
        if (convert == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convert = inflater.inflate(R.layout.mock_listview_layout, null)
        }

        if (convert != null) {
            (convert.findViewById<View>(R.id.time) as TextView).text = getItem(position).toString()
        }
        return convert
    }

    override fun getItem(position: Int): Any {
        return mValues[position]
    }

    override fun getItemId(position: Int): Long {
        return mValues[position].id.toLong()
    }

    override fun getCount(): Int {
        return mValues.size
    }

}

package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tigerapplication2.yomogi.co.jp.gps.Adapter.LocationAdapter

import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent.LocationItem
import tigerapplication2.yomogi.co.jp.gps.Database.DatabaseHelper
import tigerapplication2.yomogi.co.jp.gps.R

/**
 * 位置情報一覧Fragment
 */
class LocationInfoListFragment : Fragment() {
    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //リストの最大項目数を取得
        if (arguments != null) {
            data_count = arguments!!.getString(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_locationinfo_list, container, false)

        if (view is RecyclerView) {
            //DBより最新履歴をmColumnCount件取得
            //履歴管理DBより更新日時が新しい順に取得
            val mDatabaseHelper = DatabaseHelper.getInstance(activity)
            val cursor = mDatabaseHelper.getHistory(data_count)
            Log.d(LOG_TAG, "DB取得件数：" + cursor.count)
            count = cursor.count

            val locationContent = LocationContent(cursor)

            view.layoutManager = LinearLayoutManager(view.getContext())

            //RecycleViewに下線を追加
            val itemDecoration = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
            view.addItemDecoration(itemDecoration)

            //アダプタにリストとリスナーを設定
            view.adapter = LocationAdapter(locationContent.items, this.mListener)
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: LocationItem)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //検知した位置情報がない場合、Snackbarでユーザへ通知
        if (count == 0) {
            val layout = activity!!.findViewById<RecyclerView>(R.id.list)
            val snackbar = Snackbar.make(layout, "取得した位置情報は現在0件です。", Snackbar.LENGTH_LONG)
            val textView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            textView.setTextColor(Color.WHITE)
            snackbar.view.setBackgroundColor(Color.BLACK)
            snackbar.setActionTextColor(Color.WHITE)
            snackbar.show()
        }
    }

    companion object {
        private val ARG_COLUMN_COUNT = "column-count"
        private val LOG_TAG = LocationInfoFragment::class.java.simpleName
        private var data_count: String? = null
        private var count: Int = 0

        fun newInstance(columnCount: Int): LocationInfoListFragment {
            val fragment = LocationInfoListFragment()
            val args = Bundle()
            args.putString(ARG_COLUMN_COUNT, columnCount.toString())
            fragment.arguments = args
            return fragment
        }
    }

}

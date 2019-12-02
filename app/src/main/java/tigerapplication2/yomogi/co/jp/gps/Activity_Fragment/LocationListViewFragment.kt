package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_location_list_view.*
import kotlinx.android.synthetic.main.mock_listview_layout.*
import tigerapplication2.yomogi.co.jp.gps.Adapter.LocationListViewAdapter
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent

import tigerapplication2.yomogi.co.jp.gps.R
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LocationListViewFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LocationListViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LocationListViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ListViewに表示するデータ
        val items = ArrayList<LocationContent.LocationItem>()
        items.add(LocationContent.LocationItem("testId","time","latidu","lonmgi","alt"))

        // ListViewをセット
        val adapter = LocationListViewAdapter(this.context!!, items)
        list_view.adapter = adapter

        val header = layoutInflater.inflate(R.layout.headerlayout, null)
        list_view.addHeaderView(header)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_list_view, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationListViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LocationListViewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

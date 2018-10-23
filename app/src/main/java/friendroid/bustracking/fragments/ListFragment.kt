package friendroid.bustracking.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.adapters.AnyAdapter


open class ListFragment : Fragment() {
    var mAdapter: AnyAdapter? = null
    var title: String? = null
    private val observer = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            checkItemCount()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            checkItemCount()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            checkItemCount()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_holder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.itemList).apply {
            layoutManager = LinearLayoutManager(activity)
            setAdapter()
        }
    }


    override fun onResume() {
        super.onResume()
        setTitle()
        /*try {
            mAdapter?.registerAdapterDataObserver(observer)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }*/
        mAdapter?.registerAdapterDataObserver(observer)
        mAdapter?.startListening()
        checkItemCount()
/*
        Log.d("EEEE", "PT: ${pendingTeacherAdapter.itemCount}\n" +
                "PB: ${pendingBusesAdapter.itemCount}\n" +
                "OB: ${onlineBusAdapter.itemCount}")*/
    }

    override fun onPause() {
        super.onPause()
//        mAdapter?.stopListening()
        mAdapter?.unregisterAdapterDataObserver(observer)
    }

    open fun setAdapter(adapter: AnyAdapter? = null) {
        if (adapter != null) mAdapter = adapter
        view?.findViewById<RecyclerView>(R.id.itemList)?.adapter = mAdapter
        mAdapter?.startListening()
        checkItemCount()
    }

    fun checkItemCount() {
        if (mAdapter?.itemCount == 0) {
            view?.findViewById<RecyclerView>(R.id.itemList)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.itemList)?.visibility = View.VISIBLE
        }
        Log.d("EEEE", "${mAdapter?.javaClass}: ${mAdapter?.itemCount}")
    }

    protected open fun setTitle() {
        activity?.title = title
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("LIST_STATE_KEY", view?.findViewById<RecyclerView>(R.id.itemList)?.layoutManager?.onSaveInstanceState())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val percelable = savedInstanceState.getParcelable<Parcelable>("LIST_STATE_KEY")
            if (percelable != null)
            view?.findViewById<RecyclerView>(R.id.itemList)?.layoutManager?.onRestoreInstanceState(percelable)
        }

    }*/

}

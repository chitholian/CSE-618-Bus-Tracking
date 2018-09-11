package friendroid.bustracking.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import friendroid.bustracking.R
import friendroid.bustracking.adapters.AnyAdapter


open class ListFragment : Fragment() {
    var mAdapter: AnyAdapter? = null
    var title: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_holder, container, false)
        view.findViewById<RecyclerView>(R.id.itemList).apply {
            layoutManager = LinearLayoutManager(activity)
            setAdapter()
        }
        view.findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener { doRefresh() }
        return view
    }

    fun doRefresh() {
        Handler().postDelayed({
            if (activity != null)
                Toast.makeText(activity, "Refreshed", Toast.LENGTH_SHORT).show()
            view?.findViewById<SwipeRefreshLayout>(R.id.refresh)?.isRefreshing = false
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    fun setAdapter() {
        view?.findViewById<RecyclerView>(R.id.itemList)?.adapter = mAdapter
        mAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkItemCount()
            }
        })
        checkItemCount()
    }

    private fun checkItemCount() {
        if (mAdapter?.itemCount == 0) {
            view?.findViewById<RecyclerView>(R.id.itemList)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.itemList)?.visibility = View.VISIBLE
        }
    }

    protected open fun setTitle() {
        activity?.title = title
    }
}
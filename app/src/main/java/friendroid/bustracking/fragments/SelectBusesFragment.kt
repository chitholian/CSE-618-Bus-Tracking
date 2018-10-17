package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import friendroid.bustracking.R
import friendroid.bustracking.activities.*
import friendroid.bustracking.adapters.SelectableBusAdapter
import friendroid.bustracking.models.Bus
import friendroid.bustracking.fireSettings
import friendroid.bustracking.mUser
import kotlinx.android.synthetic.main.fragment_select_buses.*
import java.util.*
import kotlin.collections.ArrayList

class SelectBusesFragment : Fragment() {
    private lateinit var mAdapter: SelectableBusAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_buses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // show progressbar
        progressBar?.visibility = View.VISIBLE
        next_button?.isEnabled = false

        // get list of all approved buses
        val buses = ArrayList<Bus>()
        FirebaseFirestore.getInstance().collection("users").orderBy("name").whereEqualTo("role", "driver")
                .whereEqualTo("approved", true).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (document in it.result) {
                            buses.add(document.toObject(Bus::class.java))
                        }
                        // Set the adapter to show bus list
                        mAdapter = SelectableBusAdapter(buses)

                        view.findViewById<RecyclerView>(R.id.checkboxes)?.apply {
                            adapter = mAdapter
                            layoutManager = LinearLayoutManager(context)
                        }
                    } else {
                        it.exception?.printStackTrace()
                    }
                    progressBar?.visibility = View.INVISIBLE
                    next_button?.isEnabled = true

                    // check if no-item available
                    checkItemCount()
                }

        view.findViewById<Button>(R.id.next_button)?.setOnClickListener {
            it.isEnabled = false
            progressBar.visibility = View.VISIBLE

            // find selected items;
            val items = TreeSet<String>() // to keep uid only
            val recyclerView = view.findViewById<RecyclerView>(R.id.checkboxes)
            for (i in 0 until recyclerView.childCount) {
                val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
                if ((holder as SelectableBusAdapter.ViewHolder).v.findViewById<CheckBox>(R.id.checkbox).isChecked) {
                    items.add(buses[i].uid)
                }
            }

            // Now add the items to pending list of the teacher
            FirebaseFirestore.getInstance().also { instance -> instance.firestoreSettings = fireSettings }.document("pending/${mUser.uid}").set(
                    // store name, uid, identity and list of buses
                    mapOf("name" to mUser.name, "uid" to mUser.uid, "identity" to mUser.identity, "buses" to items.toList())
            ).addOnSuccessListener { _ ->
                // success, trigger onComplete
                onComplete()
            }.addOnFailureListener { err ->
                // failed!!
                err.printStackTrace()
                it.isEnabled = true
                progressBar.visibility = View.INVISIBLE
//                activity?.finish()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.subscribed_buses)
    }

    private fun checkItemCount() {
        if (mAdapter.itemCount == 0) {
            view?.findViewById<RecyclerView>(R.id.checkboxes)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.checkboxes)?.visibility = View.VISIBLE
        }
    }

    private fun onComplete() {
        if (!isDetached) {
            progressBar?.visibility = View.INVISIBLE
            next_button.isEnabled = true
            if (activity is TeacherActivity) {
                (activity as TeacherActivity).showOnlineBuses()
            } else startActivity(Intent(activity, TeacherActivity::class.java)).also {
                activity?.finish()
            }
        }
    }
}

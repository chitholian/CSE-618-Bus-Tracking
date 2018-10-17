package friendroid.bustracking.activities

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import com.google.firebase.firestore.FirebaseFirestore
import friendroid.bustracking.*
import friendroid.bustracking.adapters.SelectableBusAdapter
import friendroid.bustracking.models.Bus
import kotlinx.android.synthetic.main.activity_teacher_details.*
import java.util.*
import kotlin.collections.ArrayList

class TeacherDetailsActivity : BaseActivity() {
    private lateinit var mAdapter: SelectableBusAdapter
    private lateinit var teacherId: String
    private lateinit var collection: String
    private lateinit var buses: ArrayList<Bus>

    // to ensure that info of all pending buses has been retrieved
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_details)
        teacherId = intent.getStringExtra(EXTRA_TEACHER_ID) ?: "NA"
        collection = if (intent.action == ACTION_HANDLE_PENDING_REQUEST)
            "pending"
        else "users"
        buses = ArrayList<Bus>()
        // first, get pending teacher info
        progressBar.visibility = View.VISIBLE
        saveFab.isEnabled = false

        if (collection == "pending")
            FirebaseFirestore.getInstance().document("pending/$teacherId").get().addOnCompleteListener {
                if (it.isSuccessful && it.result.exists()) {
                    val busIds = it.result["buses"] as ArrayList<*>
                    makeTheList(busIds)
                    name?.text = it.result["name"]?.toString()
                    email?.text = it.result["identity"]?.toString()
                }
            }
        else {
            FirebaseFirestore.getInstance().document("users/$teacherId").get().addOnCompleteListener {
                if (it.isSuccessful && it.result.exists()) {
                    name?.text = it.result["name"]?.toString()
                    email?.text = it.result["identity"]?.toString()
                }
            }
            makeTheList()
        }

        saveFab.setOnClickListener {
            confirm(this, R.string.confirm_save) {
                // Save the teacher
                it.isEnabled = false
                progressBar.visibility = View.VISIBLE

                // find selected items;false
                val items = TreeSet<String>() // to keep uid only
                val recyclerView = findViewById<RecyclerView>(R.id.checkboxes)
                for (i in 0 until recyclerView.childCount) {
                    val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
                    if ((holder as SelectableBusAdapter.ViewHolder).v.findViewById<CheckBox>(R.id.checkbox).isChecked) {
                        items.add(buses[i].uid)
                    }
                }
                approveTeacher(items.toList(), teacherId)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_confirm_teacher, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (intent.action == ACTION_HANDLE_PENDING_REQUEST)
            menu?.findItem(R.id.menu_reject)?.isVisible = true
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_delete -> confirm(this, R.string.confirm_delete_id) {
                progressBar?.visibility = View.VISIBLE
                saveFab?.isEnabled = false
                val batch = FirebaseFirestore.getInstance().batch()
                batch.delete(FirebaseFirestore.getInstance().document("pending/$teacherId"))
                batch.delete(FirebaseFirestore.getInstance().document("users/$teacherId"))
                batch.commit().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast(R.string.operation_successful)
                        finish()
                    } else {
                        task.exception?.printStackTrace()
                        toast(R.string.op_failed)
                    }
                    saveFab?.isEnabled = true
                    progressBar?.visibility = View.INVISIBLE
                }
            }
            R.id.menu_reject -> confirm(this, R.string.confirm_reject) {
                progressBar?.visibility = View.VISIBLE
                saveFab?.isEnabled = false
                FirebaseFirestore.getInstance().document("pending/$teacherId").delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                toast(R.string.operation_successful)
                                finish()
                            } else {
                                it.exception?.printStackTrace()
                                toast(R.string.op_failed)
                            }
                            saveFab?.isEnabled = true
                            progressBar?.visibility = View.INVISIBLE
                        }
            }
        }
        return true
    }

    /*private fun checkItemCount() {
        if (mAdapter.itemCount == 0) {
            checkboxes.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            checkboxes.visibility = View.VISIBLE
        }
    }*/
    private fun approveTeacher(busIds: List<String>, teacherId: String) {
        // Start approval operation
        val batch = FirebaseFirestore.getInstance().batch()
        // set user as approved
        batch.update(FirebaseFirestore.getInstance().collection("users")
                .document(teacherId), "approved", true)
        batch.delete(FirebaseFirestore.getInstance().document("pending/$teacherId"))
        /*// Get all buses
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("role", "driver")
                .whereEqualTo("approved", true).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        for (document in task.result.documents) {

                        }
                }*/
        for (bus in buses) {
            bus.subscribers.remove(teacherId)
            if (busIds.contains(bus.uid))
                bus.subscribers.add(teacherId)
            // Update the bus
            FirebaseFirestore.getInstance().document("users/${bus.uid}")
                    .update(mapOf("subscribers" to bus.subscribers)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveFab?.isEnabled = true
                            progressBar?.visibility = View.INVISIBLE
                            toast(R.string.operation_successful)
                        }
                    }
        }

        // Commit batch operation.
        batch.commit().addOnCompleteListener { task ->
            if (!task.isSuccessful)
                task.exception?.printStackTrace()
        }
    }

    private fun makeTheList(busIds: ArrayList<*>? = null) {
        // Get bus list
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("role", "driver")
                .whereEqualTo("approved", true).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (d in task.result.documents) {
                            val bus = d.toObject(Bus::class.java)
                            if (bus != null) {
                                // if pending
                                if (busIds != null) {
                                    bus.subscribers.remove(teacherId)
                                    if (busIds.contains(bus.uid))
                                        bus.subscribers.add(teacherId)
                                }
                                buses.add(bus)
                                System.out.println("Here are ${bus.subscribers}")
                            }
                        }
                        mAdapter = SelectableBusAdapter(buses, teacherId)
                        progressBar?.visibility = View.VISIBLE
                        checkboxes.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = mAdapter
                            progressBar?.visibility = View.INVISIBLE
                            saveFab.isEnabled = true
                        }
                    }
                }
    }
}

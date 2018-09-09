package friendroid.bustracking.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.adapters.SelectableBusAdapter
import friendroid.bustracking.entities.Bus
import friendroid.bustracking.utils.confirm
import kotlinx.android.synthetic.main.activity_teacher_details.*

class TeacherDetailsActivity : BaseActivity() {
    private lateinit var mAdapter: SelectableBusAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_details)
        val buses = ArrayList<Bus>()
        for (i in 1..8) {
            val bus = Bus()
            bus.uid = "uid_$i"
            bus.name = "Bus Name $i"
            bus.identity = "email_$i@gmail.com"
            buses.add(bus)
        }
        mAdapter = SelectableBusAdapter(buses)
        progressBar?.visibility = View.VISIBLE
        delayed {
            findViewById<RecyclerView>(R.id.checkboxes)?.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(context)
                progressBar?.visibility = View.INVISIBLE
            }
        }
        saveFab.setOnClickListener {
            confirm(this, R.string.confirm_save) {
                it.isEnabled = false
                progressBar?.visibility = View.VISIBLE
                delayed {
                    toast(R.string.operation_successful)
                    progressBar?.visibility = View.INVISIBLE
                    it.isEnabled = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_confirm_teacher, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_delete -> confirm(this, R.string.confirm_delete) {
                progressBar?.visibility = View.VISIBLE
                delayed {
                    finish()
                }
            }
        }
        return true
    }
}

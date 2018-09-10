package friendroid.bustracking.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import friendroid.bustracking.*
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.activities.TeacherDetailsActivity
import friendroid.bustracking.adapters.TeacherListAdapter
import friendroid.bustracking.entities.Teacher
import kotlinx.android.synthetic.main.fragment_list_holder.*

class TeacherListFragment : ListFragment() {
    private val REQ_CODE = 111
    private lateinit var teachers: ArrayList<Teacher>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.all_teachers)
        teachers = ArrayList()
        for (i in 1..50)
            Teacher().also {
                it.uid = "uid_$i"
                it.identity = "email_$i@gmail.com"
                it.name = "Teacher Name $i"
                teachers.add(it)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra(EXTRA_RESULT)
            if (result == EXTRA_DELETED) { // Account deleted!!
                val position = data.getIntExtra(EXTRA_POSITION, -1)
                if (position >= 0) {
                    teachers.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        (activity as BaseActivity).delayed {
            mAdapter = TeacherListAdapter(teachers) { position ->
                val intent = Intent(activity, TeacherDetailsActivity::class.java)
                intent.putExtra(EXTRA_TEACHER_ID, teachers[position as Int].uid)
                intent.putExtra(EXTRA_POSITION, position)
                startActivityForResult(intent, REQ_CODE)
            }
            setAdapter() // We have to call because it is changed delayed.
            progressBar?.visibility = View.INVISIBLE
        }
    }
}
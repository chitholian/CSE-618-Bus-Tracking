package friendroid.bustracking.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import friendroid.bustracking.ACTION_HANDLE_PENDING_REQUEST
import friendroid.bustracking.EXTRA_POSITION
import friendroid.bustracking.EXTRA_TEACHER_ID
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.activities.TeacherDetailsActivity
import friendroid.bustracking.adapters.PendingTeacherAdapter
import friendroid.bustracking.entities.Teacher
import kotlinx.android.synthetic.main.fragment_list_holder.*


open class PendingTeacherFragment : ListFragment() {
    private val REQ_CODE = 111
    private lateinit var teachers: ArrayList<Teacher>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.pending_teachers)
        teachers = ArrayList()
        for (i in 1..18)
            Teacher().also {
                it.uid = "uid_$i"
                it.identity = "email_$i@gmail.com"
                it.name = "Teacher Name $i"
                teachers.add(it)
            }

        mAdapter = PendingTeacherAdapter(teachers, { position ->
            progressBar.visibility = View.VISIBLE
            (activity as BaseActivity).delayed {
                teachers.removeAt(position)
                mAdapter?.notifyDataSetChanged()
                progressBar.visibility = View.INVISIBLE
            }
        }, { position ->
            val intent = Intent(activity, TeacherDetailsActivity::class.java)
            intent.action = ACTION_HANDLE_PENDING_REQUEST
            intent.putExtra(EXTRA_TEACHER_ID, teachers[position as Int].uid)
            intent.putExtra(EXTRA_POSITION, position)
            startActivityForResult(intent, REQ_CODE)
        })
    }

    override fun setTitle() {
        // Do not set title.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && resultCode == Activity.RESULT_OK) {
            val position = data?.getIntExtra(EXTRA_POSITION, 0)
            if (position != null && position > 0) {
                teachers.removeAt(position)
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }
}

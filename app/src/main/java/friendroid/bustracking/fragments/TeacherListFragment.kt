package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import friendroid.bustracking.R
import friendroid.bustracking.activities.TeacherDetailsActivity
import friendroid.bustracking.adapters.TeacherListAdapter
import friendroid.bustracking.entities.Teacher

class TeacherListFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.all_teachers)

        val teachers = ArrayList<Teacher>()
        for (i in 1L..15L)
            Teacher().also {
                it.uid = "uid_$i"
                it.identity = "email_$i@gmail.com"
                it.name = "Teacher Name $i"
                teachers.add(it)
            }
        mAdapter = TeacherListAdapter(teachers) {
            startActivity(Intent(activity, TeacherDetailsActivity::class.java))
        }
    }
}
package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import friendroid.bustracking.R
import friendroid.bustracking.activities.TeacherDetailsActivity
import friendroid.bustracking.adapters.PendingTeacherAdapter
import friendroid.bustracking.entities.Teacher


open class PendingTeacherFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.pending_teachers)

        val teachers = ArrayList<Teacher>()
        for (i in 1..18)
            Teacher().also {
                it.uid = "uid_$i"
                it.identity = "email_$i@gmail.com"
                it.name = "Teacher Name $i"
                teachers.add(it)
            }

        mAdapter = PendingTeacherAdapter(teachers) { _ ->
            startActivity(Intent(activity, TeacherDetailsActivity::class.java))
        }
    }
}
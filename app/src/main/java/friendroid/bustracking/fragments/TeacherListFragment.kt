package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import friendroid.bustracking.*
import friendroid.bustracking.activities.TeacherDetailsActivity
import friendroid.bustracking.adapters.TeacherListAdapter

class TeacherListFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.all_teachers)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter(TeacherListAdapter(FirestoreRecyclerOptions.Builder<Any>().setQuery(FirebaseFirestore.getInstance()
                .collection("users").whereEqualTo("role", "teacher")
                .whereEqualTo("approved", true), Any::class.java).build()) {
            val intent = Intent(activity, TeacherDetailsActivity::class.java)
            intent.putExtra(EXTRA_TEACHER_ID, it["uid"]?.toString())
            startActivity(intent)
        })
    }
}
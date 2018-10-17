package friendroid.bustracking.fragments

import android.os.Bundle
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import friendroid.bustracking.R
import friendroid.bustracking.adapters.SimpleBusAdapter
import friendroid.bustracking.confirm


open class AllBusesFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.all_buses)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter(SimpleBusAdapter(FirestoreRecyclerOptions.Builder<Any>().setQuery(FirebaseFirestore.getInstance()
                .collection("users").whereEqualTo("role", "driver")
                .whereEqualTo("approved", true), Any::class.java).build()) { bus ->
            confirm(activity, R.string.confirm_delete) {
                // delete this bus profile
                FirebaseFirestore.getInstance().document("users/${bus["uid"]?.toString()
                        ?: "NA"}").delete()/*.addOnSuccessListener {
                    // success
                }.addOnFailureListener {
                    // failed !!
                }*/
                FirebaseDatabase.getInstance().getReference("status/${bus["uid"]?.toString()
                        ?: "NA"}").removeValue()
            }
        })
    }
}

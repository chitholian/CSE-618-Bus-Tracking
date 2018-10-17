package friendroid.bustracking.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import friendroid.bustracking.R

open class AnyAdapter(options: FirestoreRecyclerOptions<Any>, private val listener: (item: Map<*, *>) -> Unit) : FirestoreRecyclerAdapter<Any, AnyAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sample_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Any) {
        holder.view.setOnClickListener { listener.invoke(getItem(position) as Map<*, *>) }
    }
/*
    override fun startListening() {
        super.startListening()
        Log.d("EEEE" ,"Listening....")
    }*/

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

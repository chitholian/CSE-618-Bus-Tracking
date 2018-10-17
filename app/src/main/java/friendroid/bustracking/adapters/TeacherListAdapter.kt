package friendroid.bustracking.adapters

import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import friendroid.bustracking.R

class TeacherListAdapter(options: FirestoreRecyclerOptions<Any>, private val listener: (item: Map<*, *>) -> Unit) : AnyAdapter(options, listener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val teacher = getItem(position) as Map<*, *>
        holder.view.apply {
            findViewById<TextView>(R.id.textName).text = teacher["name"]?.toString()
            findViewById<TextView>(R.id.text2).text = teacher["identity"]?.toString()
            setOnClickListener { listener.invoke(teacher) }
        }
    }
}

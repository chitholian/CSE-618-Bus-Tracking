package friendroid.bustracking.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import friendroid.bustracking.R

open class SimpleBusAdapter(options: FirestoreRecyclerOptions<Any>, private val listener: (item: Map<*, *>) -> Unit) : AnyAdapter(options, listener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val bus = getItem(position) as Map<*, *>
        holder.view.apply {
            isClickable = false
            findViewById<TextView>(R.id.textName).text = bus["name"]?.toString()
            findViewById<TextView>(R.id.text2).text = bus["identity"]?.toString()
            findViewById<ImageButton>(R.id.deleteButton).apply {
                this.visibility = View.VISIBLE
                this.setOnClickListener { listener.invoke(bus) }
            }
        }
    }
}
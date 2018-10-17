package friendroid.bustracking.adapters

import android.view.View
import android.widget.ImageButton
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import friendroid.bustracking.R
import java.lang.Exception

class PendingBusAdapter(options: FirestoreRecyclerOptions<Any>, deleteListener: (item: Map<*, *>) -> Unit, private val confirmListener: (item: Map<*, *>) -> Unit) : SimpleBusAdapter(options, deleteListener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.apply {
            findViewById<ImageButton>(R.id.secondButton).apply {
                this.visibility = View.VISIBLE
                this.setOnClickListener {
                    try {
                        confirmListener.invoke(getItem(position) as Map<*, *>)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }
}
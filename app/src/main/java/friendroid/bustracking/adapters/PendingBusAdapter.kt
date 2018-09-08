package friendroid.bustracking.adapters

import android.view.View
import android.widget.ImageButton
import friendroid.bustracking.R

class PendingBusAdapter(private val items: List<Any>, deleteListener: (item: Any) -> Unit, private val confirmListener: (item: Any) -> Unit) : SimpleBusAdapter(items, deleteListener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.apply {
            findViewById<ImageButton>(R.id.secondButton).apply {
                this.visibility = View.VISIBLE
                this.setOnClickListener {
                    confirmListener.invoke(items[position])
                }
            }
        }
    }
}
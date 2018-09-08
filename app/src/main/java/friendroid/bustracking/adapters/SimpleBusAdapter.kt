package friendroid.bustracking.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.entities.Bus

open class SimpleBusAdapter(private val items: List<Any>, private val listener: (item: Any) -> Unit) : AnyAdapter(items, listener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val bus = items[position] as Bus
        holder.view.apply {
            isClickable = false
            findViewById<TextView>(R.id.textName).text = bus.name
            findViewById<TextView>(R.id.text2).text = bus.identity
            findViewById<ImageButton>(R.id.deleteButton).apply {
                this.visibility = View.VISIBLE
                this.setOnClickListener { listener.invoke(bus) }
            }
        }
    }
}
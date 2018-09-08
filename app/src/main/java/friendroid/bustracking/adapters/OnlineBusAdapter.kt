package friendroid.bustracking.adapters

import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.entities.OnlineBus

class OnlineBusAdapter(private val items: List<Any>, private val listener: (item: Any) -> Unit) : AnyAdapter(items, listener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val bus = items[position] as OnlineBus
        holder.view.apply {
            findViewById<TextView>(R.id.textName).text = bus.name
            findViewById<TextView>(R.id.text2).text = bus.location.place
            setOnClickListener { listener.invoke(bus) }
        }
    }
}
package friendroid.bustracking.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import friendroid.bustracking.R
import friendroid.bustracking.entities.Bus

class SelectableBusAdapter(private val items: List<Bus>) : RecyclerView.Adapter<SelectableBusAdapter.ViewHolder>() {
    private val selectedItems = HashSet<Bus>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.selectable_bus, parent, false)!!)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus = items[position]
        holder.v.findViewById<CheckBox>(R.id.checkbox).apply {
            text = bus.name
            this.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedItems.add(bus)
                else selectedItems.remove(bus)
            }
        }
    }

    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)
}
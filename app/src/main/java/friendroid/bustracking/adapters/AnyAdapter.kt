package friendroid.bustracking.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import friendroid.bustracking.R

open class AnyAdapter(private val items: List<Any>, private val listener: (item: Any) -> Unit) : RecyclerView.Adapter<AnyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_list_item, parent, false)
        return ViewHolder(view!!)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.setOnClickListener { listener.invoke(items[position]) }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
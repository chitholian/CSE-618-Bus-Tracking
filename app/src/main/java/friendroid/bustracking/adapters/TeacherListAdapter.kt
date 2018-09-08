package friendroid.bustracking.adapters

import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.entities.Teacher

class TeacherListAdapter(private val items: List<Any>, private val listener: (item: Any) -> Unit) : AnyAdapter(items, listener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val teacher = items[position] as Teacher
        holder.view.apply {
            findViewById<TextView>(R.id.textName).text = teacher.name
            findViewById<TextView>(R.id.text2).text = teacher.identity
            setOnClickListener { listener.invoke(teacher) }
        }
    }
}
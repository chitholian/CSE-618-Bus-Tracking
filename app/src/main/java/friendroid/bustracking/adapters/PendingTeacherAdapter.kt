package friendroid.bustracking.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.entities.Teacher

class PendingTeacherAdapter(private val items: List<Any>, private val actionListener: (position: Int) -> Unit, private val listener: (position: Any) -> Unit) : AnyAdapter(items, listener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val teacher = items[position] as Teacher
        holder.view.apply {
            findViewById<TextView>(R.id.textName).text = teacher.name
            findViewById<TextView>(R.id.text2).text = teacher.identity
            findViewById<ImageButton>(R.id.secondButton).apply {
                this.visibility = View.VISIBLE
                this.setImageResource(R.drawable.ic_account_plus)
                (this.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_END)
                this.setOnClickListener { actionListener.invoke(position) }
            }
            this.setOnClickListener { listener.invoke(position) }
        }
    }
}

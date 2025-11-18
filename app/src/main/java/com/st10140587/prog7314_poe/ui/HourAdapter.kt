package com.st10140587.prog7314_poe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st10140587.prog7314_poe.R

data class HourUi(
    val time: String,
    val temp: String,
    val iconRes: Int? = null   // <-- default, so callers can pass only (time, temp)
)

class HourAdapter : RecyclerView.Adapter<HourVH>() {
    private val items = mutableListOf<HourUi>()
    fun submit(list: List<HourUi>) { items.setAll(list) }

    private fun MutableList<HourUi>.setAll(list: List<HourUi>) {
        clear(); addAll(list); notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hour, parent, false)
        return HourVH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HourVH, position: Int) {
        holder.bind(items[position])
    }
}

class HourVH(private val root: View) : RecyclerView.ViewHolder(root) {
    private val tvTime = root.findViewById<TextView>(R.id.tvTime)
    private val tvTemp = root.findViewById<TextView>(R.id.tvTemp)
    private val ivIcon = root.findViewById<ImageView?>(R.id.ivIcon)

    fun bind(m: HourUi) {
        tvTime.text = m.time
        tvTemp.text = m.temp
        if (ivIcon != null) {
            if (m.iconRes != null) {
                ivIcon.visibility = View.VISIBLE
                ivIcon.setImageResource(m.iconRes)
            } else {
                ivIcon.visibility = View.GONE
            }
        }
    }
}

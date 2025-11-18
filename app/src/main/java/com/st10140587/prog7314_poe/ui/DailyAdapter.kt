package com.st10140587.prog7314_poe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st10140587.prog7314_poe.R

data class DayUi(val label: String, val hi: String, val lo: String)

class DailyAdapter : RecyclerView.Adapter<DayVH>() {
    private val items = mutableListOf<DayUi>()
    fun submit(list: List<DayUi>) { items.setAll(list) }

    private fun MutableList<DayUi>.setAll(list: List<DayUi>) {
        clear(); addAll(list); notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DayVH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DayVH, position: Int) {
        holder.bind(items[position])
    }
}

class DayVH(private val root: View) : RecyclerView.ViewHolder(root) {
    private val tvDay = root.findViewById<TextView>(R.id.tvDay)
    private val tvHiLo = root.findViewById<TextView>(R.id.tvHiLo)

    fun bind(m: DayUi) {
        tvDay.text = m.label
        tvHiLo.text = "${m.hi} / ${m.lo}"
    }
}
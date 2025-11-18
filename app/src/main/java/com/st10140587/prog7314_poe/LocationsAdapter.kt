package com.st10140587.prog7314_poe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.st10140587.prog7314_poe.data.local.LocationEntity

class LocationsAdapter(
    private val onClick: (LocationEntity) -> Unit,
    private val onLongClick: (LocationEntity) -> Unit
) : RecyclerView.Adapter<LocationsAdapter.VH>() {

    val current = mutableListOf<LocationEntity>()

    fun submit(items: List<LocationEntity>) {
        current.clear()
        current.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return VH(v, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(current[position])
    override fun getItemCount(): Int = current.size

    class VH(itemView: View,
             private val onClick: (LocationEntity) -> Unit,
             private val onLongClick: (LocationEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.tvName)
        private val coords = itemView.findViewById<TextView>(R.id.tvCoords)
        private val star = itemView.findViewById<ImageView>(R.id.ivStar)

        fun bind(item: LocationEntity) {
            name.text = item.name
            coords.text = "(${item.latitude}, ${item.longitude})"
            star.isVisible = item.isDefault

            itemView.setOnClickListener { onClick(item) }      // set default
            itemView.setOnLongClickListener { onLongClick(item); true } // delete
        }
    }
}

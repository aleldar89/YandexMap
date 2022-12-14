package ru.netology.yandexmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmap.databinding.PlaceItemBinding

class PlacesAdapter (
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Place, PlaceViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }
}

interface OnInteractionListener {
    fun onEdit(place: Place) {}
    fun onRemove(place: Place) {}
    fun onMap(place: Place) {}
}

class PlaceViewHolder(
    private val binding: PlaceItemBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(place: Place) {
        binding.apply {
            title.text = place.title

            remove.setOnClickListener {
                onInteractionListener.onRemove(place)
            }

            edit.setOnClickListener {
                onInteractionListener.onEdit(place)
            }

            title.setOnClickListener {
                onInteractionListener.onMap(place)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return (oldItem.lat == newItem.lat && oldItem.long == newItem.long)
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}
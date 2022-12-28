package ru.netology.yandexmap.ui

import StringArg
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import ru.netology.yandexmap.*
import ru.netology.yandexmap.databinding.PlaceListBinding

class PlacesFragment : Fragment() {

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PlaceListBinding.inflate(inflater, container, false)
        val gson = Gson()
        val adapter = PlacesAdapter(object : OnInteractionListener {
            override fun onEdit(place: Place) {
                val bundle = Bundle().apply {
                    textArg = gson.toJson(place)
                }
                findNavController().navigate(R.id.action_placesFragment_to_editPlaceFragment, bundle)
            }

            override fun onRemove(place: Place) {
                viewModel.remove(place.id)
            }

            override fun onMap(place: Place) {
                viewModel.moveToPoint(place)
                findNavController().navigate(R.id.action_placesFragment_to_mapFragment)
            }
        })

        binding.placeList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root

    }
}
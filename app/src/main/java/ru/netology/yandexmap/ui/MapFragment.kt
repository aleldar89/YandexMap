package ru.netology.yandexmap.ui

import StringArg
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.ui_view.ViewProvider
import ru.netology.yandexmap.MapViewModel
import ru.netology.yandexmap.Place
import ru.netology.yandexmap.R
import ru.netology.yandexmap.databinding.FragmentMapBinding
import ru.netology.yandexmap.databinding.PlaceBinding

class MapFragment : Fragment() {

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private var mapView: MapView? = null
    private lateinit var userLocationLayer: UserLocationLayer

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = true
                userLocationLayer.cameraPosition()?.target?.also {
                    val map = mapView?.map ?: return@registerForActivityResult
                    val cameraPosition = map.cameraPosition
                    map.move(
                        CameraPosition(
                            it,
                            cameraPosition.zoom,
                            cameraPosition.azimuth,
                            cameraPosition.tilt
                        )
                    )
                }
            } else {
                userLocationLayer.isVisible = false
            }
        }

    private val mapClickListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            NewPlaceFragment.newInstance(lat = point.latitude, long = point.longitude)
                .show(childFragmentManager, null)
        }
    }

    // обязательно должно быть свойством
    private val markerTapListener = MapObjectTapListener { marker, _ ->
        // получаем данные
        Toast.makeText(requireContext(), (marker.userData as Place).title, Toast.LENGTH_SHORT).show()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView.apply {
            userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = true
            }
            map.addInputListener(mapClickListener)

            val collection = map.mapObjects.addCollection()

            viewModel.data.observe(viewLifecycleOwner) { places ->
                places.forEach {
                    val placeBinding = PlaceBinding.inflate(layoutInflater)
                    placeBinding.root.text = it.title

                    val placemark = collection.addPlacemark(
                        Point(it.lat, it.long),
                        ViewProvider(placeBinding.root)
                    )
                    placemark.userData = it // пробрасываем данные
                    placemark.addTapListener(markerTapListener)
                }
            }

        }

        binding.myLocation.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.zoomUp.setOnClickListener {
            val cameraPosition = mapView?.map?.cameraPosition ?: return@setOnClickListener
            mapView?.map?.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom + 1,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        }

        binding.zoomDown.setOnClickListener {
            val cameraPosition = mapView?.map?.cameraPosition ?: return@setOnClickListener
            mapView?.map?.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 1,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        }

        binding.myList.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_placesFragment)
        }

        viewModel.savedPoint.observe(viewLifecycleOwner) {
            val cameraPosition = mapView?.map?.cameraPosition
            if (cameraPosition != null) {
                mapView?.map?.move(
                    CameraPosition(
                        it,
                        cameraPosition.zoom,
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    )
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView = null
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        mapView?.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop()
    }
}
package ru.netology.yandexmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class MapViewModel : ViewModel() {

    private var nextId = 1

    private val _data = MutableLiveData(emptyList<Place>())
    val data: LiveData<List<Place>>
        get() = _data

    private val _savedPoint = MutableLiveData(Point())
    val savedPoint: LiveData<Point>
        get() = _savedPoint

    fun add(place: Place) {
        _data.value = _data.value.orEmpty() + place.copy(id = nextId)
        nextId ++
    }

    fun remove(placeId: Int) {
        val places = data.value?.filter {it.id != placeId}
        _data.value = places
    }

    fun saveEdits(place: Place) {
        val places = data.value?.map {
            if (it.id != place.id)
                it
            else
                it.copy(title = place.title)
        }
        _data.value = places
    }

    fun moveToPoint(place: Place) {
        _savedPoint.value = Point(place.lat, place.long)
    }

}
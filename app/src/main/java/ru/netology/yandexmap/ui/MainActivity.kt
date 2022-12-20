package ru.netology.yandexmap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.yandex.mapkit.MapKitFactory
import ru.netology.yandexmap.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MapKitFactory.setApiKey("e35ce02e-18b3-4678-9859-286826ff3245")
        MapKitFactory.initialize(this)
    }
}
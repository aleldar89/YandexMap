package ru.netology.yandexmap.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.netology.yandexmap.MapViewModel
import ru.netology.yandexmap.Place
import ru.netology.yandexmap.R

class NewPlaceFragment : DialogFragment() {

    companion object {
        private const val LAT_KEY = "LAT_KEY"
        private const val LONG_KEY = "LONG_KEY"

        fun newInstance(lat: Double, long: Double): NewPlaceFragment = NewPlaceFragment().apply {
            arguments = bundleOf(LAT_KEY to lat, LONG_KEY to long)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputText = AppCompatEditText(requireContext())
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.enter_place_name))
            .setView(inputText)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val viewModel: MapViewModel by activityViewModels()

                val title = inputText.text?.toString()?.takeIf {
                    it.isNotBlank()
                } ?: run {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.empty_title_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                viewModel.add(
                    Place(
                        id = 0,
                        title = title,
                        lat = requireArguments().getDouble(LAT_KEY),
                        long = requireArguments().getDouble(LONG_KEY)
                    )
                )
            }
            .create()
    }
}
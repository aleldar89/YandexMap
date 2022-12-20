package ru.netology.yandexmap.ui

import StringArg
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import ru.netology.yandexmap.MapViewModel
import ru.netology.yandexmap.Place
import ru.netology.yandexmap.R

class EditPlaceFragment : DialogFragment() {

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputText = AppCompatEditText(requireContext())
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Edit place")
                .setView(inputText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val gson = Gson()

                    val place: Place = arguments?.textArg.let { string ->
                        gson.fromJson(string, Place::class.java)
                    } ?: throw NullPointerException("Edit place error")

                    val newTitle = inputText.text?.toString()?.takeIf {
                        it.isNotBlank()
                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.empty_title_error),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    val editedPlace = place.copy(title = newTitle)

                    viewModel.saveEdits(editedPlace)
                    findNavController().navigateUp()

                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    findNavController().navigateUp()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
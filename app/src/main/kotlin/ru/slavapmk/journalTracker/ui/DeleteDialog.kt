package ru.slavapmk.journalTracker.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journalTracker.R

class DeleteDialog(
    private val onAccept: ((View) -> Unit)
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.Dialog)
            val inflater = requireActivity().layoutInflater
            val inflate = inflater.inflate(R.layout.dialog_delete, null)

            inflate.findViewById<MaterialButton>(R.id.button_cancel)
                .setOnClickListener {
                    dialog?.cancel()
                }
            inflate.findViewById<MaterialButton>(R.id.button_ok)
                .setOnClickListener { acceptButton ->
                    dialog?.cancel()
                    onAccept(acceptButton)
                }

            builder.setView(inflate).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
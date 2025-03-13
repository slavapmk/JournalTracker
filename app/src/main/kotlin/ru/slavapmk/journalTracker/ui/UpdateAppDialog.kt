package ru.slavapmk.journalTracker.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.databinding.ActivityMainBinding
import ru.slavapmk.journalTracker.databinding.DialogUpdateAppBinding
import ru.slavapmk.journalTracker.viewModels.VersionInfo

class UpdateAppDialog(
    private val version: VersionInfo
) : DialogFragment() {
    private lateinit var binding: DialogUpdateAppBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.Dialog)

            binding = DialogUpdateAppBinding.inflate(requireActivity().layoutInflater)

            binding.buttonCancel.setOnClickListener {
                dialog?.cancel()
            }
            binding.buttonOk.setOnClickListener {
                dialog?.cancel()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(version.url))
                requireContext().startActivity(intent)
            }

            val string = getString(R.string.dialog_update_app_content, version.version)

            val versionText = version.version
            val spannable = SpannableString(string)

            val start = string.indexOf(versionText)
            val end = start + versionText.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.descriptionMessage.text = spannable

            builder.setView(binding.root).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
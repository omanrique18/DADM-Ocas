package co.edu.unal.tic_tac_toe.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import co.edu.unal.tic_tac_toe.R

class AboutDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.about_dialog, null))
                .setPositiveButton(getString(R.string.ok)){ _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
package co.edu.unal.tic_tac_toe.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import co.edu.unal.tic_tac_toe.R

class QuitDialog: DialogFragment() {
    private lateinit var listener: QuitDialogListener

    interface QuitDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as QuitDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement QuitDialogListener"))
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.quit_message))
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.no, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
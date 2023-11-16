package co.edu.unal.tic_tac_toe.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import co.edu.unal.tic_tac_toe.R
import co.edu.unal.tic_tac_toe.TicTacToeActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WaitDialog: DialogFragment() {
    private lateinit var awaitingGameDocRef: DocumentReference
    private lateinit var registration: ListenerRegistration
    private lateinit var listener: WaitDialogListener

    interface WaitDialogListener {
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as WaitDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement QuitDialogListener"))
        }
    }

    override fun onStart() {
        super.onStart()
        registration = awaitingGameDocRef.addSnapshotListener { value, error ->
            if(error != null) {
                awaitingGameDocRef.delete()
                this.dismiss()
            }
            if(value != null && value.exists() && value.data?.get("isPlayerPaired") == true){
                awaitingGameDocRef.update("isPlayerPaired", true)
                val intent = Intent(this.context, TicTacToeActivity::class.java)
                intent.putExtra("gameId", awaitingGameDocRef.id)
                intent.putExtra("isSingleMode", false)
                intent.putExtra("isHost", true)
                startActivity(intent)
                this.dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.wait_message))
                .setCancelable(false)
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    listener.onDialogNegativeClick(this)
                    dialogInterface.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        registration.remove()
    }

    fun getAwaitingGameDocRef(): DocumentReference{
        return this.awaitingGameDocRef
    }

    fun setAwaitingGameDocRef(docRef: DocumentReference){
        this.awaitingGameDocRef = docRef
    }
}
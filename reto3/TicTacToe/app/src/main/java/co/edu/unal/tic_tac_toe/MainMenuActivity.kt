package co.edu.unal.tic_tac_toe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.DialogFragment
import co.edu.unal.tic_tac_toe.dialogs.WaitDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainMenuActivity : AppCompatActivity(),
    WaitDialog.WaitDialogListener {
    private lateinit var singlePlayerButton: Button
    private lateinit var multiPlayerButton: Button
    private lateinit var waitDialog: WaitDialog
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        singlePlayerButton = findViewById(R.id.single_button)
        multiPlayerButton = findViewById(R.id.multi_button)

        singlePlayerButton.setOnClickListener {
            val intent = Intent(this,TicTacToeActivity::class.java)
            intent.putExtra("isSingleMode", true)
            startActivity(intent)
        }
        multiPlayerButton.setOnClickListener {
            val collection = db.collection("current_games")
            collection.get()
                .addOnSuccessListener { result ->
                    if(result.isEmpty){
                        waitDialog = WaitDialog()
                        collection.add(createUser()).addOnSuccessListener { docRef ->
                            waitDialog.setAwaitingGameDocRef(docRef)
                            waitDialog.show(supportFragmentManager, "wait")
                        }
                    }else{
                        val index = (0 until result.size()).random()
                        val document = result.documents[index]
                        document.reference.update("isPlayerPaired", true)
                        val intent = Intent(this,TicTacToeActivity::class.java)
                        intent.putExtra("gameId", document.reference.id)
                        intent.putExtra("isSingleMode", false)
                        intent.putExtra("isHost", false)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("********error",exception.toString())
                }
        }
    }

    private fun createUser(): HashMap<String,*>{
        val hostPlayerSymbol = setOf(TicTacToe.oSymbol,TicTacToe.xSymbol).random()
        val guestPlayerSymbol = TicTacToe.alternateSymbols(hostPlayerSymbol)
        return hashMapOf(
            "isPlayerPaired" to false,
            "hostPlayerSymbol" to hostPlayerSymbol,
            "guestPlayerSymbol" to guestPlayerSymbol,
            "0" to "-",
            "1" to "-",
            "2" to "-",
            "3" to "-",
            "4" to "-",
            "5" to "-",
            "6" to "-",
            "7" to "-",
            "8" to "-"
        )
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        db.collection("current_games").document(waitDialog.getAwaitingGameDocRef().id)
            .delete()
    }
}
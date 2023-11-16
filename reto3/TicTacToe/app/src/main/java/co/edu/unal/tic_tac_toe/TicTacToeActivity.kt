package co.edu.unal.tic_tac_toe

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import co.edu.unal.tic_tac_toe.dialogs.AboutDialog
import co.edu.unal.tic_tac_toe.dialogs.DifficultyDialog
import co.edu.unal.tic_tac_toe.dialogs.ResetDialog
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TicTacToeActivity : AppCompatActivity(),
    DifficultyDialog.DifficultyDialogListener,
    ResetDialog.ResetDialogListener {
    private lateinit var ticTacToe: TicTacToe
    private lateinit var gridView: GridView
    private lateinit var turnText: TextView
    private lateinit var oWinsText: TextView
    private lateinit var xWinsText: TextView
    private lateinit var tiesText: TextView
    private lateinit var gameStateText: TextView
    private lateinit var scoreBoard: Array<Int>
    private lateinit var symbols: Array<String>
    private lateinit var bundle: Bundle
    private lateinit var xSound: MediaPlayer
    private lateinit var oSound: MediaPlayer
    private lateinit var prefs: SharedPreferences
    private lateinit var gameId: String
    private lateinit var docRef: DocumentReference
    private lateinit var registration: ListenerRegistration
    private var isSingleMode = true
    private var isHost = false
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        prefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        bundle = intent.extras!!
        isSingleMode = bundle.getBoolean("isSingleMode")
        isHost = bundle.getBoolean("isHost")
        gameId = bundle.getString("gameId").toString()
        docRef = db.collection("current_games").document(gameId)
        ticTacToe = TicTacToe()
        ticTacToe.setDocRef(docRef)
        if (savedInstanceState != null)
            ticTacToe.setValuesOnRestart(
                savedInstanceState.getStringArray("board"),
                savedInstanceState.getString("turn"),
                savedInstanceState.getBoolean("isSingleMode")
            )
        gridView = findViewById(R.id.grid)
        turnText = findViewById(R.id.turn)
        oWinsText = findViewById(R.id.o_wins)
        xWinsText = findViewById(R.id.x_wins)
        tiesText = findViewById(R.id.ties)
        gameStateText = findViewById(R.id.game_status)
        scoreBoard = arrayOf(
            prefs.getInt("playerWins",0),
            prefs.getInt("ties",0),
            prefs.getInt("computerWins",0)
        )
        symbols = arrayOf(ticTacToe.getPersonSymbol(),ticTacToe.getComputerSymbol())
        gridView.setTicTacToe(ticTacToe)
        gridView.setOnTouchListener { _ , motionEvent ->
            touchGridEvent(motionEvent)
        }
        docRef.get()
            .addOnSuccessListener { result ->
                if(isHost)
                    ticTacToe.setThisSymbol(result.data?.get("hostPlayerSymbol").toString())
                else
                    ticTacToe.setThisSymbol(result.data?.get("guestPlayerSymbol").toString())
                Log.d("*******updateSymbolFromDB","nice")
                if(ticTacToe.getTurn() == ticTacToe.getThisSymbol())
                    ticTacToe.setIsListening(false)
                else
                    ticTacToe.setIsListening(true)
                registration = docRef.addSnapshotListener { value, error ->
                    if(error != null) {
                        Log.d("***","HABEMUS PROBLEMAS")
                    }
                    if(value != null && value.exists() && ticTacToe.getIsListening()){
                        if(value.data?.get("0") == "-" &&
                            value.data?.get("1") == "-" &&
                            value.data?.get("2") == "-" &&
                            value.data?.get("3") == "-" &&
                            value.data?.get("4") == "-" &&
                            value.data?.get("5") == "-" &&
                            value.data?.get("6") == "-" &&
                            value.data?.get("7") == "-" &&
                            value.data?.get("8") == "-"
                            ){
                            ticTacToe.setTurn(TicTacToe.xSymbol)
                            if(ticTacToe.getTurn() == ticTacToe.getThisSymbol())
                                ticTacToe.setIsListening(false)
                            gridView.isEnabled = true
                        }
                        else{
                            ticTacToe.changeTurn()
                        }
                        ticTacToe.updateBoardFromDB(gridView,::isGameFinished)
                    }
                }
                firstComputerMove()
                setInitialText()
                gridView.invalidate()
            }
            .addOnFailureListener { e -> Log.w("***Error updateSymbolFromDB", "e") }
    }

    override fun onResume() {
        super.onResume()
        Log.d("***Resume","Holi")
        xSound = MediaPlayer.create(applicationContext,R.raw.x_touch)
        oSound = MediaPlayer.create(applicationContext,R.raw.o_touch)
    }

    override fun onPause() {
        super.onPause()
        xSound.release()
        oSound.release()
    }

    override fun onStop() {
        super.onStop()
        if(isSingleMode) {
            val ed: SharedPreferences.Editor = prefs.edit()
            ed.putInt("playerWins", scoreBoard[0])
            ed.putInt("ties", scoreBoard[1])
            ed.putInt("computerWins", scoreBoard[2])
            ed.apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        registration.remove()
        docRef.delete()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray("board", ticTacToe.getBoard())
        outState.putString("turn", ticTacToe.getTurn())
        outState.putBoolean("isSingleMode", isSingleMode)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val difficultyItem = menu!!.findItem(R.id.ai_difficulty)
        difficultyItem.isVisible = this.isSingleMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.new_game -> {
                if(isSingleMode) {
                    ticTacToe.newGame(true)
                    loadTurnText()
                    gridView.invalidate()
                    gameStateText.text = " "
                    gridView.isEnabled = true
                    if (ticTacToe.getTurn() == ticTacToe.getComputerSymbol()) {
                        setComputerMove()
                    }
                }else{
                    ticTacToe.newGame(false)
                    ticTacToe.updateDBBoard(true)
                    loadTurnText()
                    gameStateText.text = " "
                    gridView.isEnabled = true
                    gridView.invalidate()
                }
                return true
            }
            R.id.ai_difficulty -> {
                val difficultyDialog = DifficultyDialog()
                difficultyDialog.setCurrentDifficulty(ticTacToe.getDifficulty())
                difficultyDialog.show(supportFragmentManager,"difficulty")
                return true
            }
            R.id.reset -> {
                val resetDialog = ResetDialog()
                resetDialog.show(supportFragmentManager, "quit")
                return true
            }
            R.id.about -> {
                val aboutDialog = AboutDialog()
                aboutDialog.show(supportFragmentManager, "about")
            }
        }
        return false
    }

    override fun onDialogEasyClick(dialog: DialogFragment) {
        ticTacToe.setDifficulty(TicTacToe.Difficulty.EASY)
    }
    override fun onDialogHardClick(dialog: DialogFragment) {
        ticTacToe.setDifficulty(TicTacToe.Difficulty.HARD)
    }

    override fun onDialogExpertClick(dialog: DialogFragment) {
        ticTacToe.setDifficulty(TicTacToe.Difficulty.EXPERT)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val ed: SharedPreferences.Editor = prefs.edit()
        ed.putInt("playerWins", 0)
        ed.putInt("ties", 0)
        ed.putInt("computerWins", 0)
        ed.apply()
        scoreBoard = arrayOf(0,0,0)
        ticTacToe.newGame(isSingleMode)
        setInitialText()
        gridView.invalidate()
    }

    private fun touchGridEvent(event: MotionEvent?): Boolean {
        if(!isGameFinished()){
            val xCoordinate = event?.x?.toInt()
            val yCoordinate = event?.y?.toInt()

            if (xCoordinate != null && yCoordinate != null) {
                val column = xCoordinate / gridView.getTileWidth()
                val row = yCoordinate / gridView.getTileHeight()
                val tilePosition = row * 3 + column

                if (isSingleMode) {
                    if (ticTacToe.getTurn() == ticTacToe.getPersonSymbol()) {
                        val tileWasChanged = setPlayerMove(tilePosition)
                        if (tileWasChanged) {
                            val isGameFinished = isGameFinished()
                            if (!isGameFinished) {
                                setComputerMove()
                            }
                        }
                    } else {
                        setComputerMove()
                    }
                } else {
                    if(ticTacToe.getTurn() == ticTacToe.getThisSymbol()) {
                        val tileWasChanged = setPlayerMove(tilePosition)
                        if (tileWasChanged) {
                            isGameFinished()
                            ticTacToe.updateDBBoard(false)
                            gridView.invalidate()
                        }
                    }
                    loadTurnText()
                }
            }
        }
        return true
    }
    private fun setInitialText(){
        loadTurnText()
        loadScoreBoardText()
    }

    private fun isGameFinished(): Boolean{
        if(ticTacToe.getCurrentState() != "GAME_CONTINUES") {
            return true
        }
        val result = ticTacToe.checkForWinner()
        when (result) {
            "X" -> {
                gameStateText.text = getString(R.string.x_wins)
                scoreBoard[0] = scoreBoard[0] + 1
                gridView.isEnabled = false
                ticTacToe.setIsListening(true)
            }

            "TIE" -> {
                gameStateText.text = getString(R.string.tie)
                scoreBoard[1] = scoreBoard[1] + 1
                gridView.isEnabled = false
                ticTacToe.setIsListening(true)
            }

            "O" -> {
                gameStateText.text = getString(R.string.o_wins)
                scoreBoard[2] = scoreBoard[2] + 1
                gridView.isEnabled = false
                ticTacToe.setIsListening(true)
            }

            else -> {
                gameStateText.text = " "
            }
        }
        loadTurnText()
        loadScoreBoardText()
        return result == "X" || result == "TIE" || result == "O"
    }

    private fun loadTurnText(){
        val turn = ticTacToe.getTurn()
        if(isSingleMode) {
            turnText.text = "Actual turn: $turn"
        }else {
            if(ticTacToe.getThisSymbol() == turn)
                turnText.text = "It's your turn ($turn)"
            else
                turnText.text = "Opponent's turn ($turn)"
        }
    }

    private fun loadScoreBoardText(){
        val xWins = scoreBoard[0].toString()
        val ties = scoreBoard[1].toString()
        val oWins = scoreBoard[2].toString()
        tiesText.text = "Ties: $ties"
        if(isSingleMode){
            xWinsText.text = "Player wins: $xWins"
            oWinsText.text = "Computer wins: $oWins"
        }else{
            if(ticTacToe.getThisSymbol() == TicTacToe.xSymbol){
                xWinsText.text = "Your wins: $xWins"
                oWinsText.text = "Opponent wins: $oWins"
            }else{
                xWinsText.text = "Your wins: $oWins"
                oWinsText.text = "Opponent wins: $xWins"
            }
        }
        gridView.invalidate()
    }

    private fun setPlayerMove(field: Int): Boolean{
        xSound.start()
        return if(ticTacToe.setPlayerMove(field)){
            gridView.invalidate()
            true
        }else{
            false
        }
    }

    private fun setComputerMove(){
        gridView.isEnabled = false
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            oSound.start()
            ticTacToe.setComputerMove()
            isGameFinished()
            gridView.isEnabled = true}, 1000)
        gridView.invalidate()
    }

    private fun firstComputerMove(){
        if(isSingleMode &&
            ticTacToe.getTurn()==ticTacToe.getComputerSymbol() &&
            ticTacToe.checkForWinner()==" "){
            setComputerMove()
        }
    }
}
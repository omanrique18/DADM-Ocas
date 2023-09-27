package co.edu.unal.tic_tac_toe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import co.edu.unal.tic_tac_toe.dialogs.AboutDialog
import co.edu.unal.tic_tac_toe.dialogs.DifficultyDialog
import co.edu.unal.tic_tac_toe.dialogs.QuitDialog


class TicTacToeActivity : AppCompatActivity(),
    DifficultyDialog.DifficultyDialogListener,
    QuitDialog.QuitDialogListener {
    private lateinit var ticTacToe: TicTacToe
    private lateinit var gridView: GridView
    private lateinit var turnText: TextView
    private lateinit var OWinsText: TextView
    private lateinit var XWinsText: TextView
    private lateinit var tiesText: TextView
    private lateinit var gameStateText: TextView
    private lateinit var scoreBoard: Array<Int>
    private lateinit var symbols: Array<String>
    private lateinit var bundle: Bundle
    private var isSingleMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        bundle = intent.extras!!
        isSingleMode = bundle.getBoolean("isSingleMode")

        ticTacToe = TicTacToe()
        gridView = findViewById<GridView>(R.id.grid)
        turnText = findViewById<TextView>(R.id.turn)
        OWinsText = findViewById<TextView>(R.id.o_wins)
        XWinsText = findViewById<TextView>(R.id.x_wins)
        tiesText = findViewById<TextView>(R.id.ties)
        gameStateText = findViewById<TextView>(R.id.game_status)
        scoreBoard = arrayOf(0,0,0)
        symbols = arrayOf(ticTacToe.getPersonSymbol(),ticTacToe.getComputerSymbol())

        gridView.setTicTacToe(ticTacToe)
        gridView.setOnTouchListener { _ , motionEvent -> touchGridEvent(motionEvent)
        }
        setInitialText()

        firstComputerMove()
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
                ticTacToe.newGame()
                loadTurnText()
                gridView.invalidate()
                gameStateText.text = " "
                gridView.isEnabled = true
                if(isSingleMode && ticTacToe.getTurn()==ticTacToe.getComputerSymbol()){
                    ticTacToe.setComputerMove()
                    isGameFinished()
                }
                return true
            }
            R.id.ai_difficulty -> {
                val difficultyDialog = DifficultyDialog()
                difficultyDialog.setCurrentDifficulty(ticTacToe.getDifficulty())
                difficultyDialog.show(supportFragmentManager,"difficulty")
                return true
            }
            R.id.quit -> {
                val quitDialog = QuitDialog()
                quitDialog.show(supportFragmentManager, "quit")
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
        this.finish()
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
                                isGameFinished()
                            }
                        }
                    } else {
                        setComputerMove()
                        isGameFinished()
                    }
                } else {
                    val tileWasChanged = setPlayerMove(tilePosition)
                    if (tileWasChanged) {
                        isGameFinished()
                    }
                }
            }
        }
        return true
    }
    private fun setInitialText(){
        loadTurnText()
        loadScoreBoardText()
        isGameFinished()
    }

    private fun isGameFinished(): Boolean{
        val result = ticTacToe.checkForWinner()
        when(result){
            "X" -> {
                gameStateText.text = getString(R.string.x_wins)
                scoreBoard[0] = scoreBoard[0]+1
                gridView.isEnabled = false
            }
            "TIE" -> {
                gameStateText.text = getString(R.string.tie)
                scoreBoard[1] = scoreBoard[1]+1
                gridView.isEnabled = false
            }
            "O" -> {
                gameStateText.text = getString(R.string.o_wins)
                scoreBoard[2] = scoreBoard[2]+1
                gridView.isEnabled = false
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
        turnText.text = "Actual turn: $turn"
    }

    private fun loadScoreBoardText(){
        val xWins = scoreBoard[0].toString()
        val ties = scoreBoard[1].toString()
        val oWins = scoreBoard[2].toString()
        tiesText.text = "Ties: $ties"
        if(isSingleMode){
            XWinsText.text = "Player wins: $xWins"
            OWinsText.text = "Computer wins: $oWins"
        }else{
            XWinsText.text = "X wins: $xWins"
            OWinsText.text = "O wins: $oWins"
        }

    }

    private fun setPlayerMove(field: Int): Boolean{
        return if(ticTacToe.setPlayerMove(field)){
            gridView.invalidate()
            true
        }else{
            false
        }
    }

    private fun setComputerMove(){
        ticTacToe.setComputerMove()
        gridView.invalidate()
    }

    private fun firstComputerMove(){
        if(isSingleMode && ticTacToe.getTurn()==ticTacToe.getComputerSymbol()){
            ticTacToe.setComputerMove()
            isGameFinished()
        }
    }
}
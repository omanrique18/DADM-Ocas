package co.edu.unal.tic_tac_toe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
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
    private lateinit var turnText: TextView
    private lateinit var OWinsText: TextView
    private lateinit var XWinsText: TextView
    private lateinit var tiesText: TextView
    private lateinit var gameStateText: TextView
    private lateinit var boardButtons: ArrayList<Button>
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
        turnText = findViewById<TextView>(R.id.turn)
        OWinsText = findViewById<TextView>(R.id.o_wins)
        XWinsText = findViewById<TextView>(R.id.x_wins)
        tiesText = findViewById<TextView>(R.id.ties)
        gameStateText = findViewById<TextView>(R.id.game_status)
        boardButtons = arrayListOf<Button>()
        boardButtons.add(findViewById(R.id.tile0))
        boardButtons.add(findViewById(R.id.tile1))
        boardButtons.add(findViewById(R.id.tile2))
        boardButtons.add(findViewById(R.id.tile3))
        boardButtons.add(findViewById(R.id.tile4))
        boardButtons.add(findViewById(R.id.tile5))
        boardButtons.add(findViewById(R.id.tile6))
        boardButtons.add(findViewById(R.id.tile7))
        boardButtons.add(findViewById(R.id.tile8))
        scoreBoard = arrayOf(0,0,0)
        symbols = arrayOf(ticTacToe.getPersonSymbol(),ticTacToe.getComputerSymbol())

        setInitialText()
        var computerTile = ticTacToe.setComputerMove()

        if(isSingleMode && ticTacToe.getTurn()==ticTacToe.getComputerSymbol()){
            val actualTurn = ticTacToe.getTurn()
            boardButtons[computerTile].text = actualTurn
            isGameFinished()
        }

        for ((i, button) in boardButtons.withIndex()) {
            button.setOnClickListener {
                var actualTurn = ticTacToe.getTurn()
                if(isSingleMode){
                    if(actualTurn == ticTacToe.getPersonSymbol()){
                        val tileWasChanged = ticTacToe.setPlayerMove(i)
                        if (tileWasChanged) {
                            button.text = actualTurn
                            actualTurn = ticTacToe.getTurn()
                            val isGameFinished = isGameFinished()
                            if(!isGameFinished) {
                                computerTile = ticTacToe.setComputerMove()
                                boardButtons[computerTile].text = actualTurn
                                isGameFinished()
                            }
                        }
                    }else{
                        computerTile = ticTacToe.setComputerMove()
                        boardButtons[computerTile].text = actualTurn
                        isGameFinished()
                    }
                }else{
                    val tileWasChanged = ticTacToe.setPlayerMove(i)
                    if (tileWasChanged) {
                        button.text = actualTurn
                        isGameFinished()
                    }
                }
            }
        }
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
                for (button in boardButtons) {
                    button.text = "-"
                }
                gameStateText.text = " "
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
            }
            "TIE" -> {
                gameStateText.text = getString(R.string.tie)
                scoreBoard[1] = scoreBoard[1]+1
            }
            "O" -> {
                gameStateText.text = getString(R.string.o_wins)
                scoreBoard[2] = scoreBoard[2]+1
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
        XWinsText.text = "X wins: $xWins"
        tiesText.text = "Ties: $ties"
        OWinsText.text = "O wins: $oWins"
    }
}
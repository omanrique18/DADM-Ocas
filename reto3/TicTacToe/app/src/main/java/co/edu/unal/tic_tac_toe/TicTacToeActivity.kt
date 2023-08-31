package co.edu.unal.tic_tac_toe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class TicTacToeActivity : AppCompatActivity() {
    private lateinit var ticTacToe: TicTacToe
    private lateinit var turnText: TextView
    private lateinit var OWinsText: TextView
    private lateinit var XWinsText: TextView
    private lateinit var tiesText: TextView
    private lateinit var gameStateText: TextView
    private lateinit var newGameButton: Button
    private lateinit var boardButtons: ArrayList<Button>
    private lateinit var scoreBoard: Array<Int>
    private lateinit var symbols: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)

        val bundle = intent.extras!!
        val isSingleMode = bundle.getBoolean("isSingleMode")

        ticTacToe = TicTacToe()
        turnText = findViewById<TextView>(R.id.turn)
        OWinsText = findViewById<TextView>(R.id.o_wins)
        XWinsText = findViewById<TextView>(R.id.x_wins)
        tiesText = findViewById<TextView>(R.id.ties)
        gameStateText = findViewById<TextView>(R.id.game_status)
        newGameButton = findViewById<Button>(R.id.new_game)
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

        if(isSingleMode && ticTacToe.getTurn()==ticTacToe.getComputerSymbol()){
            var actualTurn = ticTacToe.getTurn()
            var computerTile = ticTacToe.setComputerMove()
            boardButtons[computerTile].text = actualTurn
            isGameFinished()
        }

        newGameButton.setOnClickListener{
            ticTacToe.newGame()
            loadTurnText()
            for (button in boardButtons) {
                button.text = "-"
            }
            gameStateText.text = " "
        }

        for ((i, button) in boardButtons.withIndex()) {
            button.setOnClickListener {
                var computerTile = 0
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
                    if (tileWasChanged)
                        button.text = actualTurn
                        isGameFinished()
                }
            }
        }
    }

    private fun setInitialText(){
        loadTurnText()
        loadScoreBoardText()
        isGameFinished()
    }

    private fun isGameFinished(): Boolean{
        var result = ticTacToe.checkForWinner()
        when(result){
            "X" -> {
                gameStateText.text = "X Wins!"
                scoreBoard[0] = scoreBoard[0]+1
            }
            "TIE" -> {
                gameStateText.text = "It's a tie!"
                scoreBoard[1] = scoreBoard[1]+1
            }
            "O" -> {
                gameStateText.text = "O Wins!"
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
        var turn = ticTacToe.getTurn()
        turnText.text = "Actual turn: $turn"
    }

    private fun loadScoreBoardText(){
        var xWins = scoreBoard[0].toString()
        var ties = scoreBoard[1].toString()
        var oWins = scoreBoard[2].toString()
        XWinsText.text = "X wins: $xWins"
        tiesText.text = "Ties: $ties"
        OWinsText.text = "O wins: $oWins"
    }

}
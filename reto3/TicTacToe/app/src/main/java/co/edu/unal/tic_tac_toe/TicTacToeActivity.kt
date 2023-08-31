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

        newGameButton.setOnClickListener{
            ticTacToe.newGame()
        }
        for ((i, button) in boardButtons.withIndex()) {
            button.setOnClickListener {
                ticTacToe.setPlayerMove(i)
                checkForWinner()
                if(isSingleMode){
                    ticTacToe.setComputerMove()
                }


            }
        }

    }

    private fun setInitialText(){

    }

    private fun checkForWinner(){
        var result = ticTacToe.checkForWinner()
        when(result){
            "X" -> gameStateText.text = "X Wins!"
            "O" -> gameStateText.text = "O Wins!"
            "TIE" -> gameStateText.text = "It's a tie!"
        }
    }

}
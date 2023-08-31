package co.edu.unal.tic_tac_toe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainMenuActivity : AppCompatActivity() {
    private lateinit var singlePlayerButton: Button
    private lateinit var multiPlayerButton: Button

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
            val intent = Intent(this,TicTacToeActivity::class.java)
            intent.putExtra("isSingleMode", false)
            startActivity(intent)
        }
    }
}
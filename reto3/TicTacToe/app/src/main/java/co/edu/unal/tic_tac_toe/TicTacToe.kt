package co.edu.unal.tic_tac_toe

import android.util.Log
import com.google.firebase.firestore.DocumentReference

class TicTacToe() {
    companion object{
        var xSymbol = "X"
        var oSymbol = "O"
        fun alternateSymbols(symbol: String): String{
            return if (symbol == oSymbol)
                xSymbol
            else
                oSymbol
        }
    }
    private var currentDifficulty = Difficulty.EASY
    private var currentState = GameState.GAME_CONTINUES
    private var board: Array<String>
    private var personSymbol: String
    private var computerSymbol: String
    private var turn: String
    private var isListening = false
    private lateinit var thisSymbol: String
    private lateinit var docRef: DocumentReference

    init{
        this.board = Array(9) { "-" }
        this.turn = xSymbol
        this.personSymbol = xSymbol
        this.computerSymbol = oSymbol
    }

    fun getTurn(): String{
        return this.turn
    }

    fun getPersonSymbol(): String{
        return this.personSymbol
    }

    fun getComputerSymbol(): String{
        return this.computerSymbol
    }

    fun getThisSymbol(): String{
        return this.thisSymbol
    }

    fun getBoard(): Array<String>{
        return this.board
    }

    fun getDifficulty(): Difficulty {
        return this.currentDifficulty
    }

    fun getCurrentState(): String{
        return this.currentState.name
    }
    fun getIsListening(): Boolean{
        return this.isListening
    }

    fun setTurn(turn: String){
        this.turn = turn
    }

    fun setIsListening(isListening: Boolean){
        this.isListening = isListening
    }

    fun setDifficulty(difficulty: Difficulty) {
        this.currentDifficulty = difficulty
    }

    fun setDocRef(docRef: DocumentReference){
        this.docRef = docRef
    }

    fun setThisSymbol(symbol: String){
        this.thisSymbol = symbol
    }

    fun changeTurn(){
        this.turn = alternateSymbols(this.turn)
    }

    fun checkForWinner(): String {
        var i = 0
        // Check rows
        while (i <= 6) {
            if (board[i] == oSymbol && board[i + 1] == oSymbol && board[i + 2] == oSymbol) {
                this.currentState = GameState.O
                return GameState.O.name
            }
            if (board[i] == xSymbol && board[i + 1] == xSymbol && board[i + 2] == xSymbol) {
                this.currentState = GameState.X
                return GameState.X.name
            }
            i += 3
        }

        // Check columns
        for (i in 0..2) {
            if (board[i] == oSymbol && board[i + 3] == oSymbol && board[i + 6] == oSymbol) {
                this.currentState = GameState.O
                return GameState.O.name
            }
            if (board[i] == xSymbol && board[i + 3] == xSymbol && board[i + 6] == xSymbol) {
                this.currentState = GameState.X
                return GameState.X.name
            }
        }

        // Check diagonals
        if (board[0] == oSymbol && board[4] == oSymbol && board[8] == oSymbol ||
            board[2] == oSymbol && board[4] == oSymbol && board[6] == oSymbol) {
            this.currentState = GameState.O
            return GameState.O.name
        }
        if (board[0] == xSymbol && board[4] == xSymbol && board[8] == xSymbol ||
            board[2] == xSymbol && board[4] == xSymbol && board[6] == xSymbol) {
            this.currentState = GameState.X
            return GameState.X.name
        }

        // Check for tie
        for (i in 0..8) {
            if (board[i] == "-")
                return GameState.GAME_CONTINUES.name
        }
        this.currentState = GameState.TIE
        return GameState.TIE.name
    }

    fun setPlayerMove(field: Int): Boolean{
        val tileWasChanged: Boolean
        if (this.board[field] == "-") {
            this.board[field] = this.turn
            changeTurn()
            tileWasChanged = true
        }else{
            tileWasChanged = false
        }
        return tileWasChanged
    }

    fun setComputerMove(){
        var move: Int
        var result: Int
        if (this.currentDifficulty == Difficulty.HARD || this.currentDifficulty == Difficulty.EXPERT) {
            result = makeWinningMove()
            if(result != -1) return
        }
        if (this.currentDifficulty == Difficulty.EXPERT) {
            result = makeBlockingMove()
            if (result != -1) return
        }
        // Generate random move
        do {
            move = (0..8).random()
        } while (this.board[move] == personSymbol || this.board[move] == computerSymbol)
        this.board[move] = computerSymbol
        changeTurn()
    }

    private fun makeWinningMove(): Int {
        for (i in 0..8) {
            if (this.board[i] !== xSymbol && this.board[i] !== oSymbol) {
                val curr = this.board[i]
                this.board[i] = this.computerSymbol
                if (checkForWinner() == this.computerSymbol) {
                    changeTurn()
                    return i
                }
                else
                    this.board[i] = curr
            }
        }
        return -1
    }

    private fun makeBlockingMove(): Int{
        for (i in 0..8) {
            if (this.board[i] !== xSymbol && this.board[i] !== oSymbol) {
                val curr = this.board[i] // Save the current number
                this.board[i] = this.personSymbol
                if (checkForWinner() == this.personSymbol) {
                    this.board[i] = this.computerSymbol
                    changeTurn()
                    return i
                } else
                    this.board[i] = curr
            }
        }
        return -1
    }

    fun newGame(isSingleMode: Boolean){
        if(isSingleMode) {
            this.board = Array(9) { "-" }
            this.turn = xSymbol
            this.personSymbol = xSymbol
            this.computerSymbol = oSymbol
            this.currentState = GameState.GAME_CONTINUES
        }else{
            this.board = Array(9) { "-" }
            this.turn = xSymbol
            this.currentState = GameState.GAME_CONTINUES
        }
    }

    fun setValuesOnRestart(board: Array<String>?, turn: String?, isSingleMode: Boolean){
        if(board != null && turn != null){
            this.board = board
            this.turn = turn
        }else{
            newGame(isSingleMode)
        }

    }

    fun updateBoardFromDB(gridView: GridView, fn: () -> Any){
        docRef.get()
            .addOnSuccessListener { result ->
                for (i in 0..8){
                    this.board[i] = result.data?.get(i.toString()).toString()
                }
                Log.d("*******updateBoardFromDB","nice")
                if(getTurn() == getThisSymbol())
                    this.isListening = false
                fn()
                gridView.invalidate()
            }
            .addOnFailureListener { e -> Log.w("***Error updateBoardFromDB", "e") }
    }

    fun updateDBBoard(newGame: Boolean){
        val map = mutableMapOf<String,String>()
        for(i in 0..8){
            map[i.toString()] = this.board[i]
        }
        docRef.update(map as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("*******updateDBBoard","nice")
                if(newGame){
                    if(this.turn == this.getThisSymbol())
                        this.isListening = false
                }else{
                    this.isListening = true
                }
            }
            .addOnFailureListener { e -> Log.w("***Error updateDBBoard", "e") }
    }

    private enum class GameState{
        GAME_CONTINUES, TIE, X, O
    }

    enum class Difficulty{
        EASY, HARD, EXPERT
    }
}
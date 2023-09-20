package co.edu.unal.tic_tac_toe

class TicTacToe() {
    private val xSymbol = "X"
    private val oSymbol = "O"
    private var currentDifficulty = Difficulty.EASY
    private var board: Array<String>
    private var personSymbol: String
    private var computerSymbol: String
    private var turn: String

    init{
        this.board = Array(9) { "-" }
        this.turn = setOf(oSymbol,xSymbol).random()
        this.personSymbol = setOf(oSymbol,xSymbol).random()
        this.computerSymbol = alternateSymbols(this.personSymbol)
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

    fun getDifficulty(): Difficulty {
        return this.currentDifficulty
    }
    fun setDifficulty(difficulty: Difficulty) {
        this.currentDifficulty = difficulty
    }

    fun getBoard(): Array<String>{
        return this.board
    }

    fun checkForWinner(): String {
        var i = 0
        // Check rows
        while (i <= 6) {
            if (board[i] === oSymbol && board[i + 1] === oSymbol && board[i + 2] === oSymbol)
                return GameState.O.name
            if (board[i] === xSymbol && board[i + 1] === xSymbol && board[i + 2] === xSymbol)
                return GameState.X.name
            i += 3
        }

        // Check columns
        for (i in 0..2) {
            if (board[i] === oSymbol && board[i + 3] === oSymbol && board[i + 6] === oSymbol)
                return GameState.O.name
            if (board[i] === xSymbol && board[i + 3] === xSymbol && board[i + 6] === xSymbol)
                return GameState.X.name
        }

        // Check diagonals
        if (board[0] === oSymbol && board[4] === oSymbol && board[8] === oSymbol ||
            board[2] === oSymbol && board[4] === oSymbol && board[6] === oSymbol)
            return GameState.O.name
        if (board[0] === xSymbol && board[4] === xSymbol && board[8] === xSymbol ||
            board[2] === xSymbol && board[4] === xSymbol && board[6] === xSymbol)
            return GameState.X.name

        // Check for tie
        for (i in 0..8) {
            if (board[i] == "-")
                return GameState.GAME_CONTINUES.name
        }
        return GameState.TIE.name
    }

    fun setPlayerMove(field: Int): Boolean{
        val tileWasChanged: Boolean
        if (this.board[field] == "-") {
            this.board[field] = this.turn
            this.turn = alternateSymbols(this.turn)
            tileWasChanged = true
        }else{
            tileWasChanged = false
        }
        return tileWasChanged
    }

    fun setComputerMove(): Int{
        var move: Int
        var result: Int
        if (this.currentDifficulty == Difficulty.HARD || this.currentDifficulty == Difficulty.EXPERT) {
            result = makeWinningMove()
            if(result != -1) return result
        }
        if (this.currentDifficulty == Difficulty.EXPERT) {
            result = makeBlockingMove()
            if (result != -1) return result
        }
        // Generate random move
        do {
            move = (0..8).random()
        } while (this.board[move] === personSymbol || this.board[move] === computerSymbol)
        this.board[move] = computerSymbol
        this.turn = alternateSymbols(this.turn)
        return move
    }

    private fun makeWinningMove(): Int {
        for (i in 0..8) {
            if (this.board[i] !== xSymbol && this.board[i] !== oSymbol) {
                val curr = this.board[i]
                this.board[i] = this.computerSymbol
                if (checkForWinner() == this.computerSymbol) {
                    this.turn = alternateSymbols(this.turn)
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
                    this.turn = alternateSymbols(this.turn)
                    return i
                } else
                    this.board[i] = curr
            }
        }
        return -1
    }

    fun newGame(){
        this.board = Array(9) { "-" }
        this.turn = setOf(oSymbol,xSymbol).random()
        this.personSymbol = setOf(oSymbol,xSymbol).random()
        this.computerSymbol = alternateSymbols(this.personSymbol)
    }

    private fun alternateSymbols(symbol: String): String{
        return if (symbol == oSymbol)
            xSymbol
        else
            oSymbol
    }

    private enum class GameState{
        GAME_CONTINUES, TIE, X, O
    }

    enum class Difficulty{
        EASY, HARD, EXPERT
    }
}
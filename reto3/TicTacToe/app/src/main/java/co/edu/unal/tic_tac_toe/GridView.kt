package co.edu.unal.tic_tac_toe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View


class GridView(context: Context, attrs: AttributeSet): View(context,attrs) {
    private lateinit var ticTacToe: TicTacToe
    private var xBitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.x_symbol)
    private var oBitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.o_symbol)
    private var gridBitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.grid)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val gridWidth = width
        val gridHeight = height
        val tileWidth = getTileWidth()
        val tileHeight = getTileHeight()
        val board = ticTacToe.getBoard()
        val gridArea = Rect(0,0,gridWidth,gridHeight)
        canvas!!.drawBitmap(gridBitmap!!, null, gridArea, null)

        for (i in 0..8){
            val column = i % 3
            val row = i / 3
            val left = column * tileWidth + 70
            val right = (column + 1) * tileWidth - 70
            val top = row * tileHeight + 70
            val bottom = (row + 1) * tileHeight - 70
            val tileArea = Rect(left,top,right,bottom)

            if (board[i] == TicTacToe.xSymbol)
                canvas.drawBitmap(xBitmap!!, null, tileArea, null)
            if (board[i] == TicTacToe.oSymbol)
                canvas.drawBitmap(oBitmap!!, null, tileArea, null)
        }
    }

    fun setTicTacToe(ticTacToe: TicTacToe){
        this.ticTacToe = ticTacToe
        this.invalidate()
    }

    fun getTileHeight(): Int{
        return height / 3
    }

    fun getTileWidth(): Int{
        return width / 3
    }
}
package co.edu.unal.tic_tac_toe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View


class GridView(context: Context, attrs: AttributeSet, defStyle: Int): View(context,attrs,defStyle) {
    private var xBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.x_symbol)
    private var oBitmap: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.o_symbol)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val gridWidth = width
        val gridHeight = height
        val tileWidth = gridWidth / 3
        val tileHeight = gridHeight / 3

        for (i in 0..8){
            val column = i % 3
            val row = i / 3

            var left = column * tileWidth
            var right = (column + 1) * tileWidth
            var top = row * tileHeight
            var bottom = (row + 1) * tileHeight

        }
    }
}
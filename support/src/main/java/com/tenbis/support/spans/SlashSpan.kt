package com.tenbis.support.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * A [android.text.style.ReplacementSpan] used for slashes in [android.widget.EditText].
 * Adds ' / '
 */
class SlashSpan : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val slash = paint.measureText(DIVIDER, 0, 1)
        val textSize = paint.measureText(text, start, end)
        return (slash + textSize).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val spannedText = "${text.subSequence(start, end)}$DIVIDER"
        canvas.drawText(spannedText, x, y.toFloat(), paint)
    }
    companion object {
        private const val DIVIDER = "/"
    }
}
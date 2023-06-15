package com.zoom2u_customer.utility

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

open class  CustomTypefaceSpan(val famil: String?, val type: Typeface) :
    TypefaceSpan(famil) {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, type)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, type)
    }



    open fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0
        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.typeface = tf
    }


}
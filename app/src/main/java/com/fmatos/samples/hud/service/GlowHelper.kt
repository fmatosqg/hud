package com.fmatos.samples.hud.service

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.fmatos.samples.hud.R


class GlowHelper(context: Context) : ContextWrapper(context) {


    fun setBackgroundGlow(view: View, @DrawableRes drawableRes: Int, @ColorInt glowColor: Int) {
        // An added margin to the initial image

        val halfMargin = view.resources.getDimension(R.dimen.cardMargin)
        val margin = halfMargin.toInt() * 2
        // the glow radius
        val glowRadius = halfMargin * 1.2 // this is a magic number, don't change it !!!

        val drawable = view.resources.getDrawable(drawableRes, null)

        val src = drawableToBitmap(drawable = drawable)

        // extract the alpha layer from the source image
        val alpha = src.extractAlpha()

        // The output bitmap (with the icon + glow)
        val bmp = Bitmap.createBitmap(src.width + margin, src.height + margin, Bitmap.Config.ARGB_8888)

        // The canvas to paint on the image
        val canvas = Canvas(bmp)

        val paint = Paint()
        paint.color = glowColor

        // outer glow
        paint.maskFilter = BlurMaskFilter(glowRadius.toFloat(), Blur.OUTER)
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint)
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint)

        // make for a stronger brighter margin
        paint.maskFilter = BlurMaskFilter(20f, Blur.OUTER)
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint)

        // original icon
        canvas.drawBitmap(src, halfMargin, halfMargin, null)

        paint.maskFilter = BlurMaskFilter(glowRadius.toFloat(), Blur.INNER)//For Inner glow set Blur.INNER
//        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint)


        view.background = BitmapDrawable(resources, bmp)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
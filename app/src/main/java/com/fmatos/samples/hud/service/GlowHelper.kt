package com.fmatos.samples.hud.service

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.support.annotation.ColorInt
import android.widget.ImageView


class GlowHelper(context: Context) : ContextWrapper(context) {


    fun setBackgroundGlow(imgview: ImageView, imageicon: Int, @ColorInt glowColor: Int) {
        // An added margin to the initial image
        val margin = 24
        val halfMargin = (margin / 2).toFloat()
        // the glow radius
        val glowRadius = 40

        // The original image to use
        val src = BitmapFactory.decodeResource(getResources(), imageicon)

        // extract the alpha from the source image
        val alpha = src.extractAlpha()

        // The output bitmap (with the icon + glow)
        val bmp = Bitmap.createBitmap(src.width + margin, src.height + margin, Bitmap.Config.ARGB_8888)

        // The canvas to paint on the image
        val canvas = Canvas(bmp)

        val paint = Paint()
        paint.color = glowColor

        // outer glow
        paint.maskFilter = BlurMaskFilter(glowRadius.toFloat(), Blur.OUTER)//For Inner glow set Blur.INNER
        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint)

        // original icon
        canvas.drawBitmap(src, halfMargin, halfMargin, null)

        paint.maskFilter = BlurMaskFilter(glowRadius.toFloat(), Blur.INNER)//For Inner glow set Blur.INNER
//        canvas.drawBitmap(alpha, halfMargin, halfMargin, paint)


        imgview.setImageBitmap(bmp)


    }
}
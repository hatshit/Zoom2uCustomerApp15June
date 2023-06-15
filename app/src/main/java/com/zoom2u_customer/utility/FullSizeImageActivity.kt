package com.zoom2u_customer.utility

import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityFullSizeImageBinding
import kotlin.math.min


class FullSizeImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityFullSizeImageBinding
    private var photo:String?=null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_size_image)
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        if (intent.hasExtra("Photo")) {
             photo= intent.getStringExtra("Photo")
            if(!TextUtils.isEmpty(photo)) {
                binding.photo.setImageBitmap(AppUtility.getBitmapFromURL(photo))
            }

        }
    }


    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = 0.1f.coerceAtLeast(min(scaleFactor, 10.0f))
            binding.photo.scaleX = scaleFactor
            binding.photo.scaleY = scaleFactor
            return true
        }
    }

}
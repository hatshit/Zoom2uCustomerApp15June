package com.zoom2u_customer.utility

class Zoom2uContractProvider {
    companion object {

        const val API_KEY_GEOCODER_DIRECTION =
            "AIzaSyDXy3Z6OzAQ3siNfARS3Y54-sbhNQSBL0U" // Purchased account zoom.2ua@gmail.com


        const val PHONE_REGEX = "^[\\s0-9\\()\\-\\+]+$"

        val DB_NAME = "locate2u"
        var galleryPermissions = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )

        var cameraPermissions = arrayOf(
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE" //"android.permission.CAMERA",
        )
    }
}
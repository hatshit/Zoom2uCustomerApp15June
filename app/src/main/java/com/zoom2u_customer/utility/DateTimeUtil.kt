package com.zoom2u_customer.utility

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtil {

    companion object{



        /**************  Convert date time for delivery speed ETA	 */
        @SuppressLint("SimpleDateFormat")
        fun getDateTimeFromDeviceForDeliveryETA(serverDateTimeValue: String): String? {
            val dateTimeReturn: String? = null
            try {
                if (serverDateTimeValue != "") {
                    val converter = SimpleDateFormat("EEE dd MMM yyyy hh:mm a")
                    var convertedDate: Date? = Date()
                    try {
                        convertedDate = converter.parse(serverDateTimeValue)
                        val dateFormatter =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US)
                        return dateFormatter.format(convertedDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return dateTimeReturn
        }


        @SuppressLint("SimpleDateFormat")
        fun getServerFormatFromServerFormat(serverDateTimeValue: String?): String? {
            try {
                if (serverDateTimeValue != "") {
                    val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    var convertedDate: Date? = Date()
                    try {
                        convertedDate = converter.parse(serverDateTimeValue)
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
                        return dateFormatter.format(convertedDate)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }


        @SuppressLint("SimpleDateFormat")
        fun getTimeFromDateFormat(serverDateTimeValue: String?): String? {
            try {
                if (serverDateTimeValue != "") {
                    val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    var convertedDate: Date?
                    try {
                        convertedDate = converter.parse(serverDateTimeValue)
                        val dateFormatter = SimpleDateFormat("yyyy:MM:dd:HH:mm")
                        return dateFormatter.format(convertedDate)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }


        @SuppressLint("SimpleDateFormat")
        fun getTimeFromDateFormat1(serverDateTimeValue: String?): String? {
            try {
                if (serverDateTimeValue != "") {
                    val converter = SimpleDateFormat("EEE dd MMM yyyy hh:mm a")
                    val convertedDate: Date?
                    try {
                        convertedDate = converter.parse(serverDateTimeValue)
                        val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                        return dateFormatter.format(convertedDate)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

    }
}
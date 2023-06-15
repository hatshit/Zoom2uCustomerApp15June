package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuoteOptionClass (
  var `$id`: String? = null,
  var `$type`: String? = null,
  var ETA: String? = null,
  var Price: Double? = null,
  var BookingFee: Double? = null,
  var PickupDistance: String? = null,
  var DropDistance: String? = null,
  var PickupPrice: Double? = null,
  var DropPrice: Double? = null,
  var InterstatePrice: Double? = null,
  var DeliverySpeed: String? = null,
  var Distance: String? = null,
  var LiveLocationTracking: Boolean? = null,
  var DirectDriverContact: Boolean? = null,
  var PrinterRequired: Boolean? = null,
  var isAvailable: Boolean? = null,
  var DropDateTime: String? = null,
  var EarliestPickupDateTime: String? = null,
  var isPriceSelect:Boolean?=true
): Parcelable

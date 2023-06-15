package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.suggest_price

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    var Packaging: String? = null,
    var Quantity: Int? = null,
    var LengthCm: Int? = null,
    var HeightCm: Int? = null,
    var WidthCm: Int? = null,
    var ItemWeightKg: Double? = null,
    var TotalWeightKg: String? = null,
    var ContainerSize: String? = ""
): Parcelable
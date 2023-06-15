package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShipmentsClass(
    var Category: String? = null,
    var Quantity: Int? = null,
    var Value: Int? = null,
    var LengthCm: Int? = null,
    var HeightCm: Int? = null,
    var WidthCm: Int? = null,
    var ItemWeightKg: Double? = null,
    var TotalWeightKg: String? = null,
    var Packaging:String?= null
): Parcelable

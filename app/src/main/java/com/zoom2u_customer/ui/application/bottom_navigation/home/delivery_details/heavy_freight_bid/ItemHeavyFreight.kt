package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemHeavyFreight (
    var Packaging: String? = null,
    var Quantity: String? = null,
    var LengthCm: String? = null,
    var HeightCm: String? = null,
    var WidthCm: String? = null,
    var ItemWeightKg: String? = null,
    var TotalWeightKg: String? = null,
    var ContainerSize: String? = ""
): Parcelable
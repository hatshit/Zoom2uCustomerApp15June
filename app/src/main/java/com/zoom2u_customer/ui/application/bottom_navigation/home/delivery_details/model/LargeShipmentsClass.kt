package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LargeShipmentsClass (
    var Category: String? = null,
    var Quantity: Int? = null,
    var Value: Int? = null
    ): Parcelable
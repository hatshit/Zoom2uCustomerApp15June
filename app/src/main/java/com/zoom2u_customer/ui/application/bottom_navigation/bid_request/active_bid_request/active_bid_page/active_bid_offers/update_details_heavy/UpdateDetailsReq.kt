package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateDetailsReq(
    var requestId: Int? = null,
    var offerId: Int? = null,
    var carrierPrice: Int? = null,
    var customerPrice: Double? = null,
    var orderNo: String? = null,
) : Parcelable
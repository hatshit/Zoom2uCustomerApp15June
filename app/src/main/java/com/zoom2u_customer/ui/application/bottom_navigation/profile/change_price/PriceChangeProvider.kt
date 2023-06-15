package com.zoom2u_customer.ui.application.bottom_navigation.profile.change_price

object PriceChangeProvider {
    val priceChangeList: MutableList<PriceChange> = ArrayList()

    private fun addPriceChange(price_header:String) {

        val item = PriceChange(price_header)
        priceChangeList.add(item)
    }

    init {
        addPriceChange("Same Day - Pricing")
        addPriceChange("3 Hour - Pricing")
        addPriceChange("5 Hour - Pricing")
        addPriceChange("VIP - Pricing")

    }
}
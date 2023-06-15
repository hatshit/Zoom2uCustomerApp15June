package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.interstate_booking

import com.zoom2u_customer.R


object DangerIconDataProvider {
    val iconList: MutableList<DangerIcon> = ArrayList()

    private fun addIcon( image: Int,text: String) {

        val item = DangerIcon(image,text)
        iconList.add(item)

    }

    init {
        addIcon(R.drawable.danger_corrosives,"Corrosives")
        addIcon(R.drawable.danger_gas,"Gases")
        addIcon(R.drawable.danger_fule,"Flammable Liquids")
        addIcon(R.drawable.danger_paint,"Oxodising materials")
        addIcon(R.drawable.danger_bleach,"Organic peroxides")
        addIcon(R.drawable.danger_toxic,"Toxic substances")
        addIcon(R.drawable.danger_radioactive,"Radioactive materials")
        addIcon(R.drawable.danger_blood,"Infectious substances")
        addIcon(R.drawable.danger_flare,"Explosive items")
        addIcon(R.drawable.danger_misc,"Miscellaneous including dry ice and petrol engines")
        addIcon(R.drawable.danger_flammable,"Flammable solids")
        addIcon(R.drawable.danger_magnet,"Magnetised materials")

    }
}
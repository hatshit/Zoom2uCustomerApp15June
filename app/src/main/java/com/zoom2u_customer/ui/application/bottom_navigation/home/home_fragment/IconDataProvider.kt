package com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment

import com.zoom2u_customer.R


object IconDataProvider {
    val iconList: MutableList<Icon> = ArrayList()

    private fun addIcon( image: Int,text: String,desc : String,weight: Double,length : Int,
                         width : Int,height : Int,Category:String,Value:Int,ItemWeightKg:Int) {

        val item = Icon(image,text,desc,0,weight,length,width,height,Category,Value,ItemWeightKg,false)
        iconList.add(item)

    }

    init {
        addIcon(R.drawable.ic_documents,"Documents","A4 Envelopes, letters, or legal documents",0.5,40,20,10,"Documents",10,0)
        addIcon(R.drawable.ic_satchel_and_laptops,"Satchel, laptops","Laptop bags, satchels, or briefcases",3.toDouble(),50,30,20,"Bag",11,0)
        addIcon(R.drawable.ic_small_box,"Small box","Computer, microwaves, clothing, small kitchen appliances, case of beer",10.toDouble(),40,20,30,"Box",12,0)
        addIcon(R.drawable.ic_cakes_flowers_delicates,"Cakes, flowers, delicates","Single, double tier cakes, bouquets, pastries, pottery",4.toDouble(),20,20,60,"Flowers",13,0)
        addIcon(R.drawable.ic_large_box,"Large box","Paintings, chairs, TVs, flat-pack furniture",25.toDouble(),60,35,70,"Large",14,0)
        addIcon(R.drawable.ic_large_items,"Large items","Engines, Cars, Large shipping containers, Whole pallets, Plants, Large boxes",0.0,0,0,0,"XL",25,0)
        addIcon(R.drawable.ic_general_van_shipments,"General Van Shipments","Posters, Plants, Large Boxes",0.0,0,0,0,"XL",15,0)
        addIcon(R.drawable.ic_furniture,"Furniture","A chair or fridge - to fit in a van",0.0,0,0,0,"XL",16,0)
        addIcon(R.drawable.ic_building_materials,"Building Materials","Tall Timber, Framing, Concrete, Plumbing",0.0,0,0,0,"XL",18,0)
        addIcon(R.drawable.ic_general_truck_shipments,"General Truck Shipments","Misc goods too big for a van",0.0,0,0,0,"XL",19,0)
        addIcon(R.drawable.ic_pallets,"Pallets","Whole pallets of contained goods",0.0,0,0,0,"XL",20,0)
        addIcon(R.drawable.ic_machinery,"Machinery","Engines, Bobcats, Mixers, Excavators",0.0,0,0,0,"XL",21,0)
        addIcon(R.drawable.ic_vehicles,"Vehicles","Motorbikes, Scooters, Cars, Utes, Vans, Trucks, Boats Caravans",0.0,0,0,0,"XL",22,0)
        addIcon(R.drawable.ic_container,"Container","Large shipping containers",0.0,0,0,0,"XL",23,0)
        addIcon(R.drawable.ic_full_truck_load,"Full Truck Load","A full semi-trailer of goods",0.0,0,0,0,"XL",24,0)
        addIcon(R.drawable.ic_full_truck_load,"Full Truck Load","A full semi-trailer of goods",0.0,0,0,0,"Blank",29,0)

    }
}
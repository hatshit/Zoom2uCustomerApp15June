package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.databinding.ItemHeavyFreightItemBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.suggest_price.Item
import java.text.DecimalFormat
import java.util.ArrayList

class HeavyFreightItemAdapter(
    val context: Context,
    private val dataList: ArrayList<Item>
) :
    RecyclerView.Adapter<HeavyFreightItemAdapter.BindingViewHolder>() {


    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getData(): ArrayList<Item> {
        return dataList
    }

    fun addItemBtn() {
        val item = Item("", -2, -2, -2, -2, -2.0, "", "")
        dataList.add(item)
        notifyDataSetChanged()
    }

    private fun addItem(item: Item) {
        val item1 = Item(
            item.Packaging,
            item.Quantity,
            item.LengthCm,
            item.HeightCm,
            item.WidthCm,
            item.ItemWeightKg,
            item.TotalWeightKg,
            item.ContainerSize
        )
        dataList.add(item1)
        notifyDataSetChanged()
    }

    private fun removeItem() {
        dataList.removeAt(dataList.size - 1)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemHeavyFreightItemBinding =
            ItemHeavyFreightItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        if (position != 0) holder.itemBinding.removeItem.visibility = View.VISIBLE

        holder.itemBinding.addItem.setOnClickListener {
            if (dataList.size > 0)
                addItem(dataList[position])
        }

        holder.itemBinding.removeItem.setOnClickListener {
            if (dataList.size > 1)
                removeItem()
        }


        val icon: Item = dataList[position]
        holder.itemBinding.icon = icon
        when (icon.Quantity) {
            -1 -> holder.itemBinding.quantityTxt.setTextColor(Color.RED)
            -2 ->
                holder.itemBinding.quantityTxt.setTextColor(Color.BLACK)
            else -> {
                holder.itemBinding.quantityTxt.setTextColor(Color.BLACK)
                holder.itemBinding.quantity.setText(icon.Quantity.toString())
            }
        }
        when (icon.LengthCm) {
            -1 -> holder.itemBinding.lengthTxt.setTextColor(Color.RED)
            -2 ->
                holder.itemBinding.lengthTxt.setTextColor(Color.BLACK)
            else -> {
                holder.itemBinding.lengthTxt.setTextColor(Color.BLACK)
                holder.itemBinding.length.setText(icon.LengthCm.toString())
            }
        }
        when (icon.WidthCm) {
            -1 -> holder.itemBinding.widthTxt.setTextColor(Color.RED)
            -2 ->
                holder.itemBinding.widthTxt.setTextColor(Color.BLACK)
            else -> {
                holder.itemBinding.widthTxt.setTextColor(Color.BLACK)
                holder.itemBinding.width.setText(icon.WidthCm.toString())
            }
        }
        when (icon.HeightCm) {
            -1 -> holder.itemBinding.heightTxt.setTextColor(Color.RED)
            -2 ->
                holder.itemBinding.heightTxt.setTextColor(Color.BLACK)
            else -> {
                holder.itemBinding.heightTxt.setTextColor(Color.BLACK)
                holder.itemBinding.height.setText(icon.HeightCm.toString())

            }
        }
        when (icon.ItemWeightKg) {
            -1.0 -> holder.itemBinding.itemWeightTxt.setTextColor(Color.RED)
            -2.0 ->
                holder.itemBinding.itemWeightTxt.setTextColor(Color.BLACK)
            else -> {
                holder.itemBinding.itemWeightTxt.setTextColor(Color.BLACK)
                holder.itemBinding.itemWeight.setText(icon.ItemWeightKg.toString())
            }

        }
        when (icon.TotalWeightKg) {
            "-2" -> holder.itemBinding.totalWeightTxt.setTextColor(Color.BLACK)
            "0" -> holder.itemBinding.totalWeightTxt.setTextColor(Color.RED)
            else -> {
                holder.itemBinding.totalWeightTxt.setTextColor(Color.BLACK)
                holder.itemBinding.totalWeight.text = icon.TotalWeightKg.toString()+ " " + "kg"
            }
        }


        holder.itemBinding.quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "" && s.toString() != "0") {
                    icon.Quantity = s.toString().toInt()
                    holder.itemBinding.quantityTxt.setTextColor(Color.BLACK)
                    holder.itemBinding.totalWeightTxt.setTextColor(Color.BLACK)
                    if (holder.itemBinding.itemWeight.text.toString() != "")
                        getTotalWeight(
                            icon,
                            holder.itemBinding,
                            s.toString().toInt(),
                            holder.itemBinding.itemWeight.text.toString().toDouble()
                        )
                    else
                        getTotalWeight(icon, holder.itemBinding, s.toString().toInt(), 0.0)
                } else if (s.toString() == "0") {
                    icon.Quantity = -1
                    holder.itemBinding.quantityTxt.setTextColor(Color.RED)
                    icon.TotalWeightKg = "0"
                } else if (s.toString() == "") {
                    icon.Quantity = -1
                    holder.itemBinding.totalWeight.text = "0.0" + " " + "Kg"
                    holder.itemBinding.quantityTxt.setTextColor(Color.RED)
                    icon.TotalWeightKg = "0"
                }

            }
        })
        holder.itemBinding.itemWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "" && s.toString() != ".") {
                    icon.ItemWeightKg = s.toString().toDouble()
                    holder.itemBinding.itemWeightTxt.setTextColor(Color.BLACK)
                    holder.itemBinding.totalWeightTxt.setTextColor(Color.BLACK)
                    if (holder.itemBinding.quantity.text.toString() != "")
                        getTotalWeight(
                            icon,
                            holder.itemBinding,
                            holder.itemBinding.quantity.text.toString().toInt(),
                            s.toString().toDouble()
                        )
                    else
                        getTotalWeight(icon, holder.itemBinding, 0, s.toString().toDouble())
                } else if (s.toString() == "" || s.toString() == ".") {
                    icon.TotalWeightKg = "0"
                    icon.ItemWeightKg = -1.0
                    holder.itemBinding.totalWeight.text = "0.0" + " " + "Kg"
                    holder.itemBinding.itemWeightTxt.setTextColor(Color.RED)
                }
            }
        })
        holder.itemBinding.length.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.toString() != "0") {
                    dataList[position].LengthCm = s.toString().toInt()
                    holder.itemBinding.lengthTxt.setTextColor(Color.BLACK)
                } else if (s.toString() == "0") {
                    icon.LengthCm = 0
                    holder.itemBinding.lengthTxt.setTextColor(Color.RED)
                } else if (s.toString() == "") {
                    icon.LengthCm = -1
                    holder.itemBinding.lengthTxt.setTextColor(Color.RED)
                }
            }
        })
        holder.itemBinding.height.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.toString() != "0") {
                    icon.HeightCm = s.toString().toInt()
                    holder.itemBinding.heightTxt.setTextColor(Color.BLACK)
                } else if (s.toString() == "0") {
                    icon.HeightCm = 0
                    holder.itemBinding.heightTxt.setTextColor(Color.RED)
                } else if (s.toString() == "") {
                    icon.HeightCm = -1
                    holder.itemBinding.heightTxt.setTextColor(Color.RED)
                }
            }
        })
        holder.itemBinding.width.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.toString() != "0") {
                    icon.WidthCm = s.toString().toInt()
                    holder.itemBinding.widthTxt.setTextColor(Color.BLACK)
                } else if (s.toString() == "0") {
                    icon.WidthCm = 0
                    holder.itemBinding.widthTxt.setTextColor(Color.RED)
                } else if (s.toString() == "") {
                    icon.WidthCm = -1
                    holder.itemBinding.widthTxt.setTextColor(Color.RED)
                }
            }
        })

    }


    class BindingViewHolder(val itemBinding: ItemHeavyFreightItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    override fun getItemViewType(position: Int): Int = position


    fun getTotalWeight(
        icon: Item,
        holder: ItemHeavyFreightItemBinding,
        quantity: Int?,
        weight: Double?,
    ) {
        var totalWight = 0.0
        if (quantity != null && weight != null) {
            val df = DecimalFormat("#.###")
            totalWight = quantity * weight
            holder.totalWeight.text = df.format(totalWight).toString() + " " + "kg"
            icon.TotalWeightKg = totalWight.toString()
        }
    }


}
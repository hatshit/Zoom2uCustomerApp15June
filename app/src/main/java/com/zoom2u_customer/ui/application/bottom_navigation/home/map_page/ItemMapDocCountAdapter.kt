package com.zoom2u_customer.ui.application.bottom_navigation.home.map_page

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.databinding.ItemMapDoc1Binding
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import java.text.DecimalFormat

class ItemMapDocCountAdapter(
    val context: Context,
    private val dataList: MutableList<Icon>,
    private val onTotalWeight: (String,Icon) -> Unit,
    ) : RecyclerView.Adapter<ItemMapDocCountAdapter.BindingViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemMapDoc1Binding =
            ItemMapDoc1Binding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        if (position == itemCount - 1)
            holder.itemBinding.blankView.visibility = View.VISIBLE
        else
            holder.itemBinding.blankView.visibility = View.GONE

        val icon: Icon = dataList[position]
        holder.itemBinding.icon = icon
        if (icon.quantity > 0) {
            holder.itemBinding.doc.visibility = View.VISIBLE
            holder.itemBinding.quantity.setText(icon.quantity.toString())
            holder.itemBinding.itemWeight.setText(icon.weight.toString())
            holder.itemBinding.length.setText(icon.length.toString())
            holder.itemBinding.width.setText(icon.width.toString())
            holder.itemBinding.height.setText(icon.height.toString())
            val df = DecimalFormat("#.###")
            val totalWight = icon.quantity * icon.weight
            holder.itemBinding.totalWeight.text = df.format(totalWight).toString() + " " + "Kg"

        } else {
            holder.itemBinding.doc.visibility = View.GONE
            dataList.removeAt(position)
        }
        holder.itemBinding.root.setOnClickListener {
            if (!holder.itemBinding.ll1.isVisible) {
                holder.itemBinding.ll1.visibility = View.VISIBLE
                holder.itemBinding.forward.animate().rotation(180F).start()
            } else {
                holder.itemBinding.ll1.visibility = View.GONE
                holder.itemBinding.forward.animate().rotation(-0F).start()
            }
        }


        holder.itemBinding.quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "" && s.toString() !="0" && holder.itemBinding.itemWeight.text.toString() != "") {
                    icon.quantity = s.toString().toInt()
                    holder.itemBinding.count.text=s.toString()+"x"
                    holder.itemBinding.quantityTxt.setTextColor(Color.BLACK)
                    showRedErrorInHeader(icon,holder.itemBinding)
                    getTotalWeight(
                        icon, holder.itemBinding,
                        s.toString().toInt(),
                        holder.itemBinding.itemWeight.text.toString().toDouble(),
                        )

                }else if(s.toString() == "0"){
                    icon.quantity= 0
                    holder.itemBinding.count.text="0x"
                }
                else if (s.toString() == "") {
                    icon.quantity = -1
                    holder.itemBinding.count.text="x"
                    holder.itemBinding.totalWeight.text = "0.0" + " " + "Kg"
                    holder.itemBinding.quantityTxt.setTextColor(Color.RED)
                    showRedErrorInHeader(icon,holder.itemBinding)

                }

            }
        })

        holder.itemBinding.itemWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "" && s.toString() !="." && holder.itemBinding.quantity.text.toString() != "") {
                    icon.weight = s.toString().toDouble()
                    holder.itemBinding.itemWeightTxt.setTextColor(Color.BLACK)
                    showRedErrorInHeader(icon,holder.itemBinding)
                    getTotalWeight(
                        icon,
                        holder.itemBinding,
                        holder.itemBinding.quantity.text.toString().toInt(),
                        s.toString().toDouble()
                        )


                } else if (s.toString() == "" || s.toString() =="." ) {
                    icon.weight= -1.0
                    holder.itemBinding.totalWeight.text = "0.0" + " " + "Kg"
                    showRedErrorInHeader(icon,holder.itemBinding)
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
                if (!s.isNullOrEmpty() && s.toString() !="0") {
                    val length = s.toString().toInt()
                    holder.itemBinding.lengthTxt.setTextColor(Color.BLACK)
                    icon.length = length
                    showRedErrorInHeader(icon,holder.itemBinding)
                    getTotalWeight(
                        icon, holder.itemBinding, icon.quantity, icon.weight)

                }else if(s.toString() == "0"){
                    icon.length= 0
                    showRedErrorInHeader(icon,holder.itemBinding)
                    holder.itemBinding.lengthTxt.setTextColor(Color.RED)
                }
                else if (s.toString() == "") {
                    icon.length=-1
                    showRedErrorInHeader(icon,holder.itemBinding)
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
                if (!s.isNullOrEmpty() && s.toString() !="0") {
                    val height = s.toString().toInt()
                    holder.itemBinding.heightTxt.setTextColor(Color.BLACK)
                    icon.height =height
                    getTotalWeight(icon, holder.itemBinding, icon.quantity, icon.weight)
                    showRedErrorInHeader(icon,holder.itemBinding)
                }else if(s.toString() == "0"){
                    icon.height= 0
                    showRedErrorInHeader(icon,holder.itemBinding)
                    holder.itemBinding.heightTxt.setTextColor(Color.RED)
                }
                else if (s.toString() == "") {
                    icon.height =-1
                    showRedErrorInHeader(icon,holder.itemBinding)
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
                if (!s.isNullOrEmpty() && s.toString() !="0") {
                    val width = s.toString().toInt()
                    holder.itemBinding.widthTxt.setTextColor(Color.BLACK)
                    icon.width = width
                    showRedErrorInHeader(icon,holder.itemBinding)
                    getTotalWeight(
                        icon, holder.itemBinding, icon.quantity, icon.weight)
                }else if(s.toString() == "0"){
                    icon.width= 0
                    showRedErrorInHeader(icon,holder.itemBinding)
                    holder.itemBinding.widthTxt.setTextColor(Color.RED)
                }else if (s.toString() == "") {
                    icon.width = -1
                    showRedErrorInHeader(icon,holder.itemBinding)
                    holder.itemBinding.widthTxt.setTextColor(Color.RED)

                }
            }
        })

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun getTotalWeight(
        icon: Icon,
        holder: ItemMapDoc1Binding,
        quantity: Int?,
        weight: Double?,
        )
    {
        var totalWight = 0.0
        if (quantity != null && weight != null) {
            val df = DecimalFormat("#.###")
            totalWight = quantity * weight
            holder.totalWeight.text = df.format(totalWight).toString() + " " + "Kg"
            val totalWightSum = countTotalWeight()
            onTotalWeight(totalWightSum,icon)
        }
    }


    private fun countTotalWeight(): String {
        var totalWeight = 0.0
        for (item in dataList) {
            totalWeight += item.weight * item.quantity
        }
        return totalWeight.toString()
    }


  fun showRedErrorInHeader(icon: Icon,holder: ItemMapDoc1Binding){
      if(icon.quantity==0 || icon.quantity==-1 || icon.weight==-1.0 || icon.length == -1
          || icon.height == -1 || icon.width == -1 ||icon.length==0 || icon.height ==0 ||
              icon.width ==0)
          holder.documentTxt.setTextColor(Color.RED)
      else
          holder.documentTxt.setTextColor(Color.BLACK)
  }


    class BindingViewHolder(val itemBinding: ItemMapDoc1Binding) :
        RecyclerView.ViewHolder(itemBinding.root)


}
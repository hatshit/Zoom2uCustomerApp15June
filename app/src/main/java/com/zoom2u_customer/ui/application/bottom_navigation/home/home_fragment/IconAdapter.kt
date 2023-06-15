package com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.IconItemBinding


class IconAdapter(
    private val context: Context, private var dataList: MutableList<Icon>,
    private val isItemClick: (MutableList<Icon>) -> Unit,
    private val isLargeItemClick: () -> Unit
) :
    RecyclerView.Adapter<IconAdapter.BindingViewHolder>() {
    private var isHeavyFreight: Boolean? = null
    private var isLargeItemClicked: Boolean = true
    override fun getItemCount(): Int {
        return if (isHeavyFreight == true)
            dataList.size
        else 6
    }

    fun isComeInHomePage(isLargeItemClicked: Boolean){
        this.isLargeItemClicked=isLargeItemClicked
    }


    fun isHeavyFreight(isHeavyFreight: Boolean) {
        this.isHeavyFreight = isHeavyFreight
        notifyDataSetChanged()
    }

    fun updateItem(icon: Icon?) {
        dataList.forEachIndexed { index, pod ->
            if (pod.text == icon?.text) {
                notifyItemChanged(index)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: IconItemBinding =
            IconItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val icon: Icon = dataList[position]
        holder.itemBinding.icon = icon
        if (icon.quantity == 0)
            unhighlightView(holder)
        else
            highlightView(holder)


        if (icon.Value == 10 || icon.Value == 11)
            holder.itemBinding.topBlankView.visibility = View.GONE
        else
            holder.itemBinding.topBlankView.visibility = View.VISIBLE



        holder.itemBinding.layoutDocLl.setOnClickListener {
            if (dataList[position].Category != "XL") {
                isLargeItemClicked = true
                if (dataList[position].quantity == 0) {
                    dataList[position].quantity += 1
                    notifyItemChanged(position)
                    isItemClick(dataList)
                    if (dataList[position].quantity == 1) {
                        isHeavyFreight(false)
                        makeSelect_UnSelect()
                    }
                } else if (dataList[position].quantity == 1) {
                    dataList[position].quantity -= 1
                    notifyItemChanged(position)
                    isItemClick(dataList)
                }
            } else
                heavyFreightCaseMange(position)

        }


        holder.itemBinding.selectBtn.setOnClickListener {
            heavyFreightCaseMange(position)
        }



        if (dataList[position].Category == "XL") {
            if (dataList[position].Value == 25) {
                holder.itemBinding.loadMoreIcon.visibility = View.VISIBLE

            } else {
                holder.itemBinding.loadMoreIcon.visibility = View.GONE
            }

            if (dataList[position].Value == 24 || position == 5)
                holder.itemBinding.blankView.visibility = View.VISIBLE
            else
                holder.itemBinding.blankView.visibility = View.GONE



            holder.itemBinding.plusMinus.visibility = View.GONE
            holder.itemBinding.selectBtn.visibility = View.VISIBLE
            if (dataList[position].isSelected) {
                holder.itemBinding.selectBtn.setBackgroundResource(R.drawable.selected_background)
                holder.itemBinding.selectBtn.text = "Selected"
                highlightView(holder)
            } else {
                holder.itemBinding.selectBtn.setBackgroundResource(R.drawable.chip_background)
                holder.itemBinding.selectBtn.text = "Select"
                unhighlightView(holder)
            }
        } else if (dataList[position].Category == "Blank") {
            holder.itemBinding.loadMoreIcon.visibility = View.GONE
            holder.itemBinding.layoutDocLl.visibility = View.GONE
            holder.itemBinding.plusMinus.visibility = View.GONE
            holder.itemBinding.text.visibility = View.GONE
        } else {
            holder.itemBinding.loadMoreIcon.visibility = View.GONE
            holder.itemBinding.plusMinus.visibility = View.VISIBLE
            holder.itemBinding.selectBtn.visibility = View.GONE
            holder.itemBinding.layoutDocLl.visibility = View.VISIBLE
            holder.itemBinding.plusMinus.visibility = View.VISIBLE
            holder.itemBinding.text.visibility = View.VISIBLE
        }
        holder.itemBinding.increment.setOnClickListener {
            isLargeItemClicked = true
            dataList[position].quantity += 1
            notifyItemChanged(position)
            isItemClick(dataList)
            if (dataList[position].quantity == 1) {
                isHeavyFreight(false)
                makeSelect_UnSelect()
            }
        }
        holder.itemBinding.decrement.setOnClickListener {
            if (dataList[position].quantity > 0)
                dataList[position].quantity -= 1
            notifyItemChanged(position)
            isItemClick(dataList)
        }


    }

    private fun heavyFreightCaseMange(position: Int) {
        if (dataList[position].Value == 25) {
            if (isLargeItemClicked) {
                isLargeItemClicked = false
                isHeavyFreight(true)
                dataList[position].isSelected = true
                makeCountZero()
                isItemClick(dataList)
                isLargeItemClick()
            } else {
                isLargeItemClicked = true
                isLargeItemClick()
                makeSelect_UnSelect1()
                isItemClick(dataList)
            }
        } else {
            /**make current selected item and 15 no large item true and else false*/
            isLargeItemClicked = false
            dataList[position].isSelected = true
            for (item in dataList) {
                if (item.Value != 25 && item.Value != dataList[position].Value)
                    item.isSelected = false
            }
            isItemClick(dataList)
            notifyDataSetChanged()
        }
    }

    private fun makeSelect_UnSelect() {
        /**if large item selected then if we increse any normal booking count then make is unSelect */
        for (item in dataList) {
            item.isSelected = false
        }
        notifyDataSetChanged()
    }

    private fun makeSelect_UnSelect1() {
        /**if large item clicked and heavy item selected then again click on large item then unselect heavy item */
        for (item in dataList) {
            item.isSelected = item.Value == 25
        }
        notifyDataSetChanged()
    }

    private fun makeCountZero() {
        /**if any normal count greater then 0 and clicked on larger item make
         * count 0  */
        for (item in dataList) {
            item.quantity = 0
        }
        notifyDataSetChanged()
    }

    private fun highlightView(holder: BindingViewHolder) {
        holder.itemBinding.layoutDocLl.setBackgroundResource(R.drawable.image_background_onchnag)
    }

    private fun unhighlightView(holder: BindingViewHolder) {
        holder.itemBinding.layoutDocLl.setBackgroundResource(R.drawable.white_background)
    }

    class BindingViewHolder(val itemBinding: IconItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

}
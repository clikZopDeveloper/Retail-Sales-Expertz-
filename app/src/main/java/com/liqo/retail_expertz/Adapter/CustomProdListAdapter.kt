package com.liqo.retail_expertz.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson
import com.liqo.retail_expertz.Model.AddProductBean
import com.liqo.retail_expertz.R


class CustomProdListAdapter(
    var context: Activity,
    var list: MutableList<String>?,
    val catName:String,
) : RecyclerView.Adapter<CustomProdListAdapter.MyViewHolder>() {
    private val data = mutableListOf<Int>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dataproduct_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        /*     holder.tvAdd.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(20f))
             holder.tvQtyAdd.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(100f))
             holder.tvQtyMinus.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(100f))
             holder.tvQty.background = RoundView(Color.TRANSPARENT, RoundView.getRadius(20f), true, R.color.orange)
             holder.tvOff.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(20f))
             holder.tvAdd.visibility = View.VISIBLE*/

        holder.tvCategoryName.text = catName
        holder.tvPurchaseCatName.text = list!![position]
    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvCategoryName: TextView = itemview.findViewById(R.id.tvCategoryName)
        val tvPurchaseCatName: TextView = itemview.findViewById(R.id.tvPurchaseCatName)

    }

}
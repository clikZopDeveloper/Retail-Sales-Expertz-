package com.liqo.retail_expertz.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.liqo.retail_expertz.Activity.AddCustomerActivity
import com.liqo.retail_expertz.Model.AddProductBean
import com.liqo.retail_expertz.R


class CustomProdInterListAdapter(
    var context: Activity,
    var list: MutableList<AddProductBean>?,
    val catName:String,
) : RecyclerView.Adapter<CustomProdInterListAdapter.MyViewHolder>() {
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

   //     holder.tvCategoryName.text = catName
        holder.tvPurchaseCatName.text = list!![position].catName
        val item = list!![position].ID
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun removeItem(position: Int) {
        if (position in list!!.indices) {
            list!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvCategoryName: TextView = itemview.findViewById(R.id.tvCategoryName)
        val tvPurchaseCatName: TextView = itemview.findViewById(R.id.tvPurchaseCatName)
        val ivDelete: ImageView = itemview.findViewById(R.id.ivDelete)
        fun bind(item: Int) {
            ivDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position)
                }else{
                    (context as AddCustomerActivity).removeItemByNameInter(item)
                }
            }
        }
    }

}
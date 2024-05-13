package com.liqo.retail_expertz.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liqo.retail_expertz.Model.TelecalerDashboardBean
import com.liqo.retail_expertz.R
import com.liqo.retail_expertz.Utills.RvStatusClickListner


class TelecallerAdapter(var context: Activity, var list: List<TelecalerDashboardBean.Data.Dashboard>, var rvClickListner: RvStatusClickListner) : RecyclerView.Adapter<TelecallerAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard, parent, false)
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


        holder.tvTitle.text= list[position].status.toString()
       // holder.tvSubTitle.text= list[position].subTitle
        if (list[position].value!=null){
            holder.tvAmount.text= list[position].value.toString()
        }

    //    holder.ivImage.setImageDrawable(context.resources.getDrawable(list[position].drawableId))

      /*  if ("Retailer"=="Retailer"){
      //      holder.itemView.visibility=View.GONE
        }*/

        holder.itemView.setOnClickListener {


           rvClickListner.clickPos( list[position].status.uppercase(),list[position].statusId)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val ivImage: ImageView = itemview.findViewById(R.id.ivImage)
       val tvTitle: TextView = itemview.findViewById(R.id.tvTitle)
       val tvSubTitle: TextView = itemview.findViewById(R.id.tvSubTitle)
       val tvAmount: TextView = itemview.findViewById(R.id.tvAmount)
    }

}
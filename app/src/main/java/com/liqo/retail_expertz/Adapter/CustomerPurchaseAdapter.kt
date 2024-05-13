package com.liqo.retail_expertz.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.liqo.retail_expertz.R
import com.liqo.retail_expertz.Model.CustomerDetailBean
import com.liqo.retail_expertz.Utills.RvListClickListner

import com.google.gson.Gson


class CustomerPurchaseAdapter(var context: Activity,var way:String, var list: List<CustomerDetailBean.Data.CustomerPurchasedCategory>, var rvClickListner: RvListClickListner) : RecyclerView.Adapter<CustomerPurchaseAdapter.MyViewHolder>(){
    private val data = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_purcase, parent, false)
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


        holder.tvCatName.text= list[position].name
      //  holder.tvDate.text= list[position].careatedAt.toString()

       // holder.ivImage.setImageDrawable(context.resources.getDrawable(list[position].drawableId))

        if (list[position].purchasedStatus==1){
            holder.ivCheck.isEnabled=false
            holder.ivCheck.isChecked=true
        }else{
            holder.ivCheck.isEnabled=true
            holder.ivCheck.isChecked=false
        }

        if (way.equals("Detail")){
            holder.ivCheck.visibility=View.GONE
            if (list[position].purchasedStatus==1){
                holder.cardData.visibility=View.VISIBLE
            }else{
                holder.cardData.visibility=View.GONE
            }
        }else{
            holder.ivCheck.visibility=View.VISIBLE
        }

        holder.ivCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                data.add(list[position].id)

            } else {
                data.remove(list[position].id)

            }

            Log.d("zxczxc", Gson().toJson(data))
            rvClickListner.clickPos(data, list[position].id)
        }
        /*holder.itemView.setOnClickListener {
            rvClickListner.clickPos("",list[position].categoryId)
        }*/
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvCatName: TextView = itemview.findViewById(R.id.tvCatName)
        val tvDate: TextView = itemview.findViewById(R.id.tvDate)
        val ivCheck: CheckBox = itemview.findViewById(R.id.ivCheck)
        val cardData: CardView = itemview.findViewById(R.id.cardData)
    }

}
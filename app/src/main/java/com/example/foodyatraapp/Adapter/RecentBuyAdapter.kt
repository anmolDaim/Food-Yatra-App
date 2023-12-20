package com.example.foodyatraapp.Adapter

import android.net.Uri
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodyatraapp.dataModel.OrderDetails
import com.example.foodyatraapp.databinding.RecentBuyItemBinding


class RecentBuyAdapter(private var context: Context,
                       private var foodNameList:ArrayList<String>,
                       private var foodImageList:ArrayList<String>,
                       private var foodPriceList:ArrayList<String>,
                       private var foodQuantityList:ArrayList<Int>
    ): RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding= RecentBuyItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int =foodNameList.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
holder.bind(position)    }

//    fun updateData(orderDetails: java.util.ArrayList<OrderDetails>) {
//        // Clear the existing data
//        foodNameList.clear()
//        foodImageList.clear()
//        foodPriceList.clear()
//        foodQuantityList.clear()
//
//        // Add data from the new list
//        for (orderDetail in orderDetails) {
//            // Add relevant data from OrderDetails to adapter lists
//            foodNameList.addAll(orderDetail.foodNames ?: arrayListOf())
//            foodImageList.addAll(orderDetail.foodImages ?: arrayListOf())
//            foodPriceList.addAll(orderDetail.foodPrices ?: arrayListOf())
//            foodQuantityList.addAll(orderDetail.foodQuantities ?: arrayListOf())
//        }
//        notifyDataSetChanged() // Notify adapter after updating the data
//    }

    inner class RecentViewHolder (private val binding:RecentBuyItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                foodName.text=foodNameList[position]
                foodPrice.text = foodPriceList[position]
                foodQuantity.text = foodQuantityList [position].toString()
                val uriString = foodImageList [position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(foodImage)
            }
        }

    }

}
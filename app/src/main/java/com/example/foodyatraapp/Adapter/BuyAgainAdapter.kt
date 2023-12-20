package com.example.foodyatraapp.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodyatraapp.databinding.BuyAgaimItemBinding

class BuyAgainAdapter(private val buyingAgainFoodName:MutableList<String>,
    private val buyAgainFoodPrice:MutableList<String>,
    private val buyAgainFoodImage:MutableList<String>,
    private val requireContext:Context) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding =BuyAgaimItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyingAgainFoodName[position],buyAgainFoodPrice[position],buyAgainFoodImage[position])
    }

    override fun getItemCount(): Int =buyingAgainFoodName.size

    inner class BuyAgainViewHolder (private val binding: BuyAgaimItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.buyAgainFoodName.text=foodName
            binding.buyAgainFoodPrice.text=foodPrice
           val uriString =foodImage
            val uri= Uri.parse(uriString)
            Glide.with(requireContext).load(uri).into(binding.buyAgainFoodImage)
        }
    }
}
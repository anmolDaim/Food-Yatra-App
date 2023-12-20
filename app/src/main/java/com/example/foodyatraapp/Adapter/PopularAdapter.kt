package com.example.foodyatraapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodyatraapp.activity.DetailsActivity
import com.example.foodyatraapp.databinding.PopularItemBinding

class PopularAdapter(private val items : List<String>,private val price :List<String>,private val image : List<Int>,private val requiredContext: Context): RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item=items[position]
        val price =price[position]
        val images =image[position]
        holder.bind(item,images,price)

        holder.itemView.setOnClickListener{
            val intent= Intent(requiredContext, DetailsActivity::class.java)
            intent.putExtra("menuItemName",item)
            intent.putExtra("MenuImage",images)
            requiredContext.startActivity(intent)
        }
    }

    class PopularViewHolder (private val binding: PopularItemBinding): RecyclerView.ViewHolder(binding.root){
        private val imagesView=binding.imageView5
        fun bind(item: String, images: Int,price: String) {
            binding.FoodNamePopular.text=item
            binding.pricePopular.text=price
            imagesView.setImageResource(images)

        }

    }

}
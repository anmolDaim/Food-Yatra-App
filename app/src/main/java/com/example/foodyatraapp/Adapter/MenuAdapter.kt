package com.example.foodyatraapp.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodyatraapp.activity.DetailsActivity
import com.example.foodyatraapp.dataModel.MenuItem
import com.example.foodyatraapp.databinding.MenuItemBinding

class MenuAdapter(private val menuItems: List<MenuItem>,
                  private val requiredContext: Context)
    : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)

    }

    override fun getItemCount(): Int =menuItems.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
       holder.bind(position)
    }

    inner class MenuViewHolder(private val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        init{
            binding.root.setOnClickListener{
                val position=adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    openDetailActivity(position)
                }
            }
        }

        private fun openDetailActivity(position: Int) {
            val menuItem=menuItems[position]

            //intent to open detail activity and pass data
            val intent = Intent(requiredContext,DetailsActivity::class.java).apply {
               putExtra("MenuItemName",menuItem.foodName)
                putExtra("MenuItemImage",menuItem.foodImage)
                putExtra("MenuItemDescription",menuItem.foodDescription)
                putExtra("MenuItemIngrediants",menuItem.foodIngrediant)
                putExtra("MenuItemPrice",menuItem.foodPrice)

            }
            //start the detail activity
            requiredContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem=menuItems[position]
            binding.apply {
                menuFoodName.text=menuItem.foodName
                menuPrice.text=menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requiredContext).load(uri).into(menuImage)
            }
        }


    }
}




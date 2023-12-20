package com.example.foodyatraapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodyatraapp.Adapter.RecentBuyAdapter
import com.example.foodyatraapp.R
import com.example.foodyatraapp.dataModel.OrderDetails
import com.example.foodyatraapp.databinding.ActivityRecentOrderItemsBinding
import com.example.foodyatraapp.databinding.ActivitySignUpBinding

class RecentOrderItems : AppCompatActivity() {
    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodQuantities: ArrayList<Int>
    private lateinit var recentOrderAdapter: RecentBuyAdapter
    private val binding: ActivityRecentOrderItemsBinding by lazy{
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrderItems") as ArrayList<OrderDetails>
        recentOrderItems ?.let{
            orderDetails->
            if(orderDetails.isNotEmpty()){
                val recentOrderItem=orderDetails[0]
                allFoodNames=recentOrderItem.foodNames as ArrayList<String>
                allFoodPrices=recentOrderItem.foodPrices as ArrayList<String>
                allFoodQuantities=recentOrderItem.foodQuantities as ArrayList<Int>
                allFoodImages=recentOrderItem.foodImages as ArrayList<String>
            }
            setAdapter(allFoodNames, allFoodImages, allFoodPrices, allFoodQuantities)
        }


        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter( foodNames: ArrayList<String>,
                            foodImages: ArrayList<String>,
                            foodPrices: ArrayList<String>,
                            foodQuantities: ArrayList<Int>) {
        val rv = binding.recyclerViewRecentBuy
        rv.layoutManager = LinearLayoutManager(this)
        recentOrderAdapter = RecentBuyAdapter(this, foodNames, foodImages, foodPrices, foodQuantities)
        rv.adapter = recentOrderAdapter
    }
}
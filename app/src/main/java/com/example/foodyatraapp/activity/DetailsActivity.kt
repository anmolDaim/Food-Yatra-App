package com.example.foodyatraapp.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.foodyatraapp.R
import com.example.foodyatraapp.dataModel.CartItems
import com.example.foodyatraapp.databinding.ActivityDetailsBinding
import com.example.foodyatraapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var foodName:String?=null
    private var foodImage:String?=null
    private var foodDescription:String?=null
    private var foodIngrediant:String?=null
    private var foodPrice:String?=null
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        foodName=intent.getStringExtra("MenuItemName")
        foodPrice=intent.getStringExtra("MenuItemPrice")
        foodDescription=intent.getStringExtra("MenuItemDescription")
        foodImage=intent.getStringExtra("MenuItemImage")
        foodIngrediant=intent.getStringExtra("MenuItemIngrediants")

        with(binding){
            detailFoodName.text=foodName
            detaiilDescripton.text=foodDescription
            detailIngrediant.text=foodIngrediant
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailedFoodImage)
        }
        binding.imageButton.setOnClickListener{
            finish()
        }
        binding.addToCartButton.setOnClickListener{
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database=FirebaseDatabase.getInstance().reference
        val userId=auth.currentUser?.uid?:""

        //create a cart item object
val cartItem= CartItems(foodName.toString(),
    foodPrice.toString(),
    foodDescription.toString(),
    foodImage.toString(),1)
        //save data to cart item to firebase
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Items aded into cart successfully😁", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Item not added 😭", Toast.LENGTH_SHORT).show()
        }
    }
}
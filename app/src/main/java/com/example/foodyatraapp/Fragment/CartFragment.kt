package com.example.foodyatraapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import com.example.foodyatraapp.Adapter.cartAdapter
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.example.foodyatraapp.activity.PayOutActivity
import com.example.foodyatraapp.dataModel.CartItems
import com.example.foodyatraapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartFragment : Fragment() {
    private lateinit var binding : FragmentCartBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var foodNames:MutableList<String>
    private lateinit var foodPrice:MutableList<String>
    private lateinit var foodDescription:MutableList<String>
    private lateinit var foodImagesUri:MutableList<String>
    private lateinit var foodIngrediant:MutableList<String>
    private lateinit var quantity:MutableList<Int>
    private lateinit var CartAdapter: cartAdapter
    private lateinit var userId:String
    private lateinit var databaseReference:DatabaseReference
    private var isProcessingOrder = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)


        auth=FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        retriveCartItems()

        binding.processButton.setOnClickListener{
            if (!isProcessingOrder) {
                isProcessingOrder = true
                getOrderItemDetails()
            }
//            val Intent= Intent(requireContext(),PayOutActivity::class.java)
//            startActivity(Intent)
        }

        return binding.root
    }

    private fun getOrderItemDetails() {
        val orderIdReference:DatabaseReference=database.reference.child("user").child(userId).child("CartItems")
        val foodName= mutableListOf<String>()
        val foodPrice= mutableListOf<String>()
        val foodImage= mutableListOf<String>()
        val foodDescripton= mutableListOf<String>()
        val foodIngrediants= mutableListOf<String>()
        val foodQuantities=CartAdapter.getUpdatedItemQuantities()

        orderIdReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapShot in snapshot.children){
                    val orderItems=foodSnapShot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescripton.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngrediant?.let { foodIngrediants.add(it) }
                }
                orderNow(foodName,foodPrice,foodDescripton,foodImage,foodIngrediants,foodQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "order making failed please try again", Toast.LENGTH_SHORT).show()
            }

        })
        isProcessingOrder = false
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescripton: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngrediants: MutableList<String>,
        foodQuantity: MutableList<Int>
    ) {
        if (isAdded && context!=null){
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDescripton as ArrayList<String>)
            intent.putExtra("FoodItemIngrediant", foodIngrediants as ArrayList<String>)
            intent.putExtra("FoodItemQuantity", foodQuantity as ArrayList<Int>)
            startActivity(intent)

        }
        
    }

    private fun retriveCartItems() {
        database= FirebaseDatabase.getInstance()
        userId=auth.currentUser?.uid?:""
        val foodReference:DatabaseReference=database.reference.child("user").child(userId).child("CartItems")
        //list to store cart itewms
        foodNames= mutableListOf()
        foodPrice= mutableListOf()
        foodDescription= mutableListOf()
        foodImagesUri= mutableListOf()
        foodIngrediant= mutableListOf()
        quantity= mutableListOf()

        //fetch data from the database
        foodReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val cartItems=foodSnapshot.getValue(CartItems::class.java)
                    cartItems?.foodName?.let{foodNames.add(it)}
                    cartItems?.foodPrice?.let{foodPrice.add(it)}
                    cartItems?.foodDescription?.let{foodDescription.add(it)}
                    cartItems?.foodImage?.let{foodImagesUri.add(it)}
                    cartItems?.foodQuantity?.let{quantity.add(it)}
                    cartItems?.foodIngrediant?.let{foodIngrediant.add(it)}
                }
                setAdapter()
            }

            private fun setAdapter() {
                CartAdapter = cartAdapter(requireContext(), foodNames, foodPrice, foodDescription, foodImagesUri, quantity, foodIngrediant)
                binding.cartRecyclerView.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                binding.cartRecyclerView.adapter=CartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not fetched", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
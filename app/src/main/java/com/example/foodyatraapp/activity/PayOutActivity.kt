package com.example.foodyatraapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.foodyatraapp.Fragment.CongratsBottomSheet
import com.example.foodyatraapp.dataModel.OrderDetails
import com.example.foodyatraapp.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    private lateinit var name:String
    private lateinit var address:String
    private lateinit var phone:String
    private lateinit var totalAmount:String
    private lateinit var foodItemName:ArrayList<String>
    private lateinit var foodItemPrice:ArrayList<String>
    private lateinit var foodItemImage:ArrayList<String>
    private lateinit var foodItemDescription:ArrayList<String>
    private lateinit var foodItemIngrediant:ArrayList<String>
    private lateinit var foodItemQuantity:ArrayList<Int>
    private lateinit var databaseReference:DatabaseReference
    private lateinit var auth:FirebaseAuth
    private lateinit var userId:String

    private lateinit var binding: ActivityPayOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            binding = ActivityPayOutBinding.inflate(layoutInflater)
            setContentView(binding.root)

            auth = FirebaseAuth.getInstance()
            databaseReference = FirebaseDatabase.getInstance().reference

            setUserData()

            // Get users details from Firebase
            val intent = intent
            foodItemName = intent.getStringArrayListExtra("FoodItemName") ?: arrayListOf()
            foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") ?: arrayListOf()
            foodItemImage = intent.getStringArrayListExtra("FoodItemImage") ?: arrayListOf()
            foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") ?: arrayListOf()
            foodItemIngrediant = intent.getStringArrayListExtra("FoodItemIngrediant") ?: arrayListOf()
            foodItemQuantity = intent.getIntegerArrayListExtra("FoodItemQuantity") ?: arrayListOf()

            // Log to check if the intent data is received correctly
            Log.d("IntentDataDebug", "FoodItemName: $foodItemName")
            Log.d("IntentDataDebug", "FoodItemPrice: $foodItemPrice")

            totalAmount = calculateTotalAmount().toString() + '$'
            Log.d("TotalAmountDebug", "Total Amount: $totalAmount")
            //binding.totalAmountTextView.isEnabled=false
            binding.totalAmountTextView.setText(totalAmount)

            binding.PlacemyOrder.setOnClickListener {
                name=binding.userName.toString().trim()
                address=binding.address.text.toString().trim()
                phone=binding.phone.toString().trim()
                Log.d("TotalAmountDebug", "Total Amount: $totalAmount")
                Log.d("TotalAmountDebug", "Total Amount: $totalAmount")
                Log.d("TotalAmountDebug", "Total Amount: $totalAmount")
                if(name.isBlank()&&address.isBlank()&&phone.isBlank()){
                    Toast.makeText(this, "Please fill All details", Toast.LENGTH_SHORT).show()
                }else{
                    placeOrder()

                }
            }
            binding.backButtonImage.setOnClickListener {
                finish()
            }
        }
    }

    private fun placeOrder() {
        userId=auth.currentUser?.uid?:""
        val time=System.currentTimeMillis()
        val itemPushKey=databaseReference.child("OrderDetails").push().key
        name = binding.userName.text.toString().trim() // Retrieve the name from EditText
        address = binding.address.text.toString().trim() // Retrieve the address from EditText
        phone = binding.phone.text.toString().trim() // Retrieve the phone from EditText

        if (name.isBlank() || address.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show()
            return
        }
        val orderDetails=OrderDetails(userId,name,foodItemName,foodItemPrice,foodItemImage,foodItemQuantity,address,totalAmount,phone,time,itemPushKey,false,false)
        val orderReference=databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails:OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }
    }

    private fun removeItemFromCart() {
        val cartItemReference=databaseReference.child("user").child(userId).child("CartItems")
        cartItemReference.removeValue()
    }

    private fun calculateTotalAmount(): Double {
            var totalAmount = 0.0
            for (i in 0 until foodItemPrice.size) {
                var price = foodItemPrice[i]
                val priceValue = price.removePrefix("$").toDouble()
                var quantity = foodItemQuantity[i]
                totalAmount += priceValue * quantity
            }
            return totalAmount
        }

        private fun setUserData() {
            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val userReference = databaseReference.child("user").child(userId)

                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val namees = snapshot.child("name").getValue(String::class.java) ?: ""
                            val addresses = snapshot.child("address").getValue(String::class.java) ?: ""
                            val phones = snapshot.child("phone").getValue(String::class.java) ?: ""
                            binding.apply {
                                userName.setText(namees)
                                address.setText(addresses)
                                phone.setText(phones)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

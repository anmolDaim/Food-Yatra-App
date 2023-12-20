package com.example.foodyatraapp.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodyatraapp.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class cartAdapter(private val context:Context,
                  private val CartItems:MutableList<String>,
                  private val CartItemPrices:MutableList<String>,
                  private var CartDescription:MutableList<String>,
                  private var CartImages:MutableList<String>,
                  private val CartQuantity:MutableList<Int>,
                  private val CartIngrediant:MutableList<String>
)
    : RecyclerView.Adapter<cartAdapter.cartViewholder>() {

        //instance Firebase
        private val auth=FirebaseAuth.getInstance()

    //initialize Firebase
    init{
        val database=FirebaseDatabase.getInstance()
        val userid=auth.currentUser?.uid?:""
        val cartItemNumber=CartItems.size
        val defaultQuantity = 1 // Default quantity for items

        itemQuantities= IntArray(cartItemNumber){1}
        cartItemReference=database.reference.child("user").child(userid).child("CartItems")

        // Assign itemQuantities to CartQuantity
        CartQuantity.clear()
        CartQuantity.addAll(itemQuantities.asList())
    }

    companion object{

        private var itemQuantities:IntArray= intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewholder {
        val binding =CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return cartViewholder(binding)
    }

    override fun getItemCount(): Int
         =CartItems.size

    override fun onBindViewHolder(holder: cartViewholder, position: Int) {
       holder.bind(position)
    }

    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantity= mutableListOf<Int>()
        itemQuantity.addAll(CartQuantity)
        return itemQuantity
    }

    inner class cartViewholder(private val binding: CartItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity=itemQuantities[position]
                cartFoodName.text=CartItems[position]
                cartItemPrice.text=CartItemPrices[position]
                //loads image using glide
                val uriString = CartImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context)
                    .load(uri)
                    .into(cartImage)
                cartItemQuantity.text=quantity.toString()

                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusButton.setOnClickListener {
                    increaseQuantity(position)
                }
                deleteButton.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }
        private fun increaseQuantity(position:Int){
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                CartQuantity[position]= itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }
        private fun deleteItem(position:Int){
           val positionRetrive=position
            getUniqueKeyAtPosition(positionRetrive){
                uniqueKey->
                if(uniqueKey!=null){
                    removeItem(position,uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if(uniqueKey!=null){
                cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    CartItems.removeAt(position)
                    CartItemPrices.removeAt(position)
                    CartDescription.removeAt(position)
                    CartImages.removeAt(position)
                    CartQuantity.removeAt(position)
                    CartIngrediant.removeAt(position)
                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, CartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrive: Int,onComplete:(String?)->Unit) {
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey:String?=null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed{index,dataSnapshot->
                        if(index==positionRetrive){
                            uniqueKey=dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        private fun decreaseQuantity(position:Int){
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                CartQuantity[position]= itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

    }
}
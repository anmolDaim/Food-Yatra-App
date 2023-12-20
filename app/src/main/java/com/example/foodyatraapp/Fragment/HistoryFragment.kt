package com.example.foodyatraapp.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodyatraapp.Adapter.BuyAgainAdapter
import com.example.foodyatraapp.activity.RecentOrderItems
import com.example.foodyatraapp.dataModel.OrderDetails
import com.example.foodyatraapp.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable


class HistoryFragment : Fragment() {
    private lateinit var binding :FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var userId:String
    private var listOfOrderItems: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        //initaialize firebase auth
        auth=FirebaseAuth.getInstance()
        //init firebase database
        database = FirebaseDatabase.getInstance()
        //retrive and display the user order history
        retriveBuyHistory()

        //recentBuy button click
        binding.recentbuyitem.setOnClickListener{
            saveItemRecentBuy()
        }
        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }

        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey=listOfOrderItems[0].itemPushKey
        val completedOrderReference=database.reference.child("CompletedOrder").child(itemPushKey!!)
        completedOrderReference.child("paymentReceived").setValue(true)
            .addOnSuccessListener {
                Log.d("UpdateStatus", "Payment status updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("UpdateStatus", "Failed to update payment status: $e")
            }
    }

    private fun saveItemRecentBuy() {
        listOfOrderItems.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItems", ArrayList(listOfOrderItems))
            startActivity(intent)
        }
    }

    //fun to retrive itwm buy recently
    private fun retriveBuyHistory() {
binding.recentbuyitem.visibility=View.INVISIBLE
        userId=auth.currentUser?.uid?:""

        val buyItemReference:DatabaseReference=database.reference.child("user").child(userId).child("BuyHistory")
        buyItemReference.keepSynced(true)
        val shortingQuery=buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children){
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItems.add(it)
                    }
                }

                listOfOrderItems.reverse()

                if (listOfOrderItems.isNotEmpty()){
                    //display the most recent order details
                    setDataInRecentBuyItem()
                    //set up to recycler view with previous order details
                    setPreviousBuyRecyclerView()
                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setDataInRecentBuyItem() {
        binding.recentbuyitem.visibility=View.VISIBLE
        val recentOrderItem=listOfOrderItems.firstOrNull()
        recentOrderItem?.let{
            with(binding){
                buyFoodName.text=it.foodNames?.firstOrNull()?:""
                buyFoodPrice.text=it.foodPrices?.firstOrNull()?:""
                val image=it.foodImages?.firstOrNull()?:""
                val uri= Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyFoodImage)

                val isOrderIsAccepted=listOfOrderItems[0].orderAccepted
                if(isOrderIsAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility=View.VISIBLE
                }
                listOfOrderItems.reverse()
                if(listOfOrderItems.isNotEmpty()){

                }
            }
        }
    }

    private fun setPreviousBuyRecyclerView() {
        val buyAgainFoodName=mutableListOf<String>()
        val buyAgainFoodPrice=mutableListOf<String>()
        val buyAgainFoodImage= mutableListOf<String>()
        for (i in 1 until listOfOrderItems.size){
            listOfOrderItems[i].foodNames?.firstOrNull()?.let{buyAgainFoodName.add(it)}
            listOfOrderItems[i].foodPrices?.firstOrNull()?.let{buyAgainFoodPrice.add(it)}
            listOfOrderItems[i].foodImages?.firstOrNull()?.let{buyAgainFoodImage.add(it)}
        }
        val rv=binding.buyAgainRecyclerView
        rv.layoutManager=LinearLayoutManager(requireContext())
        buyAgainAdapter=
            BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice,buyAgainFoodImage,requireContext())
        rv.adapter=buyAgainAdapter
    }

}
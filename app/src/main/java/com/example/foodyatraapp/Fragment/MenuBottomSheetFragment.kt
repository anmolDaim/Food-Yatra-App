package com.example.foodyatraapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodyatraapp.Adapter.MenuAdapter
import com.example.foodyatraapp.dataModel.MenuItem
import com.example.foodyatraapp.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding :FragmentMenuBottomSheetBinding
    private lateinit var databse : FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMenuBottomSheetBinding.inflate(inflater,container,false)
        binding.buttonBack.setOnClickListener { dismiss() }

        reriveMenuItems()

        return binding.root
    }

    private fun reriveMenuItems() {
        databse=FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference=databse.reference.child("menu")
        menuItems= mutableListOf()
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let{
                        menuItems.add(it)
                    }
                    //once data received set adapter
                    setAdapter()
                }
            }

            private fun setAdapter() {
                val adapter= MenuAdapter(menuItems,requireContext())
                binding.menuRecyclerview.layoutManager=LinearLayoutManager(requireContext())
                binding.menuRecyclerview.adapter=adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {

    }
}
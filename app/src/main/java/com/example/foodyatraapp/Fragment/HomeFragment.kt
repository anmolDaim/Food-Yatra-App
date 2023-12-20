package com.example.foodyatraapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import com.example.foodyatraapp.dataModel.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodyatraapp.Adapter.MenuAdapter
import com.example.foodyatraapp.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        //retrive and display popular meu item
        retriveveAndDisplayPopularMenuItem()
        return binding.root
    }

    private fun retriveveAndDisplayPopularMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        //retrive menu item from the database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem =
                        foodSnapshot.getValue(com.example.foodyatraapp.dataModel.MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                    //once data received set adapter
                    randomPopularItem()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun randomPopularItem() {
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6;
        val subSetMenuItems = index.take(numItemToShow).map {
            menuItems[it]
        }
        setPOpularItemsAdapter(subSetMenuItems)
    }

    private fun setPOpularItemsAdapter(subSetMenuItems: List<MenuItem>) {
        val adapter = MenuAdapter(subSetMenuItems, requireContext())
        binding.popularRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.popularRecyclerView.adapter = adapter
    }

}



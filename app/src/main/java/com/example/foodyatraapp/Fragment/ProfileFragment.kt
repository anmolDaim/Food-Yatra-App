package com.example.foodyatraapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodyatraapp.R
import com.example.foodyatraapp.dataModel.userModel
import com.example.foodyatraapp.databinding.FragmentCartBinding
import com.example.foodyatraapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val auth= FirebaseAuth.getInstance()
    private val database= FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        seeUserData()

        binding.saveInfoButton.setOnClickListener{
            val name=binding.profileName.text.toString()
            val email=binding.profileEmail.text.toString()
            val phone=binding.profilePhone.text.toString()
            val address=binding.profileAddress.text.toString()

            updateUserId(name,email,address,phone)
        }
        return binding.root
    }

    private fun updateUserId(name: String, email: String, address: String, phone: String) {
        val userId=auth.currentUser?.uid
        if(userId!=null){
            val userReference=database.getReference("user").child(userId)
            val userData= hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(context, "Profile Updated successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context, "Profile updated failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun seeUserData() {
        val userId=auth.currentUser?.uid
        if(userId!=null){
            val userReference=database.getReference("user").child(userId)
            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userProfile=snapshot.getValue(userModel::class.java)
                        if (userProfile!=null){
                            binding.profileName.setText(userProfile.name)
                            binding.profileAddress.setText(userProfile.address)
                            binding.profilePhone.setText(userProfile.phone)
                            binding.profileEmail.setText(userProfile.email)
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
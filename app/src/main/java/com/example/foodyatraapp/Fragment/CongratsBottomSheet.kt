package com.example.foodyatraapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodyatraapp.R
import com.example.foodyatraapp.activity.MainActivity
import com.example.foodyatraapp.databinding.FragmentCartBinding
import com.example.foodyatraapp.databinding.FragmentCongratsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CongratsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentCongratsBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCongratsBottomSheetBinding.inflate(inflater,container,false)
        binding.goHome.setOnClickListener {
            val Intent= Intent(requireContext(),MainActivity::class.java)
            startActivity(Intent)
        }
        return binding.root
    }

    companion object {

    }
}
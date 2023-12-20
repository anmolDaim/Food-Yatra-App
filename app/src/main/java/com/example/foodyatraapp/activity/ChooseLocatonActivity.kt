package com.example.foodyatraapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.foodyatraapp.R
import com.example.foodyatraapp.databinding.ActivityChooseLocatonBinding
import com.example.foodyatraapp.databinding.ActivityLoginBinding

class ChooseLocatonActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocatonBinding by lazy{
        ActivityChooseLocatonBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList= arrayOf("jaipur","Panipat","Ambala","Indore","Mumbai")
        val adapter =ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

    }
}
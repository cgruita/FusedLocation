package com.gruita.fusedlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gruita.fusedlocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        binding.idTextView.setOnClickListener {
//            viewModel.applyRepeatBlur(blurLevel)
        }
    }
}
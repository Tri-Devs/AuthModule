package com.trishaft.fitwithus.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trishaft.fitwithus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /*
    * lazy components are lifecycle aware.
    * no need to manage the lifecycle for this variable.
    * */
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        instance = this
        appBinding = binding
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object{
        private lateinit var instance:Activity
        private lateinit var appBinding: ActivityMainBinding
        fun getInstance() = instance
        fun getBinding() = appBinding
    }
}
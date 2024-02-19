package com.trishaft.fitwithus.activities

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
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
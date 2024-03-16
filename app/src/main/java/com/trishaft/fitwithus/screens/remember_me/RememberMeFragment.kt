package com.trishaft.fitwithus.screens.remember_me

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.databinding.LayoutRememberMeBinding
import com.trishaft.fitwithus.utilities.PrefManager
import com.trishaft.fitwithus.utilities.navigate

class RememberMeFragment :Fragment(){

    private val binding:LayoutRememberMeBinding by lazy {
        LayoutRememberMeBinding.inflate(layoutInflater)
    }

    private val handler:Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private lateinit var prefManager: PrefManager
    private val savedProfileList:ArrayList<RememberMeModal> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        prefManager = PrefManager.getInstance(requireContext())
        savedProfileList.addAll(prefManager.getProfile())
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setViewListener()
    }

    private fun init() {
        binding.tvProfileName.text = PrefManager.getInstance(requireContext()).getProfile()[0].email
    }

    private fun setViewListener() {
        binding.apply {
            btnLoginAnother.setOnClickListener {  }
            btnMoreAcc.setOnClickListener { }
            ivSettings.setOnClickListener {  }
            btnLogin.setOnClickListener { navigate(R.id.action_rememberMeFragment_to_loginFragment) }
        }
    }
}
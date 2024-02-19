package com.trishaft.fitwithus.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trishaft.fitwithus.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        * Start all the necessary code here.
        * */
    }






    companion object {

        /*
        * make the variable updated in the parallel communication or in multithreading as well.
        * */
        @Volatile
        var instance : LoginFragment? = null


       /*
       * run this method synchronized so that unnecessary instance is not created.
       * As all function call will always occur in serial manner.
       * */
        @JvmStatic
        fun loginNewInstance() : LoginFragment {
            return instance ?: synchronized(this) {
                instance ?: LoginFragment().also { instance = it }
            }
        }
    }
}
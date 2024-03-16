package com.trishaft.fitwithus.screens.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.databinding.FragmentLoginBinding
import com.trishaft.fitwithus.databinding.FragmentSplashBinding
import com.trishaft.fitwithus.utilities.PrefManager
import com.trishaft.fitwithus.utilities.navigate


class SplashFragment : Fragment() {

    private val binding : FragmentSplashBinding by lazy{
        FragmentSplashBinding.inflate(layoutInflater)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = binding.splashImage

        val scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0.5f, 1f)
        val rotate = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f)
        val fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f)
        val translationY = ObjectAnimator.ofFloat(imageView, "translationY", -200f, 0f)

// Create a DecelerateInterpolator for a smoother start and end
        val decelerateInterpolator = DecelerateInterpolator()

// Apply the DecelerateInterpolator to the animations
        scaleX.interpolator = decelerateInterpolator
        scaleY.interpolator = decelerateInterpolator
        rotate.interpolator = decelerateInterpolator
        fadeIn.interpolator = decelerateInterpolator
        translationY.interpolator = decelerateInterpolator

// Create an AnimatorSet to play all animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, rotate, fadeIn, translationY)
        animatorSet.duration = 2000 // Animation duration in milliseconds
        animatorSet.start()

        Handler(Looper.getMainLooper()).postDelayed({
            if(PrefManager.getInstance(requireContext()).getProfile().isEmpty())
                navigate(R.id.action_splashFragment_to_loginFragment)
            else
                navigate(R.id.action_splashFragment_to_rememberMeFragment)
        }, 2100)
    }


}
package com.teblung.pokedex.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teblung.pokedex.databinding.ActivitySplashBinding
import com.teblung.pokedex.ui.home.MainActivity

class SplashActivity : AppCompatActivity() {

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            tvTitleSplash.text = "Pokedex"
            btnSplash.apply {
                text = "Start"
                setOnClickListener {
                    startActivity(MainActivity.intentMain(this@SplashActivity))
                    finish()
                }
            }
        }
    }
}
package com.keyinc.customview

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.keyinc.customview.customView.CustomView
import com.keyinc.customview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customView.increaseValue()

        binding.increaseButton.setOnClickListener {
            binding.customView.increaseValue()
        }

        binding.decreaseButton.setOnClickListener {
            binding.customView.decreaseValue()
        }
        var isOn = true
        binding.disable.setOnClickListener {
            isOn = if (isOn) {
                binding.customView.turnOff()
                false
            } else {
                binding.customView.turnOn()
                true
            }
        }
    }
}
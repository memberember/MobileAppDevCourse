package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.second_activity.*

class SecondActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)
        act2_button.setOnClickListener {
            val newStr = act2_edit.text.toString()
            val i = Intent()
            i.putExtra("tag2",newStr)
            setResult(0,i)

            finish()
        }
        val str = intent.getStringExtra("tag")
        act2_edit.setText(str)
    }

}
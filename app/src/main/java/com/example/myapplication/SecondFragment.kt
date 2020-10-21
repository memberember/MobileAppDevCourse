package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.sec_fragment.*
import kotlinx.android.synthetic.main.sec_fragment.view.*

class SecondFragment:Fragment() {

    lateinit var url:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = arguments!!.getString("url")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sec_fragment, container, false)

//view.frag2_browser

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        frag2_browser.loadUrl(url)
    }
}
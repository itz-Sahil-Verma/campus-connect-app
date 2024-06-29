package com.example.campusconnectfinal.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.databinding.ActivityCreateadBinding
import com.example.campusconnectfinal.databinding.FragmentHomeBinding
import com.example.campusconnectfinal.databinding.FragmentMarketBinding
import com.example.campusconnectfinal.homeactivities.Contacts
import com.example.campusconnectfinal.homeactivities.erp
import com.example.campusconnectfinal.homeactivities.libraray


class home : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(SlideModel(R.drawable.campus_mainimg))
        imageList.add(SlideModel(R.drawable.slideinmg2))
        imageList.add(SlideModel(R.drawable.communities))

        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)

        webViewSetup(binding.web,0)

        binding.contacticon.setOnClickListener {
            val intent = Intent(context, Contacts::class.java)
            startActivity(intent)
        }
//
        binding.erpicon.setOnClickListener {
            val intent = Intent(context, erp::class.java)
            startActivity(intent)
        }
//
        binding.libicon.setOnClickListener {
            val intent = Intent(context, libraray::class.java)
            startActivity(intent)
        }

        binding.canteenicon.setOnClickListener {
            Toast.makeText(context, "Canteen Section is Under Developement...", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun webViewSetup(webView: WebView, index: Int) {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByClassName('block block-block last even')[0].style.display='none'; " +
                        "})()");
                }
            }
        webView.loadUrl("https://www.jcboseust.ac.in/")
        }


}
package com.example.campusconnectfinal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.databinding.FragmentHomeBinding
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

        binding.web.settings.javaScriptEnabled = true

        var webView = binding.web

        webView.loadUrl("https://www.jcboseust.ac.in/") // Load the URL

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webView.loadUrl("javascript:(function() { " +
                        "var element = document.querySelector('#content > div.translate > div:nth-child(2)'); " +
                        "var parent = element.parentNode; " +
                        "while (parent !== document.body) { " +
                        "    parent.style.display = 'none'; " +
                        "    parent = parent.parentNode; " +
                        "} " +
                        "element.style.display = 'block'; " +
                        "})()")
            }
        }
//        binding.web.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                extractNoticesSection(view)
//            }
//        }


// Load the website's URL into the WebView


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

    fun extractNoticesSection(webView: WebView) {
        webView.evaluateJavascript(
            "(function(){ " +
                    "var noticesElement = document.querySelector(\"#content > div.translate > div:nth-child(2)\"); " +
                    "if (noticesElement !== null) { " +
                    "    var noticesHtml = noticesElement.innerHTML; " +
                    "    return '<html><body>' + noticesHtml + '</body></html>'; " +
                    "} else { " +
                    "    return '<html><body>No notices found.</body></html>'; " +
                    "} " +
                    "})()",
            { html ->
                val noticesHtml = html.toString()
                webView.loadDataWithBaseURL(null, noticesHtml, "text/html", "UTF-8", null)
            }
        )
    }


}
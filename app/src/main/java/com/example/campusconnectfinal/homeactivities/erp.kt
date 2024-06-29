package com.example.campusconnectfinal.homeactivities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.campusconnectfinal.R

class erp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_erp)

        val web = findViewById<WebView>(R.id.weberp)
        WebViewSetup(web)

    }

    private  fun WebViewSetup(a : WebView){
        a.webViewClient = WebViewClient()
        a.apply {
            settings.javaScriptEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
            loadUrl("https://jcboseustymca.co.in/")
        }
    }
}
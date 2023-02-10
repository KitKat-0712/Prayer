package com.example.prayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

class WebFragment(private val url: String) : Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AdBlocker.init(mainActivity)
        val webView = view.findViewById<WebView>(R.id.web_view)
        val webSettings = webView.settings

        webView.webViewClient = MyBrowser()
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        webView.loadUrl(url)

        view.findViewById<AppCompatButton>(R.id.leave).setOnClickListener {
            (activity as MainActivity).replaceNowFragmentWith(HomeFragment())
        }
        view.findViewById<AppCompatButton>(R.id.previous).setOnClickListener {
            if(webView.canGoBack()) {
                webView.goBack()
            }
        }
        view.findViewById<AppCompatButton>(R.id.reload).setOnClickListener {
            webView.reload()
        }
    }

    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        private val loadedUrls: MutableMap<String, Boolean> = HashMap()

        @Nullable
        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            val ad: Boolean
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url)
                loadedUrls[url] = ad
            } else {
                ad = loadedUrls[url]!!
            }
            return if (ad) AdBlocker.createEmptyResource() else super.shouldInterceptRequest(
                view,
                url
            )
        }
    }
}
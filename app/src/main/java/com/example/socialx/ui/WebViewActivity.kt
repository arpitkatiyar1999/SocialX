package com.example.socialx.ui

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.socialx.R
import com.example.socialx.databinding.ActivityWebViewBinding
import com.example.socialx.models.NetworkUtil
import com.google.android.material.snackbar.Snackbar


class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private var  url=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_005c996)
        binding=ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAll()
        checkNetwork()
    }

    private fun checkNetwork() {
        if(NetworkUtil().isConnected(applicationContext))
            binding.webView.loadUrl(url)
        else
                setSnackbar{checkNetwork()}
    }
    private fun setSnackbar(runnable: Runnable) {
        val snackbar: Snackbar =
            Snackbar.make(binding.root, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") { runnable.run() }
                .setActionTextColor(resources.getColor(R.color.white))
        snackbar.show()
    }

    private fun initAll() {
        url=intent.getStringExtra("url")!!
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
         binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
             url=intent.getStringExtra("url")!!
          binding.swipeRefreshLayout.isRefreshing=false
      })
    }
}
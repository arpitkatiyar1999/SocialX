package com.example.socialx.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.socialx.R
import com.example.socialx.adapters.ArticlesAdapter
import com.example.socialx.api.ApiClient
import com.example.socialx.api.ApiInterface
import com.example.socialx.databinding.ActivityHomeBinding
import com.example.socialx.models.Article
import com.example.socialx.models.NetworkUtil
import com.example.socialx.models.NewsResponseData
import com.example.socialx.ui.login.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    private var articleList:ArrayList<Article> =ArrayList()
    private var apiInterface: ApiInterface? = null
    private lateinit var articlesAdapter:ArticlesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAll()
        checkNetwork()
    }

    private fun initAll() {
        apiInterface = ApiClient.getApiClient()!!.create(ApiInterface::class.java)
        articlesAdapter= ArticlesAdapter(applicationContext,articleList)
        binding.recyclerView.adapter=articlesAdapter
    }
    private fun checkNetwork() {
       if(NetworkUtil().isConnected(applicationContext))
       getTopHeadlines()
        else
            setSnackbar { checkNetwork() }
    }

    private fun getTopHeadlines() {
        showProgressBar()
        apiInterface!!.getLatestHeadlines(getString(R.string.country),getString(R.string.api_key)).enqueue(object : Callback<NewsResponseData?> {
                override fun onResponse(call: Call<NewsResponseData?>, response: Response<NewsResponseData?>) {
                    if (response.body() != null) {
                        articleList.clear()
                        articleList.addAll(response!!.body()!!.articles)
                        articlesAdapter.notifyDataSetChanged()
                    } else
                        Toast.makeText(applicationContext, R.string.some_error_occurred, Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
                override fun onFailure(call: Call<NewsResponseData?>, t: Throwable) {
                    setSnackbar { getTopHeadlines() }
                }
            })
    }
    private fun showProgressBar() {
        if (binding.progressBar.progressBarContainer.visibility != View.VISIBLE)
            binding.progressBar.progressBarContainer.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        if (binding.progressBar.progressBarContainer.visibility != View.GONE)
            binding.progressBar.progressBarContainer.visibility = View.GONE
    }
    private fun setSnackbar(runnable: Runnable) {
        val snackbar: Snackbar =
            Snackbar.make(binding.root, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") { runnable.run() }
                .setActionTextColor(resources.getColor(R.color.white))
        snackbar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        val search=menu?.findItem(R.id.searchView)
        val searchView=search?.actionView as SearchView
        searchView.queryHint=getString(R.string.search_in_feed)
        searchView.imeOptions= EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                articlesAdapter.filter.filter(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.logout)
        {
            showAlertDialog()
        }
        return true;
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout from the app")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(applicationContext,"Logged Out Successfully",Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
           dialog.dismiss()
        }
        builder.show()
    }
}
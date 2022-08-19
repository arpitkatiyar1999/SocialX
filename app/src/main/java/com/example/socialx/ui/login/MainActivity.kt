package com.example.socialx.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.socialx.R
import com.example.socialx.adapters.ViewPagerAdapter
import com.example.socialx.databinding.ActivityMainBinding
import com.example.socialx.ui.news.NewsActivity
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAll()
        setTabLayout()

    }

    private fun initAll() {

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

    }

    private fun setTabLayout() {
        with(binding)
        {
            tabLayout.addTab(
                tabLayout.newTab().setText(getString(com.example.socialx.R.string.txt_login))
            )
            tabLayout.addTab(
                tabLayout.newTab().setText(getString(com.example.socialx.R.string.text_signup))
            )
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }

    }

    fun selectTab(pos: Int) {
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(pos))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val oneTapClient = Identity.getSignInClient(this)
                    val auth: FirebaseAuth = Firebase.auth
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                            val idToken = googleCredential.googleIdToken
                            when {
                                idToken != null -> {
                                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                    auth.signInWithCredential(firebaseCredential)
                                        .addOnCompleteListener(this) { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(applicationContext,resources.getString(R.string.signed_in_successfully), Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this, NewsActivity::class.java))
                                            } else {
                                                // If sign in fails, display a message to the user.
//                                                Log.w(TAG, "signInWithCredential:failure", task.exception)
//                                                updateUI(null)
                                            }
                                        }
                                }
                                else -> {
//                                    // Shouldn't happen.
//                                    Log.d(TAG, "No ID token!")
                                }
                            }
                        }
                        else -> {
                            // Shouldn't happen.
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }

    }
}
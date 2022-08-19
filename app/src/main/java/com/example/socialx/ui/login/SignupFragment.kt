package com.example.socialx.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialx.R
import com.example.socialx.databinding.FragmentSignupBinding
import com.example.socialx.ui.news.NewsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentSignupBinding.inflate(inflater,container,false)
        initAll()
        return binding.root
    }

    private fun initAll() {
        auth = Firebase.auth
        with(binding)
        {
            signUpBtn.setOnClickListener { signUpUsingGmail()}
            signInText.setOnClickListener { (activity as MainActivity).selectTab(0) }

        }

    }

    private fun signUpUsingGmail() {
        var email:String = binding.editEmailSignUp.text.toString()
        var password:String = binding.editPassSignUp.text.toString()
        var name:String = binding.editNameSignUp.text.toString()
        var number:String = binding.editNumberSignUp.text.toString()
        if(name.isBlank())
        {
            showToast(id = R.string.enter_name_first)
            binding.editNameSignUp.requestFocus()
            return
        }
        if(email.isBlank())
        {
            showToast(id = R.string.enter_email_first)
            binding.editEmailSignUp.requestFocus()
            return
        }
        if(number.length<10)
        {
            showToast(id = R.string.valid_mobile_number)
            binding.editNumberSignUp.requestFocus()
            return
        }
        if(password.isBlank())
        {
            showToast(id = R.string.enter_password_first)
            binding.editPassSignUp.requestFocus()
            return
        }
        if (password.length<6)
        {
            showToast(id = R.string.password_length)
            binding.editPassSignUp.requestFocus()
            return
        }

        if(!binding.checkBox.isChecked)
        {
            showToast(id=R.string.terms_policies)
            return
        }
        showProgressBar()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context,getString(R.string.account_created_successfully),Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, NewsActivity::class.java)
                    startActivity(intent)
                    (activity as MainActivity).finish()
                } else {
                    showToast(text =task.exception.toString().substringAfter(":").trim() )
                }
                hideProgressBar()
            }
    }

    private fun showProgressBar() {
        if (binding.progressBar.progressBarContainer.visibility != View.VISIBLE)
            binding.progressBar.progressBarContainer.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        if (binding.progressBar.progressBarContainer.visibility != View.GONE)
            binding.progressBar.progressBarContainer.visibility = View.GONE
    }

    private fun showToast(id: Int = -1, text: String = "") {
        if (text.isNotBlank()) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            return
        } else {
            Toast.makeText(context, getString(id), Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
package com.example.socialx.ui

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialx.R
import com.example.socialx.databinding.FragmentSigninBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SigninFragment : Fragment() {
    private val RC_SIGN_IN=100
    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    private lateinit var gso:GoogleSignInOptions
    private lateinit var gsc:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = create()
        signInWithFacebook()
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }
                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    showToast(id = R.string.some_error_occurred)
                }

            })
    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        showProgressBar()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    afterSigningSuccessfull()
                    showToast(id = R.string.signed_in_successfully)
                } else {
                    showToast(text = task.exception.toString().substringAfter(":").trim())
                }
                hideProgressBar()
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        initAll()
        return binding.root
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

    private fun initAll() {
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build())
            .setAutoSelectEnabled(true)
            .build()
        gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(requireActivity(), gso);
        binding.signInBtn.setOnClickListener { signInWithEmail() }
        binding.forgotPassword.setOnClickListener { handleForgotPassword() }
        binding.signUpText.setOnClickListener { (activity as MainActivity).selectTab(1) }
        binding.googleLoginBtn.setOnClickListener { signInWithGmail() }
        binding.fbLoginBtn.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, callbackManager, listOf("public_profile", "email"))
        }
    }

    private fun signInWithGmail() {
        showProgressBar()
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                    hideProgressBar()
                } catch (e: IntentSender.SendIntentException) {
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
            }
    }
    private fun handleForgotPassword() {
        var email = binding.editEmailSignIN.text.toString()
        if (email == null || email.isBlank()) {
            showToast(id = R.string.enter_email_first)
            return
        }
        showProgressBar()
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast(id = R.string.reset_password_link)
                } else {
                    showToast(text = task.exception.toString().substringAfter(":").trim())
                }
                hideProgressBar()
            }
    }

    private fun signInWithEmail() {
        var email: String = binding.editEmailSignIN.text.toString()
        var password: String = binding.editPassSignIn.text.toString()
        if (email.isBlank()) {
            showToast(id = R.string.enter_email_first)
            return
        }
        if (password.isBlank()) {
            showToast(id = R.string.enter_password_first)
            return
        }
        if (password.length < 6) {
            showToast(id = R.string.password_length)
            return
        }
        showProgressBar()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showToast(id = R.string.signed_in_successfully)
                    afterSigningSuccessfull()
                } else {
                    showToast(text = task.exception.toString().substringAfter(":").trim())
                }
                hideProgressBar()
            }
    }
    private fun afterSigningSuccessfull()
    {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        (activity as MainActivity).finish()
    }
    private fun showProgressBar() {
        if (binding.progressBar.progressBarContainer.visibility != View.VISIBLE)
            binding.progressBar.progressBarContainer.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        if (binding.progressBar.progressBarContainer.visibility != View.GONE)
            binding.progressBar.progressBarContainer.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            showProgressBar()
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        showToast(id = R.string.signed_in_successfully)
                                        afterSigningSuccessfull()
                                    } else {
                                        showToast(id = R.string.some_error_occurred)
                                    }
                                }
                            hideProgressBar()
                        }
                        else -> {
                            showToast(id = R.string.some_error_occurred)
                        }

                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            showToast(id=R.string.no_internet_connection)
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }
}
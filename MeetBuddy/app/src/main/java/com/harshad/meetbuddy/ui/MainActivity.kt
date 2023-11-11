package com.harshad.meetbuddy.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.harshad.meetbuddy.R
import com.harshad.meetbuddy.databinding.ActivityMainBinding
import com.harshad.meetbuddy.viewmodel.SignUpLogInViewModel

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId = ""
    private val signUpLogInViewModel: SignUpLogInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(false)
        performClicks()
        observeLiveData()
    }

    private fun observeLiveData() {
        signUpLogInViewModel.verificationIdLiveData.observe(this) { newVerificationId ->
            if (newVerificationId != "") {
                verificationId = newVerificationId
            }
        }

        signUpLogInViewModel.errorMessageLiveData.observe(this) { errorMsg ->
            if (errorMsg != "") {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        signUpLogInViewModel.isUserAuthorizedLiveData.observe(this) { isUserAuthorised ->
            if (isUserAuthorised) {
                launchNextScreen()
            } else {
                Toast.makeText(
                    this,
                    "You are not authorized we have send OTP with your phone number enter this opt",
                    Toast.LENGTH_SHORT
                ).show()
                binding.etvOtpNo.visibility = View.VISIBLE
                binding.tvOtpLabel.visibility = View.VISIBLE
            }
        }
    }

    private fun launchNextScreen() {
        val videoScreenIntent = Intent(this@MainActivity, VideoMeetActivity::class.java)
        startActivity(videoScreenIntent)
    }

    private fun performClicks() {
        binding.btnLogin.setOnClickListener {
            val mobileNo = binding.etvMobileNo.text.toString()
            if (mobileNo.isEmpty()) {
                binding.etvMobileNo.error = "Please enter mobile number"
            } else {
                if (verificationId == "") {
                    signUpLogInViewModel.sendVerificationCode(
                        "+91$mobileNo",
                        this@MainActivity,
                        auth
                    )
                } else {
                    if (binding.etvOtpNo.text.toString().isNotEmpty()) {
                        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            binding.etvOtpNo.text.toString()
                        )
                        signUpLogInViewModel.verifyOpt(credential, this, auth)
                    } else {
                        binding.etvOtpNo.error = "please enter OTP.."
                    }
                }
            }
        }
    }

}
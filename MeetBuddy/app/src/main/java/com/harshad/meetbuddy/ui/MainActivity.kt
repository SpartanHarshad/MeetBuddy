package com.harshad.meetbuddy.ui


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val REQ_PERMISION_ID = 1111

    private val permissions_ = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.RECORD_AUDIO
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(false)
        checkPermissions()
        observeLiveData()
    }

    private fun checkPermissions() {
        var isAllPermissionGranted = true
        for (permission in permissions_) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isAllPermissionGranted = false
            }
        }
      Log.d("isPermission","permission $isAllPermissionGranted")
        if (isAllPermissionGranted) {
            performClicks()
        } else {
            ActivityCompat.requestPermissions(this, permissions_, REQ_PERMISION_ID)
        }
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
            Log.d("LogIn","is member $isUserAuthorised")
            if (isUserAuthorised) {
                launchNextScreen()
            } else {
                binding.etvOtpNo.visibility = View.VISIBLE
                binding.tvOtpLabel.visibility = View.VISIBLE
                Toast.makeText(
                    this,
                    "You are not authorized we have send OTP with your phone number enter this opt",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun launchNextScreen() {
        val videoScreenIntent = Intent(this@MainActivity, ChooseCallModeActivity::class.java)
        startActivity(videoScreenIntent)
    }

    private fun performClicks() {
        binding.btnLogin.setOnClickListener {
            //launchNextScreen()
            val mobileNo = binding.etvMobileNo.text.toString()
            if (mobileNo.isEmpty()) {
                binding.etvMobileNo.error = "Please enter mobile number"
            } else {
                if (verificationId == "") {
                    /*signUpLogInViewModel.sendVerificationCode(
                        "+91$mobileNo",
                        this@MainActivity,
                        auth
                    )*/
                    launchNextScreen()
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISION_ID) {
            var allPermissionGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionGranted = false
                    break
                }
            }
            if (!allPermissionGranted) {
                ActivityCompat.requestPermissions(this, permissions_, REQ_PERMISION_ID)
            } else {
                performClicks()
            }
        }
    }
}
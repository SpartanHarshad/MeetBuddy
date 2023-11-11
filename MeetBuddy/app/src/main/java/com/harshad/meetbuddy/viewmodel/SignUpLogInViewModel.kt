package com.harshad.meetbuddy.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class SignUpLogInViewModel : ViewModel() {

    val verificationIdLiveData = MutableLiveData<String>()
    val isUserAuthorizedLiveData = MutableLiveData<Boolean>()
    val errorMessageLiveData = MutableLiveData<String>()

    fun sendVerificationCode(phoneNumber: String, ctx: Activity, auth: FirebaseAuth) {
        verificationIdLiveData.postValue("")
        val callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                errorMessageLiveData.postValue(".....")
                verifyOpt(credential, ctx, auth)
                Log.d("LogIn", "VerificationCompleted : Secussfull")
            }

            override fun onVerificationFailed(exc: FirebaseException) {
                errorMessageLiveData.postValue(exc.localizedMessage)
                Log.d("LogIn", "exception : ${exc.localizedMessage}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Log.d("LogIn", "on_code sent : $verificationId")
                verificationIdLiveData.postValue(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(ctx)
            .setCallbacks(callBack)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    fun verifyOpt(credential: PhoneAuthCredential, ctx: Activity, auth: FirebaseAuth) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(ctx) { task ->
                if (task.isSuccessful) {
                    Log.d("LogIn", "comp : ${task.isComplete}")
                    isUserAuthorizedLiveData.postValue(true)
                } else {
                    Log.d("LogIn", "comp : ${task.exception?.localizedMessage}")
                    isUserAuthorizedLiveData.postValue(false)
                }
            }
    }
}
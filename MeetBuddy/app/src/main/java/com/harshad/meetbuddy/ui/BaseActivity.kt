package com.harshad.meetbuddy.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.harshad.meetbuddy.R
import com.harshad.meetbuddy.utility.NetworkConnectionObserver

open class BaseActivity: AppCompatActivity() {

    private lateinit var networkDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        networkDialog = MaterialAlertDialogBuilder(this)
            .setView(R.layout.layout_network_dialog)
            .setCancelable(false)
            .create()

        val networkConnectionObserver = NetworkConnectionObserver(this)
        networkConnectionObserver.observe(this){
            if (!it){
                  if (!networkDialog.isShowing)
                      networkDialog.show()
            }else{
                if (networkDialog.isShowing)
                    networkDialog.hide()
            }
        }
    }
}
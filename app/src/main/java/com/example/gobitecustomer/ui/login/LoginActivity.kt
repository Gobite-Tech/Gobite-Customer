package com.example.gobitecustomer.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.LoginRequestNew
import com.example.gobitecustomer.data.modelNew.OTPRequest
import com.example.gobitecustomer.databinding.ActivityLoginBinding
import com.example.gobitecustomer.ui.home.HomeActivity
import com.example.gobitecustomer.ui.signup.SignUpActivity
import com.example.gobitecustomer.utils.AppConstants
import org.koin.android.ext.android.inject
import java.lang.NullPointerException
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel by viewModel<LoginViewModel>()
    private lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setListener()
        if (!preferencesHelper.oauthId.isNullOrEmpty()) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    private fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            val phoneNo = binding.editPhone.text.toString()
            preferencesHelper.mobile = phoneNo
            var otp : Int = 0
            if (phoneNo.isNotEmpty() && phoneNo.length == 10) {
                try {
                    val secureRandom = SecureRandom.getInstanceStrong()
                    otp = secureRandom.nextInt(900000) + 100000
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                Log.e("OTP Generated - ", " - Request Login$phoneNo")
                val intent = Intent(this,OTPActivity::class.java)
                intent.putExtra(AppConstants.CUSTOMER_OTP,otp.toString())
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Invalid Phone Number!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}
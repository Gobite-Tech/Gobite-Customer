package com.example.gobitecustomer.ui.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.LoginRequestNew
import com.example.gobitecustomer.data.modelNew.OTPRequest
import com.example.gobitecustomer.data.modelNew.sendOtpModel
import com.example.gobitecustomer.databinding.ActivityOtpactivityBinding
import com.example.gobitecustomer.ui.home.HomeActivity
import com.example.gobitecustomer.ui.signup.SignUpActivity
import com.example.gobitecustomer.utils.AppConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpactivityBinding
    private val viewModel by viewModel<LoginViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var number: String? = null
    private var otp = ""
    private var storedVerificationId = ""
    lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)
        getArgs()
        initView()
        setListener()
        setObservers()
        number?.let { sendOtp(it) }
    }

    private fun getArgs() {
        number = intent.getStringExtra(AppConstants.CUSTOMER_MOBILE)
        otp = intent.getStringExtra(AppConstants.CUSTOMER_OTP)!!
        Log.e("Number testing", number!!)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otpactivity)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        auth = FirebaseAuth.getInstance()
        binding.textOtpSent.text =
            "Enter the six digit OTP which has been sent to your mobile number: $number"
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            if (binding.editOtp.text.toString()
                    .isNotEmpty() && binding.editOtp.text.toString().length == 6
            ) {
                if(binding.editOtp.text.toString() == otp){
//                    viewModel.getOTP(OTPRequest(number!!))
                }
            }
        }
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }

        countDownTimer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.textResendOtp.setText("Resend OTP (" + millisUntilFinished / 1000 + ")")
            }

            override fun onFinish() {
                binding.textResendOtp.setText("Resend OTP")
                binding.textResendOtp.isEnabled = true
            }
        }
    }

    private fun sendOtp(number: String) {
        val sendOtpModel = sendOtpModel(
            from = "Blueve",
            to = ArrayList<String>().apply { add(number) },
            type = "sms",
            type_details = "",
            data_coding = "plain",
            flash_message = false,
            campaign_id = "5622674",
            template_id = "883641850"
        )

        viewModel.sendOTP(sendOtpModel)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cancel process?")
            .setMessage("Are you sure want to cancel the OTP process?")
            .setPositiveButton("Yes") { dialog, which ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun setObservers() {


        //sendOTP
        viewModel.performGetOTPStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(this, "OTP send successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "Something send wrong", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Sending OTP...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })


        //getOTP
        viewModel.performGetOTPStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val OtpResult = resource.data

                            Log.e(
                                "LoginActivity",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )
                            progressDialog.dismiss()
                            val authToken = OtpResult.data.auth_token
                            preferencesHelper.oauthId = authToken
                            viewModel.Login(LoginRequestNew("OTP",authToken,"456321"))

                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "Please Sign Up", Toast.LENGTH_SHORT).show()
                        Log.e("Sign Up needed", "Failed ${resource.data}")
                        if (resource.error is NullPointerException){
                            viewModel.SignUpIn(OTPRequest("SIGNUP",preferencesHelper.mobile!!))
                        }else{
//                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })

        //SIGN UP
        viewModel.performSignUpInStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val loginresult = resource.data

                            Log.e(
                                "SignUp ka",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )



                            preferencesHelper.oauthId = loginresult.data.auth_token
                            startActivity(Intent(applicationContext, SignUpActivity::class.java))
                            finish()

                            progressDialog.dismiss()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Registering ...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })


        //Login
        viewModel.performLoginStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val loginresult = resource.data

                            Log.e(
                                "SignUp ka",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )

                            preferencesHelper.jwtToken = loginresult.data.token

                            Toast.makeText(applicationContext,"Welcome!!",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                            finish()

                            progressDialog.dismiss()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })
    }
}
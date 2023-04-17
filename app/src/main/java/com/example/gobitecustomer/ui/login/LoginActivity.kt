package com.example.gobitecustomer.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.LoginRequestNew
import com.example.gobitecustomer.data.modelNew.OTPRequest
import com.example.gobitecustomer.databinding.ActivityLoginBinding
import org.koin.android.ext.android.inject
import java.lang.NullPointerException
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel by viewModel<LoginViewModel>()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        initView()
//        setListener()
//        setObservers()
//        if (!preferencesHelper.oauthId.isNullOrEmpty() && preferencesHelper.userId != -1) {
//            startActivity(Intent(applicationContext, HomeActivity::class.java))
//            finish()
//        }
    }

    private fun initView() {
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    private fun setListener() {
//        binding.buttonLogin.setOnClickListener {
//            val phoneNo = binding.editPhone.text.toString()
//            preferencesHelper.mobile = phoneNo
//            if (phoneNo.isNotEmpty() && phoneNo.length == 10) {
//                Log.e("LoginAvt" , " - Request Login$phoneNo")
//                viewModel.getOTP(OTPRequest("LOGIN", phoneNo))
//            } else {
//                Toast.makeText(applicationContext, "Invalid Phone Number!", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
    }


    private fun setObservers() {
        //getOTP
        viewModel.performGetOTPStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val OtpResult = resource.data

                            Log.e(
                                "Login Activity",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )
                            progressDialog.dismiss()
                            val authToken = OtpResult.data.auth_token
                            preferencesHelper.oauthId = authToken
                            viewModel.Login(LoginRequestNew("OTP",authToken,"456321"))




                            /* if (resource.data.code == 1163) {
                                 val userModel = resource.data.data?.userModel
                                 preferencesHelper.userId = userModel?.userId
                                 unloadKoinModules(networkModule)
                                 loadKoinModules(networkModule)
                                 val intent = Intent(applicationContext, SignUpActivity::class.java)
                                 startActivity(intent)
                                 finish()
                             } else {
                                 val userModel = resource.data.data?.userModel
                                 val placeModel = resource.data.data?.placeModel
                                 if (userModel != null) {
                                     preferencesHelper.saveUser(
                                         userId = userModel.userId,
                                         name = userModel.name,
                                         email = userModel.email,
                                         mobile = preferencesHelper.mobile,
                                         role = userModel.role,
                                         oauthId = preferencesHelper.oauthId,
                                         place = Gson().toJson(placeModel)
                                     )
                                 }
                                 unloadKoinModules(networkModule)
                                 loadKoinModules(networkModule)
                                 startActivity(Intent(applicationContext, HomeActivity::class.java))
                                 finish()
                             }*/
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
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
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

//                            preferencesHelper.oauthId = loginresult.data.auth_token
//                            startActivity(Intent(applicationContext, SignUpActivity::class.java))
//                            finish()

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

//                            startActivity(Intent(applicationContext, HomeActivity::class.java))
//                            finish()

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
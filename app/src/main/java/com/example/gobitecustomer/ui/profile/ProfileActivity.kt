package com.example.gobitecustomer.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.UpdateUserRequest
import com.example.gobitecustomer.databinding.ActivityProfileBinding
import com.example.gobitecustomer.ui.order.OrdersActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModel<ProfileViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initView()
        setListener()
        setObservers()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        progressDialog = ProgressDialog(this)
        binding.editEmail.setText(preferencesHelper.email)
        binding.editName.setText(preferencesHelper.name)
        binding.textMobile.text = preferencesHelper.mobile
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonUpdate.setOnClickListener {
            if (binding.editName.text.toString().isNotEmpty()) {
                        progressDialog.setMessage("Updating profile...")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        viewModel.updateUserDetails(
                            UpdateUserRequest(
                                binding.editName.text.toString(),
                                preferencesHelper.mobile.toString()
                            )
                        )
            } else {
                Toast.makeText(applicationContext, "Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
        binding.layoutChooseCampus.setOnClickListener {
//            showCampusListBottomDialog()
        }
        binding.textYourOrders.setOnClickListener {
            startActivity(Intent(applicationContext, OrdersActivity::class.java))
        }

    }

    private fun setObservers() {

        viewModel.performUpdateStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        binding.buttonUpdate.isEnabled = true
                        preferencesHelper.name = binding.editName.text.toString()
                        preferencesHelper.email = binding.editEmail.text.toString()

                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            preferencesHelper.clearCartPreferences()
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.buttonUpdate.isEnabled = true
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        binding.buttonUpdate.isEnabled = true
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        binding.buttonUpdate.isEnabled = false
                        progressDialog.setMessage("Updating profile...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

//    private fun showCampusListBottomDialog() {
//        viewModel.searchPlace("")
//        val dialog = PlacePickerDialog()
//        dialog.setListener(this)
//        dialog.placesList = places
//        dialog.show(supportFragmentManager, null)
//    }


}
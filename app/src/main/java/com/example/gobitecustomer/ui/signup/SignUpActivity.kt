package com.example.gobitecustomer.ui.signup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.SignUpRequestNew
import com.example.gobitecustomer.data.modelNew.UpdateUserRequest
import com.example.gobitecustomer.databinding.ActivitySignUpBinding
import com.example.gobitecustomer.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModel<SignUpViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getArgs()
        initView()
        setListener()
        setObservers()
    }

    private fun getArgs(){
        number = preferencesHelper.mobile
    }

    private fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonRegister.setOnClickListener {
                if(binding.editName.text.toString().isNotEmpty()){
                    val oathId = preferencesHelper.oauthId
                    val phoneNo = preferencesHelper.mobile
                    viewModel.registerUser(
                        SignUpRequestNew("PASSWORD",oathId!!,"456321",
                        phoneNo!!,"123456","123456")
                    )
            }else{
                Toast.makeText(applicationContext,"Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
        binding.layoutChooseCampus.setOnClickListener {
            //TODO campus list
//            showCampusListBottomDialog()
        }
    }

    private fun setObservers() {
        /*viewModel.performFetchPlacesStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Getting places")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No places found", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        places.clear()
                        it.data?.data?.let { it1 -> places.addAll(it1) }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "Something went wrong", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
            }
        })*/


        viewModel.performSignUpStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        preferencesHelper.name = binding.editName.text.toString()
                        preferencesHelper.email = binding.editEmail.text.toString()
//                        preferencesHelper.place = Gson().toJson(selectedPlace)
                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Registration Successful! Login Now", Toast.LENGTH_SHORT).show()
                            preferencesHelper.oauthId = null

                            viewModel.updateUserDetails( "Bearer ${resource.data.data.token}",
                                UpdateUserRequest(
                                    binding.editName.text.toString(),
                                    preferencesHelper.mobile.toString()
                                )
                            )

                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Registering...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })



        viewModel.performUpdateStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        preferencesHelper.name = binding.editName.text.toString()
                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            preferencesHelper.clearCartPreferences()
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Please Update your Profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Please Update your Profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating profile...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })
    }

//    private fun showCampusListBottomDialog() {
//        viewModel.searchPlace("")
//        val dialog = PlacePickerDialog()
//        dialog.setListener(this)
//        dialog.placesList = places
//        dialog.show(supportFragmentManager,null)
//
//    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@SignUpActivity)
            .setTitle("Cancel process?")
            .setMessage("Are you sure want to cancel the registration process?")
            .setPositiveButton("Yes") { dialog, which ->
                preferencesHelper.oauthId = null
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

//    override fun onPlaceClick(place: PlaceModel) {
//        selectedPlace = place
//        binding.textCampusName.text = place.name
//    }
}
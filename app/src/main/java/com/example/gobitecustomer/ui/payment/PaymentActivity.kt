package com.example.gobitecustomer.ui.payment

import android.animation.LayoutTransition
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.databinding.ActivityPaymentBinding
import com.example.gobitecustomer.ui.home.HomeActivity
import com.example.gobitecustomer.ui.placeorder.PlaceOrderActivity
import com.example.gobitecustomer.utils.AppConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import dev.shreyaspatil.easyupipayment.model.TransactionStatus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentActivity : AppCompatActivity(),PaymentStatusListener {

    private lateinit var binding: ActivityPaymentBinding
    private val viewModel by viewModel<PaymentViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private var orderId: String? = null
    private var price : String ?= null
    private var token: String? = null
    private lateinit var easyUpiPayment : EasyUpiPayment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        getArgs()
        initView()
        setObservers()
        setupPaymentModes()
        binding.radioUpi.setOnCheckedChangeListener { _, b ->
            if (b){
                binding.textPay.visibility = View.VISIBLE
            }else{
                binding.textPay.visibility = View.GONE
            }
        }

        binding.textPay.setOnClickListener {
            setListener()
        }

        binding.textTotal.text = price.toString()

        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getArgs() {
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        val pricey = intent.getDoubleExtra(AppConstants.DISCOUNTED_AMOUNT, 1000.0)
        price = String.format("%.2f", pricey)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.layoutContent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setListener() {

        val transac_id = "TID" + System.currentTimeMillis()

        try {
            Log.e("PaymentActivity", "setListener")
            Log.e("PaymentActivity", "setListener paisa: ${price}"+"0" )
            easyUpiPayment = EasyUpiPayment(this) {
                this.paymentApp = PaymentApp.ALL
                this.payeeVpa = "bluevelvettechprivatelimited.ibz@icici"
                this.payeeName = preferencesHelper.name
                this.transactionId = transac_id
                this.transactionRefId = transac_id
                this.description = "Payment for Order"
                this.payeeMerchantCode = ""
                this.amount = price.toString()+"0"
            }
            Log.e("PaymentActivity", "setListener: ${easyUpiPayment}")
            easyUpiPayment.setPaymentStatusListener(this)
            easyUpiPayment.startPayment()

        }catch (e : Exception){
            e.printStackTrace()
            Log.e("PaymentActivity", "setListener: ${e.printStackTrace()}" )
        }
    }

    private fun setObservers() {

    }

    private fun setupPaymentModes(){

    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@PaymentActivity)
            .setTitle("Cancel process?")
            .setMessage("Are you sure want to cancel the order")
            .setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString())
//        textView_status.text = transactionDetails.toString()

        when (transactionDetails.transactionStatus) {
            TransactionStatus.SUCCESS -> onTransactionSuccess()
            TransactionStatus.FAILURE -> onTransactionFailed()
            TransactionStatus.SUBMITTED -> onTransactionSubmitted()
            else -> {}
        }
    }

    private fun onTransactionSubmitted() {
        Toast.makeText(this, "Transaction Processed", Toast.LENGTH_SHORT).show()
    }

    private fun onTransactionFailed() {
        Toast.makeText(this, "Error in payment, Try Again", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }

    private fun onTransactionSuccess() {
        Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, PlaceOrderActivity::class.java)
        intent.putExtra(AppConstants.ORDER_ID,orderId)
        startActivity(intent)
        finish()

    }

    override fun onTransactionCancelled() {
        Toast.makeText(this, "Error in payment, Try Again", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }
}
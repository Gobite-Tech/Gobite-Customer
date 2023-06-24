package com.example.gobitecustomer.ui.search

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.modelNew.allShops
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ActivitySearchBinding
import com.example.gobitecustomer.ui.restaurant.RestaurantActivity
import com.example.gobitecustomer.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import java.util.Timer
import java.util.TimerTask

class SearchActivity : AppCompatActivity() ,View.OnClickListener {

    private lateinit var binding: ActivitySearchBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var outletAdapter: SearchAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var shopList: ArrayList<shops>
    private var adapterList : ArrayList<shops> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()

        shopList = preferencesHelper.getShopList() as ArrayList<shops>

        binding.editSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterList.clear()
                if (query != null) {
                    Log.e("SF req send" , "SF")
                    searchOutlets(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun searchOutlets(query: String) {
        for (shop in shopList){
            if(shop.name != null){
                val match = kmp(shop.name,query)
                Log.e("List is" , "$match")
                if (match.isNotEmpty()){
                    adapterList.add(shop)
                }
            }
        }
        outletAdapter.notifyDataSetChanged()
    }

    fun kmp(numval: String, patternval: String): List<Int> {
        var text = numval.lowercase()
        var pattern = patternval.lowercase()
        val n = text.length
        val m = pattern.length
        val lps = computeLps(pattern)
        val matches = mutableListOf<Int>()
        var i = 0  // index into text
        var j = 0  // index into pattern
        while (i < n) {
            if (text[i] == pattern[j]) {
                i++
                j++
                if (j == m) {
                    matches.add(i - m)
                    j = lps[j - 1]
                }
            } else if (j > 0) {
                j = lps[j - 1]
            } else {
                i++
            }
        }
        return matches
    }

    fun computeLps(pattern: String): IntArray {
        val m = pattern.length
        val lps = IntArray(m)
        var len = 0
        var i = 1
        while (i < m) {
            if (pattern[i] == pattern[len]) {
                len++
                lps[i] = len
                i++
            } else if (len > 0) {
                len = lps[len - 1]
            } else {
                lps[i] = 0
                i++
            }
        }
        return lps
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        //binding.layoutSearch.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        setupShopRecyclerView()
    }


    private fun setupShopRecyclerView() {
        outletAdapter = SearchAdapter(adapterList, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: shops, position: Int) {
                val shopList = preferencesHelper.getShopList()
                val shop = shopList?.firstOrNull {
                    it.id == item.id
                }
                val intent = Intent(applicationContext, RestaurantActivity::class.java)
                intent.putExtra(AppConstants.SHOP, Gson().toJson(shop))
                startActivity(intent)
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = outletAdapter
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close -> {
                onBackPressed()
            }
        }
    }

}
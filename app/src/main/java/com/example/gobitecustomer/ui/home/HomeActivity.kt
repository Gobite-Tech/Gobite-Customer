package com.example.gobitecustomer.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.model.MenuItemModel
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ActivityHomeBinding
import com.example.gobitecustomer.databinding.HeaderLayoutBinding
import com.example.gobitecustomer.ui.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.materialdrawer.Drawer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer

class HomeActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModel<HomeViewModel>()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var progressDialog: ProgressDialog
    private var shopList: ArrayList<shops> = ArrayList()
    private var cartList: ArrayList<MenuItemModel> = ArrayList()
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    private var placeId = ""

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.image_menu -> {
                    drawer.openDrawer()
                }
//                R.id.text_search -> {
//                    startActivity(Intent(applicationContext, SearchActivity::class.java))
//                }
            }
        }
    }
}
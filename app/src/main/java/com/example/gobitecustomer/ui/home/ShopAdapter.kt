package com.example.gobitecustomer.ui.home

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ItemShopBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ShopAdapter(
    private val context: Context,
    private val shopList: List<shops>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding: ItemShopBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_shop,
            parent,
            false
        )
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shopList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    class ShopViewHolder(var binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(shop: shops, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(shop.icon).placeholder(R.drawable.ic_shop).into(binding.imageShop)
            binding.textShopName.text = shop.name
            var isShopOpen = shop.open_now
            binding.tvTimeShop.text = shop.avg_serve_time.toString() + " mins"
//            var sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//            var sdf2 = SimpleDateFormat("hh:mm a", Locale.getDefault())
//            var openingTimeString = sdf2.format(sdf.parse(shop.name))
//            var closingTimeString = sdf2.format(sdf.parse(shop.name))
//            var openingTime = getTime(shop.name)
//            var closingTime = getTime(shop.name)
//            val currentTime = Date()
//            isShopOpen = currentTime.before(closingTime) && currentTime.after(openingTime)
            if (isShopOpen) {
                binding.textShopName.setTextColor(
                    ContextCompat.getColor(
                        binding.layoutRoot.context,
                        android.R.color.black
                    )
                )
                binding.textShopDesc.setTextColor(
                    ContextCompat.getColor(
                        binding.layoutRoot.context,
                        android.R.color.tab_indicator_text
                    )
                )
                binding.textShopRating.setTextColor(
                    ContextCompat.getColor(
                        binding.layoutRoot.context,
                        android.R.color.tab_indicator_text
                    )
                )
                binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_star,
                    0,
                    0,
                    0
                )
//                binding.imageShop.clearColorFilter( binding.textShopDesc.text = "Closes at "+shop.closing_time)
                    binding.textShopDesc.text = "Pick up only"
            } else {
                binding.textShopDesc.text = "Opens at " + shop.opening_time
                binding.textShopName.setTextColor(
                    ContextCompat.getColor(
                        binding.layoutRoot.context,
                        R.color.disabledColor
                    )
                )
                binding.textShopDesc.setTextColor(
                    ContextCompat.getColor(
                        binding.layoutRoot.context,
                        R.color.disabledColor
                    )
                )
                binding.textShopRating.setTextColor(
                    ContextCompat.getColor(
                        binding.layoutRoot.context,
                        R.color.disabledColor
                    )
                )
                binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_star_disabled,
                    0,
                    0,
                    0
                )
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                binding.imageShop.colorFilter = filter
            }
            binding.layoutRoot.setOnClickListener { listener.onItemClick(shop, position) }

        }

        private fun getTime(time: String): Date {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val timeCalendar = Calendar.getInstance()
            val closingTime = sdf.parse(time)
            val cal1 = Calendar.getInstance()
            cal1.time = closingTime
            timeCalendar[Calendar.HOUR_OF_DAY] = cal1.get(Calendar.HOUR_OF_DAY)
            timeCalendar[Calendar.MINUTE] = cal1.get(Calendar.MINUTE)
            timeCalendar[Calendar.SECOND] = cal1.get(Calendar.SECOND)
            return timeCalendar.time
        }


    }

    interface OnItemClickListener {
        fun onItemClick(item: shops, position: Int)
    }


}
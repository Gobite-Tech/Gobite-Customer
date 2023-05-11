package com.example.gobitecustomer.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.modelNew.OrderX
import com.example.gobitecustomer.databinding.ItemOrderBinding
import com.example.gobitecustomer.utils.AppConstants
import java.lang.Exception
import java.text.SimpleDateFormat

class OrdersAdapter(private val orderList: ArrayList<OrderX>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): OrderViewHolder {
        val binding: ItemOrderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(var binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderX, position: Int, listener: OnItemClickListener) {
            //Picasso.get().load(menuItem.photoUrl).into(binding.imageShop)
            binding.textShopName.text = order.shop_name
            var cnt = 0
            var price = 0.0
            for (item in order.items) {
                cnt += item.quantity
                price += item.total_price
            }
            binding.textItemsCount.text = "$cnt ITEM(S)"
            try {
                val apiDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
                val date = apiDateFormat.parse(order.order_placed_time)
                val dateString = appDateFormat.format(date)
                binding.textOrderTime.text = dateString
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.textOrderPrice.text = "₹ $price"
            var items = ""
            order.items.forEach {
                items += it.quantity.toString() + " X " + it.item_name + "\n"
            }
            binding.textOrderItems.text = items
            val orderStatus = order.order_status
            binding.textOrderStatus.text = orderStatus


            //Rating
//            if (order.transactionModel.orderModel.rating == 0.0) {
//                binding.buttonTrackRate.visibility = View.VISIBLE
//                binding.textOrderRating.visibility = View.GONE
//            } else {
//                binding.buttonTrackRate.visibility = View.GONE
//                binding.textOrderRating.visibility = View.VISIBLE
//                binding.textOrderRating.text = order.transactionModel.orderModel.rating.toString()
//            }


            when (orderStatus) {

                AppConstants.ORDER_STATUS_PREPARED, -> {
                    binding.buttonTrackRate.text = "Completed"
                    binding.textOrderStatus.setCompoundDrawablesWithIntrinsicBounds(
                        binding.textOrderStatus.context.getDrawable(R.drawable.ic_checked),
                        null,
                        null,
                        null)
                }

                AppConstants.ORDER_STATUS_CANCELLED -> {
                    binding.buttonTrackRate.text = "Ccancelled"
                    binding.textOrderStatus.setCompoundDrawablesWithIntrinsicBounds(
                        binding.textOrderStatus.context.getDrawable(R.drawable.ic_cancelled),
                        null,
                        null,
                        null)
                }

                else -> {
                    binding.buttonTrackRate.text = "Track Order"
                    binding.textOrderStatus.setCompoundDrawablesWithIntrinsicBounds(
                        binding.textOrderStatus.context.getDrawable(R.drawable.ic_pending),
                        null,
                        null,
                        null)
                }

            }
            binding.layoutRoot.setOnClickListener { listener.onItemClick(order, position) }

//            Rate Order
//            binding.buttonTrackRate.setOnClickListener {
//                if (binding.buttonTrackRate.text.toString() == "Rate Order" || binding.buttonTrackRate.text.toString() == "Rate Food") {
//                    listener.onRatingClick(order, position)
//                } else {
//                    listener.onItemClick(order, position)
//                }
//            }

        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: OrderX, position: Int)
        fun onRatingClick(item: OrderX, position: Int)
    }

}
package com.example.gobitecustomer.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.modelNew.ItemXX
import com.example.gobitecustomer.databinding.ItemOrderProductBinding

class OrderItemAdapter(private val context: Context, private val foodItemList: List<ItemXX>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): OrderItemViewHolder {
        val binding: ItemOrderProductBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(foodItemList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class OrderItemViewHolder(var binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: ItemXX, position: Int, listener: OnItemClickListener) {
            binding.textFoodName.text = food.quantity.toString()+" x "+food.item_name
            binding.textFoodPrice.text = "₹" + food.actual_price * food.quantity
            binding.layoutRoot.setOnClickListener { listener.onItemClick(food, position) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: ItemXX?, position: Int)
    }

}
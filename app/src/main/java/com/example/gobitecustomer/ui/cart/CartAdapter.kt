package com.example.gobitecustomer.ui.cart

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.modelNew.Item
import com.example.gobitecustomer.databinding.ItemCartProductBinding

class CartAdapter(private val context: Context, private val foodItemList: List<Item>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CartItemViewHolder {
        val binding: ItemCartProductBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cart_product, parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(foodItemList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class CartItemViewHolder(var binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Item, position: Int, listener: OnItemClickListener) {
            binding.textFoodName.text = food.quantity.toString()+" x "+food.name
            binding.textFoodPrice.text = "₹" + food.variants[food.variants.size -1 ].price * food.quantity
            binding.layoutRoot.setOnClickListener { listener.onItemClick(food, position) }
            if (food.quantity == 0) {
                binding.layoutQuantityControl.imageSub.visibility = View.GONE
                binding.layoutQuantityControl.textQuantity.text = "Add"
            } else {
                binding.layoutQuantityControl.imageSub.visibility = View.VISIBLE
                binding.layoutQuantityControl.textQuantity.text = food.quantity.toString()
            }
            binding.layoutQuantityControl.imageAdd.setOnClickListener { listener.onQuantityAdd(position) }
            binding.layoutQuantityControl.imageSub.setOnClickListener { listener.onQuantitySub(position) }
        }

        init {
            binding.layoutQuantityControl.root.layoutTransition.enableTransitionType(
                LayoutTransition.CHANGING)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Item?, position: Int)
        fun onQuantityAdd(position: Int)
        fun onQuantitySub(position: Int)
    }

}
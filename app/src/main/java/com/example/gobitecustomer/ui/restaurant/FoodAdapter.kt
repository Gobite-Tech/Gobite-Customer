package com.example.gobitecustomer.ui.restaurant

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.modelNew.Item
import com.example.gobitecustomer.databinding.ItemFoodBinding
import com.squareup.picasso.Picasso

class FoodAdapter(private val context: Context, private val foodItemList: List<Item>, private val listener: OnItemClickListener,
                  private val isShopOpen: Boolean = true) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FoodViewHolder {
        val binding: ItemFoodBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_food, parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodItemList[position], foodItemList, holder.adapterPosition, listener, isShopOpen)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class FoodViewHolder(var binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Item, foodItemList: List<Item>, position: Int, listener: OnItemClickListener, isShopOpen: Boolean) {
            binding.textCategory.visibility = View.GONE
            binding.textCategory.text = food.category
            if (position > 0 && foodItemList[position - 1].category == food.category) {
                binding.textCategory.visibility = View.GONE
            } else {
                binding.textCategory.visibility = View.VISIBLE
            }
            binding.textFoodName.text = food.name
            binding.textFoodDesc.text = food.category
            binding.textFoodPrice.text = "₹" + food.variants[0].price.toString()
            Picasso.get().load(food.icon).placeholder(R.drawable.ic_food).into(binding.imageFood)
            binding.layoutRoot.setOnClickListener { listener.onItemClick(food, position) }
//            if (food.isVeg == 1) {
//                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_veg))
//            } else {
//                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_non_veg))
//            }

                binding.layoutQuantityControl.imageSub.visibility = View.VISIBLE
                binding.layoutQuantityControl.textQuantity.text = food.quantity.toString()

            binding.layoutQuantityControl.imageAdd.setOnClickListener { listener.onQuantityAdd(position) }
            binding.layoutQuantityControl.imageSub.setOnClickListener { listener.onQuantitySub(position) }
//            if (food.variants[0].availability==0) {
//                binding.textFoodName.setTextColor(ContextCompat.getColor(binding.layoutRoot.context, R.color.disabledColor))
//                binding.textFoodPrice.setTextColor(ContextCompat.getColor(binding.layoutRoot.context, R.color.disabledColor))
//                binding.layoutQuantityControl.root.visibility = View.GONE
//                val colorMatrix = ColorMatrix()
//                colorMatrix.setSaturation(0f)
//                val filter = ColorMatrixColorFilter(colorMatrix)
//                binding.imageFood.colorFilter = filter
//            }else{
                binding.textFoodName.setTextColor(ContextCompat.getColor(binding.layoutRoot.context, android.R.color.black))
                binding.textFoodPrice.setTextColor(ContextCompat.getColor(binding.layoutRoot.context, android.R.color.black))
                binding.layoutQuantityControl.root.visibility = View.VISIBLE
                binding.imageFood.clearColorFilter()
//            }
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
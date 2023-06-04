package com.example.gobitecustomer.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ItemSearchBinding
import com.squareup.picasso.Picasso

class SearchAdapter(private val outletList: List<shops>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SearchViewHolder {
        val binding: ItemSearchBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_search, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(outletList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return outletList.size
    }

    class SearchViewHolder(var binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: shops, position: Int, listener: OnItemClickListener) {
                binding.textShopName.text = menuItem.name
            if (menuItem.open_now){
                binding.textShopDesc.text = "Open Now"
            }else{
                binding.textShopDesc.text = "Closed"
            }
//            Picasso.get().load(menuItem.photoUrl).placeholder(R.drawable.ic_food).into(binding.imageShop)
            binding.layoutRoot.setOnClickListener { listener.onItemClick(menuItem, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: shops, position: Int)
    }

}
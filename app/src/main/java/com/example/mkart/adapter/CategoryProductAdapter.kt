package com.example.mkart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.mkart.activity.ProductDetailActivity
import com.example.mkart.databinding.ItemCategoryProductLayoutBinding
import com.example.mkart.databinding.LayoutProductItemBinding
import com.example.mkart.model.AddProductModel

class CategoryProductAdapter(val context: Context, val list:ArrayList<AddProductModel>)
    : RecyclerView.Adapter<CategoryProductAdapter.CategoryProductViewViewHolder>() {


    inner class CategoryProductViewViewHolder(val binding:ItemCategoryProductLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewViewHolder {
        val binding= ItemCategoryProductLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return CategoryProductViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryProductViewViewHolder, position: Int) {
        Glide.with(context).load(list[position].productCoverImage).into(holder.binding.imageView4)
        holder.binding.textView6.text=list[position].productName
        holder.binding.textView7.text=list[position].productSp

        holder.itemView.setOnClickListener{
            val intent= Intent(context,ProductDetailActivity::class.java)
            intent.putExtra("id",list[position].productId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
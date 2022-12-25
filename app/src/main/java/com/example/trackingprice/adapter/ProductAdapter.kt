package com.example.trackingprice.adapter

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackingprice.R
import com.example.trackingprice.model.Product
import com.squareup.picasso.Picasso
import java.util.*


class ProductAdapter internal constructor(
    context: Context,
) :RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    val mContext = context
    private val inflater:LayoutInflater = LayoutInflater.from(context)
    private var productList = arrayListOf<Product>()

    inner class ProductViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val productName : TextView = itemView.findViewById(R.id.product_name)
        val productPrice : TextView = itemView.findViewById(R.id.product_price)
        val productDateUpdated : TextView = itemView.findViewById(R.id.last_checked_time)
        val productPriceTrend:TextView = itemView.findViewById(R.id.product_price_trend)
        val productImage : ImageView = itemView.findViewById(R.id.product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = inflater.inflate(R.layout.product_item,parent,false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = productList[position]

        holder.productName.text = current.product_name
        holder.productPrice.text = "${current.product_price}₽ "

        if (current.product_image_dir.isNotBlank()){
            Picasso.get().load(current.product_image_dir).into(holder.productImage)
        }

        if(current.product_price_trend>=1){
            holder.productPriceTrend.setTextColor(mContext.getColor(android.R.color.holo_red_dark))
            holder.productPriceTrend.text = "${current.product_price_trend}₽"
        }else{
            holder.productPriceTrend.setTextColor(mContext.getColor(R.color.teal_200))
            holder.productPriceTrend.text = "${current.product_price_trend}₽"
        }

        val dateFormat = DateFormat.format("HH:mm dd.MM.yyyy", current.product_last_checked)
        holder.productDateUpdated.text = "Last updated:$dateFormat"


    }

    fun setProductList(pList:List<Product>){
        this.productList = pList as ArrayList<Product>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}















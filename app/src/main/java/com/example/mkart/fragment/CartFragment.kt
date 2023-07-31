package com.example.mkart.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.mkart.R
import com.example.mkart.activity.AddressActivity
import com.example.mkart.activity.CategoryActivity
import com.example.mkart.adapter.CartAdapter
import com.example.mkart.databinding.FragmentCartBinding
import com.example.mkart.roomdb.AppDatabase
import com.example.mkart.roomdb.ProductModel

class CartFragment : Fragment() {

    private lateinit var binding:FragmentCartBinding
    private lateinit var list:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(layoutInflater)

        val prefrence=requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor=prefrence.edit()
        editor.putBoolean("isCart",false)
        editor.apply()

        val dao=AppDatabase.getInstance(requireContext()).productDao()

        list=ArrayList()

        dao.getAllProducts().observe(requireActivity()){
            binding.cartRecycler.adapter=CartAdapter(requireContext(),it)

            list.clear()
            for(data in it){
                list.add(data.productId)
            }

            totalCost(it)
        }



        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
        var total=0
        for(item in data!!){
            total+=item.productSp!!.toInt( )
        }
        binding.textView13.text="Total item in cart is ${data.size}"
        binding.textView14.text="Total Cost: ${total}"

        binding.checkout.setOnClickListener {

            val intent= Intent(context, AddressActivity::class.java)
            val b=  Bundle()
            b.putStringArrayList("productsId",list)
            b.putString("totalCost",total.toString())
            intent.putExtras(b)


           startActivity(intent)
        }
    }

}
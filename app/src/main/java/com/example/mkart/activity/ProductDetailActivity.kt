package com.example.mkart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mkart.MainActivity
import com.example.mkart.R
import com.example.mkart.databinding.ActivityProductDetailBinding
import com.example.mkart.roomdb.AppDatabase
import com.example.mkart.roomdb.ProductDao
import com.example.mkart.roomdb.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProductDetailBinding.inflate(layoutInflater)

        getProductDetails(intent.getStringExtra("id"))
        
        setContentView(binding.root)

        
    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list=it.get("productImages") as ArrayList<String>
                val name=it.getString("productName")
                val productSp=it.getString("productSp")
                val productDesc=it.getString("productDescription")
                binding.textView5.text=name
                binding.textView8.text=productSp
                binding.textView9.text=productDesc

                val slideList=ArrayList<SlideModel>()
                for(data in list){
                    slideList.add(SlideModel(data,ScaleTypes.CENTER_CROP))
                }

                cartAction(proId,name,productSp,it.getString("productCoverImg"))

                binding.imageSlider.setImageList(slideList)

            }.addOnFailureListener {
                Toast.makeText(this,"Something wrong happened",Toast.LENGTH_LONG).show()
            }

    }

    private fun cartAction(proId: String, name:String?, productSp: String?, coverImg:String?) {
        val productDao=AppDatabase.getInstance(this).productDao()
        if(productDao.isExit(proId)!=null){
            binding.textView10.text="GO TO CART"
       }else{
            binding.textView10.text="ADD TO CART"
        }

        binding.textView10.setOnClickListener {
            if(productDao.isExit(proId)!=null){
               openCart()
            }else{
                addToCart(productDao,proId,name,productSp,coverImg)
            }
        }
    }

    private fun addToCart(productDao: ProductDao, proId: String, name: String?, productSp: String?, coverImg: String?) {

        val data = ProductModel(proId,name,coverImg,productSp)
        lifecycleScope.launch(Dispatchers.IO){
            productDao.insertProduct(data)
            binding.textView10.text="GO TO CART"
        }

    }

    private fun openCart() {
        val prefrence=this.getSharedPreferences("info", MODE_PRIVATE)
        val editor=prefrence.edit()
        editor.putBoolean("isCart",true)
        editor.apply()

        startActivity(Intent(this,MainActivity::class.java))
            finish()

    }
}
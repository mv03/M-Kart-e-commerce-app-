package com.example.mkart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.mkart.MainActivity
import com.example.mkart.R
import com.example.mkart.roomdb.AppDatabase
import com.example.mkart.roomdb.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CheckOutActivity : AppCompatActivity() , PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        val checkout= Checkout()
        checkout.setKeyID("rzp_test_D14keLzeKPFVtp");

        val price=intent.getStringExtra("totalCost")!!
        try {
            val options = JSONObject()
            options.put("name","MKart")
            options.put("description","Best e-commerce app")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#33056B");
            options.put("currency","INR");
            options.put("amount",(price!!.toInt()*100))
            options.put("email","muditvj303@gmail.com")
            options.put("contact","9876543210")
            checkout.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            uploadData()
        }
    }

    private fun uploadData() {
        val id=intent.getStringArrayListExtra("productIds")
        for(currentId in id!!){
            fetchData(currentId)
        }
    }

    private fun fetchData(productId: String?) {

        val dao=AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get().addOnSuccessListener {

                lifecycleScope.launch(Dispatchers.IO){
                    dao.deleteProduct(ProductModel(productId))
                }


                saveData(it.getString("productName"),
                it.getString("productSp"),
               productId
                    )
            }
    }

    private fun saveData(name: String?, price: String?, productId: String) {
        val preferences=this.getSharedPreferences("user", MODE_PRIVATE)
        val data= hashMapOf<String,Any>()
        data["name"]=name!!
        data["price"]=price!!
        data["productId"]=productId
        data["status"]="Ordered"
        data["userId"]=preferences.getString("number","")!!

        val firestore=Firebase.firestore.collection("allOrders")
        val key=firestore.document().id
        data["orderId"]=key

        firestore.add(data).addOnSuccessListener {
            Toast.makeText(this,"Order Placed", Toast.LENGTH_LONG).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show()
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this,"Payment success", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this,"Payment Error ", Toast.LENGTH_LONG).show()
    }
}
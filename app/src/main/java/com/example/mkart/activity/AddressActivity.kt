package com.example.mkart.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mkart.databinding.ActivityAddressBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddressActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAddressBinding
    private lateinit var preferences : SharedPreferences
    private lateinit var totalCost:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddressBinding.inflate(layoutInflater)
        preferences=this.getSharedPreferences("user", MODE_PRIVATE)
        setContentView(binding.root)

        totalCost=intent.getStringExtra("totalCost")!!

        loadUserInfo()

        binding.proceed.setOnClickListener {
            validateData(
                binding.userName.text.toString(),
                binding.userNumber.text.toString(),
                binding.userVillage.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString(),
                binding.userPinCode.text.toString(),
            )
        }
    }

    private fun validateData(
        name: String,
        number: String,
        village: String,
        city: String,
        state: String,
        pinCode: String
    ) {
        if(name.isEmpty() || number.isEmpty() || village.isEmpty() || city.isEmpty() || state.isEmpty() || pinCode.isEmpty()){
            Toast.makeText(this,"Please Fill all the fields",Toast.LENGTH_LONG).show()
        }else{
           storeData(village,city,state,pinCode)
        }
    }

    private fun storeData( village: String, city: String, state: String, pinCode: String) {
        val map= hashMapOf<String,Any>()
        map["village"]=village
        map["city"]=city
        map["state"]=state
        map["pinCode"]=pinCode

        Firebase.firestore.collection(  "users")
            .document(preferences.getString("number","")!!)
            .update(map).addOnSuccessListener {

                val b=  Bundle()
                b.putStringArrayList("productsId",intent.getStringArrayListExtra("productIds"))
                b.putString("totalCost",totalCost)

                val intent= Intent(this, CheckOutActivity::class.java)

                intent.putExtras(b)

                startActivity(intent)


            }.addOnFailureListener {
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show()
            }
    }

    private fun loadUserInfo() {


        Firebase.firestore.collection("users")
            .document(preferences.getString("number","")!!)
            .get().addOnSuccessListener {
                binding.userName.setText(it.getString("userName"))
                binding.userNumber.setText(it.getString("userPhoneNumber"))
                binding.userVillage.setText(it.getString("village"))
                binding.userCity.setText(it.getString("city"))
                binding.userState.setText(it.getString("state"))
                binding.userPinCode.setText(it.getString("pinCode"))
            }
    }
}
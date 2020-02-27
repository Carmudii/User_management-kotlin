package com.user.management

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.eoku.network.RetrofitBuilder
import com.example.eoku.network.serviceApi
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.user.network.getterSetter.Register
import com.user.utils.Email
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var layout_name: TextInputLayout
    private lateinit var layout_email: TextInputLayout
    private lateinit var layout_password: TextInputLayout
    private lateinit var layout_phone: TextInputLayout
    private lateinit var layout_address: TextInputLayout

    private lateinit var edt_name: TextInputEditText
    private lateinit var edt_email: TextInputEditText
    private lateinit var edt_password: TextInputEditText
    private lateinit var radio_gender:RadioGroup
    private lateinit var edt_phone:TextInputEditText
    private lateinit var edt_address:TextInputEditText

    private lateinit var progressBars: ProgressBar

    private lateinit var btn_register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = "Register Management"
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layout_name = findViewById(R.id.layout_name)
        layout_email = findViewById(R.id.layout_email)
        layout_password = findViewById(R.id.layout_password)
        layout_address = findViewById(R.id.layout_address)
        layout_phone = findViewById(R.id.layout_phone)
        edt_name = findViewById(R.id.edt_name)
        edt_email = findViewById(R.id.edt_email)
        radio_gender = findViewById(R.id.radio_gender)
        edt_password = findViewById(R.id.edt_password)
        edt_phone = findViewById(R.id.edt_phone)
        edt_address = findViewById(R.id.edt_address)
        btn_register = findViewById(R.id.btn_register)

        progressBars = findViewById(R.id.progressBars)

        btn_register.setOnClickListener {
            validation()
        }
    }

    private fun validation() {
        if(edt_name.text?.length == 0) {
            layout_name.error = "Name is require"
        } else if(edt_email.text?.length == 0) {
            layout_email.error = "Email is require"
        } else if(edt_password.text?.length == 0) {
            layout_password.error = "Password is require"
        } else if(edt_phone.text?.length == 0) {
            layout_phone.error = "Phone is require"
        } else if(edt_address.text?.length == 0) {
            layout_address.error = "Address is require"
        } else if(edt_name.text!!.length <= 5){
            layout_name.error = "Name must longest 5 character"
        }else if(edt_email.text!!.length <= 5){
            layout_email.error = "Email must longest 5 character"
        }else if(edt_password.text!!.length <= 5){
            layout_password.error = "Password must longest 5 character"
        }else if(edt_phone.text!!.length <= 5){
            layout_phone.error = "Phone must longest 5 character"
        }else if(edt_address.text!!.length <= 5){
            layout_address.error = "Address must longest 5 character"
        }else if(!Email.isValidEmail(edt_email.text.toString())){
            layout_email.error = "Invalid email address"
        }else{
            val gender:RadioButton = findViewById(radio_gender.checkedRadioButtonId)
            Register(edt_name.text.toString(), edt_email.text.toString(), edt_password.text.toString(), edt_phone.text.toString(), gender.text.toString(), edt_address.text.toString())
//            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    fun Register(getName:String,getEmail: String, getPassword: String, getNotlpn:String, getGender:String, getAddress:String) {
        val json = JSONObject()
        json.put("name", getName)
        json.put("email", getEmail)
        json.put("password", getPassword)
        json.put("no_tlpn", getNotlpn)
        json.put("gender", getGender)
        json.put("address", getAddress)

        showLoading()

        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        callApi.register(requestBody).enqueue(object : Callback<Register> {
            override fun onFailure(call: Call<Register>, t: Throwable) {
                hiddeLoading()
                Log.e("FETCH FAIL", t.message.toString())
                Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                hiddeLoading()
                Log.i("FETCH SUCCES", response.body().toString())
                if (response.isSuccessful && response.code() == 200 && response.body()?.status == true) {
                    Toast.makeText(applicationContext, "Register success", Toast.LENGTH_LONG).show()
                    onBackPressed()
                    finish()
                } else {
                    val errResponse = JSONObject(response.errorBody()?.string().toString()).getString("message")
                    Toast.makeText(applicationContext, errResponse, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun showLoading(){
        btn_register.visibility = View.GONE
        progressBars.visibility = View.VISIBLE
    }

    fun hiddeLoading(){
        btn_register.visibility = View.VISIBLE
        progressBars.visibility = View.GONE
    }

}

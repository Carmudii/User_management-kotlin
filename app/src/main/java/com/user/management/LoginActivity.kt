package com.user.management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.eoku.network.RetrofitBuilder
import com.example.eoku.network.serviceApi
import com.example.eoku.storage.sharedPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.user.network.getterSetter.Login
import com.user.utils.Email
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var layout_email: TextInputLayout
    private lateinit var layout_password: TextInputLayout
    private lateinit var edt_email: TextInputEditText
    private lateinit var edt_password: TextInputEditText
    private lateinit var btn_login: Button
    private lateinit var btn_register: Button
    private lateinit var progressBars: ProgressBar

    private lateinit var getSharedPreference: sharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        getSharedPreference = sharedPreferences(this)

        supportActionBar?.title = "Login Management"
        supportActionBar?.elevation = 0f

        layout_email = findViewById(R.id.layout_email)
        layout_password = findViewById(R.id.layout_password)
        edt_email = findViewById(R.id.edt_email)
        edt_password = findViewById(R.id.edt_password)
        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_register)

        progressBars = findViewById(R.id.progressBars)

        btn_login.setOnClickListener {
            validation()
        }

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        if (getSharedPreference.getString(getSharedPreference._token) != "empty"){
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

    }

    private fun validation() {
        if(edt_email.text?.length == 0) {
            layout_email.error = "Email is require"
        } else if(edt_password.text?.length == 0) {
            layout_password.error = "Password is require"
        } else if(edt_email.text!!.length <= 5){
            layout_email.error = "Email must longest 5 character"
        }else if(edt_password.text!!.length <= 5){
            layout_password.error = "Password must longest 5 character"
        }else if(!Email.isValidEmail(edt_email.text.toString())){
            layout_email.error = "Invalid email address"
        }else{
            Login(edt_email.text.toString(), edt_password.text.toString())
//            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }


    fun Login(getEmail: String, getPassword: String) {
        val json = JSONObject()
        json.put("email", getEmail)
        json.put("password", getPassword)

        showLoading()

        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        callApi.login(requestBody).enqueue(object : Callback<Login> {
            override fun onFailure(call: Call<Login>, t: Throwable) {
                hiddeLoading()
                Log.e("FETCH FAIL", t.message.toString())
                Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                hiddeLoading()
                Log.i("FETCH SUCCES", response.body()?.data?.token.toString())
                if (response.isSuccessful && response.code() == 200 && response.body()?.status == true) {
                    getSharedPreference.saveString(getSharedPreference._token, response.body()?.data?.token.toString())

                    Toast.makeText(applicationContext, "Login success", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    finish()
                } else {
                    val errResponse = JSONObject(response.errorBody()?.string().toString()).getString("message")
                    Snackbar.make(edt_email, errResponse, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    fun showLoading(){
        btn_login.visibility = View.GONE
        progressBars.visibility = View.VISIBLE
    }

    fun hiddeLoading(){
        btn_login.visibility = View.VISIBLE
        progressBars.visibility = View.GONE
    }

}

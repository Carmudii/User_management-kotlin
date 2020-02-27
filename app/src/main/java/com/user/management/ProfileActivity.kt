package com.user.management

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.eoku.network.RetrofitBuilder
import com.example.eoku.network.serviceApi
import com.example.eoku.storage.sharedPreferences
import com.squareup.picasso.Picasso
import com.user.network.getterSetter.DefaultResponse
import com.user.network.getterSetter.UserById
import com.user.utils.JWTUtil
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivity : AppCompatActivity() {


    private lateinit var getSharedPreference: sharedPreferences

    private lateinit var img_photo:ImageView
    private lateinit var tv_name: TextView
    private lateinit var account_type: TextView
    private lateinit var tv_email: TextView
    private lateinit var tv_phone: TextView
    private lateinit var tv_gender: TextView
    private lateinit var tv_address: TextView
    private lateinit var bt_update: Button
    private lateinit var bt_delete: Button

    private lateinit var data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Detail Profile"
        supportActionBar?.elevation = 0f

        getSharedPreference = sharedPreferences(this)
        data = JWTUtil.decoded(getSharedPreference.getString(getSharedPreference._token).toString())

        img_photo = findViewById(R.id.img_profile)
        tv_name = findViewById(R.id.tv_name)
        account_type = findViewById(R.id.account_type)
        tv_email = findViewById(R.id.tv_email)
        tv_phone = findViewById(R.id.tv_phone)
        tv_gender = findViewById(R.id.tv_gender)
        tv_address = findViewById(R.id.tv_address)
        bt_update = findViewById(R.id.bt_update)
        bt_delete = findViewById(R.id.bt_delete)

        fetchData()

        bt_update.isEnabled = false
        if (data.getString("role") == "user") {
            tv_name.text = data.getString("name")
            account_type.text = data.getString("role")
            tv_email.text = data.getString("email")
            tv_phone.text = data.getString("phone")
            tv_gender.text = data.getString("gender")
            tv_address.text = data.getString("address")
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        bt_update.setOnClickListener {

            startActivity(Intent(this, ProfileUpActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .apply {
                    putExtra("id", intent.getIntExtra("id", 0))
                    putExtra("name", tv_name.text.toString())
                    putExtra("email", tv_email.text.toString())
                    putExtra("phone", tv_phone.text.toString())
                    putExtra("gender", tv_gender.text.toString())
                    putExtra("address", tv_address.text.toString())
                    putExtra("photo", intent.getStringExtra("photo"))
                    putExtra("role", account_type.text.toString())
                })

        }

        bt_delete.isEnabled = false

        Picasso.get()
            .load(this.intent.getStringExtra("photo"))
            .placeholder(R.drawable.profile_logo)
            .resize(200, 200)
            .centerCrop()
            .into(img_profile)

        if (this.intent.getBooleanExtra("viewPeopleProfile", false)) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            bt_delete.text = "Hapus Akun"
            bt_delete.setOnClickListener {
                onDeleteUser()
            }

            if (data.getInt("id") == this.intent.getIntExtra("id", 0))
                bt_delete.visibility = View.GONE

        } else {
            bt_delete.text = "Logout"
            bt_delete.setOnClickListener {

                getSharedPreference.remove(getSharedPreference._token)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }
        }
    }

    fun fetchData() {
        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val token = "Bearer " + getSharedPreference.getString(getSharedPreference._token).toString()

        callApi.getUserById(token, this.intent.getIntExtra("id", 0)).enqueue(object :
            Callback<UserById> {
            override fun onFailure(call: Call<UserById>, t: Throwable) {
                Log.e("FETCH FAIL", t.message.toString())
                Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UserById>, response: Response<UserById>) {
                if (response.isSuccessful) {
                    tv_name.text = response.body()?.data?.name
                    account_type.text = response.body()?.data?.role
                    tv_email.text = response.body()?.data?.email
                    tv_phone.text = response.body()?.data?.no_tlpn
                    tv_gender.text = response.body()?.data?.gender
                    tv_address.text = response.body()?.data?.address

                    bt_delete.isEnabled = true
                    bt_update.isEnabled = true
                } else {
                    val errResponse =
                        JSONObject(response.errorBody()?.string().toString()).getString("message")
                    if (errResponse == "Unauthorized") {
                        getSharedPreference.remove(getSharedPreference._token)
                        Toast.makeText(applicationContext, "Session Expired", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(
                            Intent(applicationContext, LoginActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    } else {
                        Toast.makeText(applicationContext, errResponse, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun onDeleteUser() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Ingin menghapus user ini?")
            .setCancelable(false)
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialog, _ ->
                DeleteUser()
//                Snackbar.make(bt_delete, "Feature Under Development", Snackbar.LENGTH_SHORT).show()
                dialog.dismiss()
            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
            .show()
    }

    fun DeleteUser() {
        val data =
            JWTUtil.decoded(getSharedPreference.getString(getSharedPreference._token).toString())
        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val token = "Bearer " + getSharedPreference.getString(getSharedPreference._token).toString()

        callApi.delete(token, this.intent.getIntExtra("id", 0)).enqueue(object :
            Callback<DefaultResponse> {
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e("FETCH FAIL", t.message.toString())
                Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if (response.isSuccessful) {
                    bt_delete.isEnabled = false
                    bt_update.isEnabled = false

                    if (data.getString("role") == "user") {
                        getSharedPreference.remove(getSharedPreference._token)
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        finish()
                    } else {
                        onBackPressed()
                    }
                } else {
                    val errResponse =
                        JSONObject(response.errorBody()?.string().toString()).getString("message")
                    if (errResponse == "Unauthorized") {
                        getSharedPreference.remove(getSharedPreference._token)
                        Toast.makeText(applicationContext, "Session Expired", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(
                            Intent(applicationContext, LoginActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    } else {
                        Toast.makeText(applicationContext, errResponse, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

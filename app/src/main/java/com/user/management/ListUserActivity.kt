package com.user.management

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eoku.network.RetrofitBuilder
import com.example.eoku.network.serviceApi
import com.example.eoku.storage.sharedPreferences
import com.user.network.getterSetter.AllUsers
import com.user.utils.Adapters.ListUserAdapter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListUserActivity : AppCompatActivity() {

    private lateinit var rv_listUser: RecyclerView
    private lateinit var getSharedPreference: sharedPreferences
    lateinit var itemsswipetorefresh: SwipeRefreshLayout

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user)

        context = this
        getSharedPreference = sharedPreferences(this)

        supportActionBar?.title = "List User Management"
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemsswipetorefresh = findViewById(R.id.itemsswipetorefresh)
        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)
        itemsswipetorefresh.setOnRefreshListener {
            fetchData()
            itemsswipetorefresh.isRefreshing = true
        }

        rv_listUser = findViewById(R.id.rv_listUser)
        rv_listUser.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        fetchData()
    }

    fun fetchData() {
        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val token = "Bearer "+getSharedPreference.getString(getSharedPreference._token).toString();

        callApi.getAllUser(token).enqueue(object : Callback<AllUsers> {
            override fun onFailure(call: Call<AllUsers>, t: Throwable) {
                itemsswipetorefresh.isRefreshing = false
                Log.e("FETCH FAIL", t.message.toString())
                Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<AllUsers>, response: Response<AllUsers>) {
                itemsswipetorefresh.isRefreshing = false
                if(response.isSuccessful) {
                    val data = response.body()?.data
                    rv_listUser.adapter = ListUserAdapter(context, data!!)
                } else {
                    val errResponse = JSONObject(response.errorBody()?.string().toString()).getString("message")
                    if (errResponse == "Unauthorized") {
                        getSharedPreference.remove(getSharedPreference._token)
                        Toast.makeText(applicationContext, "Session Expired", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(
                            Intent(applicationContext, LoginActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }else{
                        Toast.makeText(applicationContext, errResponse, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        fetchData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

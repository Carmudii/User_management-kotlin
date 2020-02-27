package com.user.management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.eoku.network.RetrofitBuilder
import com.example.eoku.storage.sharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.user.utils.JWTUtil
import org.json.JSONObject

class DashboardActivity : AppCompatActivity() {

    private lateinit var listUser: CardView
    private lateinit var addUser: CardView
    private lateinit var profileAdmin: CardView
    private lateinit var logout: FloatingActionButton

    private lateinit var getSharedPreference: sharedPreferences
    private lateinit var data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        getSharedPreference = sharedPreferences(this)
        data = JWTUtil.decoded(getSharedPreference.getString(getSharedPreference._token).toString())

        supportActionBar?.title = "Dashboard Management"
        supportActionBar?.elevation = 0f

        listUser = findViewById(R.id.cv_listUser)
        addUser = findViewById(R.id.cv_addUser)
        profileAdmin = findViewById(R.id.cv_profileAdmin)
        logout = findViewById(R.id.logout)

        listUser.setOnClickListener {

            startActivity(Intent(this, ListUserActivity::class.java))

        }

        addUser.setOnClickListener {

            startActivity(Intent(this, DashboardRegisterActivity::class.java))
//            Snackbar.make(listUser, "Feature Under Development", Snackbar.LENGTH_SHORT)
//                .show()

        }

        profileAdmin.setOnClickListener {
            val id = data.getInt("id")
            val urlPhoto = RetrofitBuilder.BASE_URL_PORT + "/images/"+data.getString("photo")

            startActivity(Intent(this, ProfileActivity::class.java)
                .apply {
                    putExtra("id", id)
                    putExtra("photo", urlPhoto)
                })
        }

        logout.setOnClickListener {
            Toast.makeText(this,"Logout Successfully..", Toast.LENGTH_LONG).show()
            getSharedPreference.remove(getSharedPreference._token)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        if(data.getString("role") != "admin") {
            val getData = data
            val id = data.getInt("id")
            startActivity(Intent(this, ProfileActivity::class.java)
                .apply {
                    putExtra("id", id)
                    putExtra("photo", RetrofitBuilder.BASE_URL_PORT+"/images/"+getData.getString("photo"))
                }
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

            finish()
        }
    }
}

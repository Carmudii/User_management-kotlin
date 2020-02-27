package com.user.utils.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eoku.network.RetrofitBuilder
import com.squareup.picasso.Picasso
import com.user.management.ProfileActivity
import com.user.management.R
import com.user.utils.AdapterModels.ListUserModel

class ListUserAdapter(val context : Context, var list: ArrayList<ListUserModel>) : RecyclerView.Adapter<ListUserAdapter.ViewHolder>() {

    override fun onBindViewHolder(v: ViewHolder, position: Int) {
        var path_mages = RetrofitBuilder.BASE_URL_PORT +"/images/"+ list[position].photo
        if (list[position].role == "admin"){
            v.tv_name.setTextColor(Color.RED)
        }
        v.tv_name.text = list[position].name
        v.tv_email.text = list[position].email

        Picasso.get()
            .load(path_mages)
            .placeholder(R.drawable.profile_logo)
            .resize(200, 200)
            .centerCrop()
            .into(v.photo)

        v.item.setOnClickListener {
            context.startActivity(Intent(context, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .apply {
                    putExtra("id", list[position].id)
                    putExtra("viewPeopleProfile", true)
                    putExtra("photo", path_mages)
                })
        }
    }

    override fun onCreateViewHolder(View: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(View.context).inflate(R.layout.item_user_list, View, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val photo: ImageView = v.findViewById(R.id.img_profile)
        val tv_name: TextView = v.findViewById(R.id.tv_name)
        val tv_email: TextView = v.findViewById(R.id.tv_email)
        val item: LinearLayout = v.findViewById(R.id.item_user)
    }
}
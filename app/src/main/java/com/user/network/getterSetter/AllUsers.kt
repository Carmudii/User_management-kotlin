package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName
import com.user.utils.AdapterModels.ListUserModel

class AllUsers(status: Boolean=false, message: String="", data: ArrayList<ListUserModel>) {

    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message
    @SerializedName("data")
    var data: ArrayList<ListUserModel> = data
}
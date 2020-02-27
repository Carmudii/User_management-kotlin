package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName
import com.user.model.User

class Register(status: Boolean=false, message: String="", data: User) {

    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message
    @SerializedName("data")
    var data: User? = data
}

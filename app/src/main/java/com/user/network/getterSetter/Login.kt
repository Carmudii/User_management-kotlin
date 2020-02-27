package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName
import com.user.model.Jwt

class Login(status: Boolean=false, message: String="", data: Jwt) {

    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message
    @SerializedName("data")
    var data: Jwt? = data
}
package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName
import com.user.model.Data

class UserById(status: Boolean=false, message: String="", data: Data) {

    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message
    @SerializedName("data")
    var data: Data? = data
}

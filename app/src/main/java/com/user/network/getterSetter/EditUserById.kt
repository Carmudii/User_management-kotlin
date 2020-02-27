package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName

class EditUserById(status: Boolean=false, message: String="") {

    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message

}

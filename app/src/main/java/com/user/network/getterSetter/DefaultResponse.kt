package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class DefaultResponse(status: Boolean=false, message: String="", data:JSONObject) {

    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message
    @SerializedName("data")
    var data: JSONObject? = data
}
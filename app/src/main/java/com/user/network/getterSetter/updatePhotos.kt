package com.user.network.getterSetter

import com.google.gson.annotations.SerializedName

class updatePhotos(status:Boolean, message:String, data:String){
    @SerializedName("status")
    var status: Boolean? = status
    @SerializedName("message")
    var mesage: String? = message
    @SerializedName("data")
    var data: String? = data
}
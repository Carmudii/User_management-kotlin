package com.user.management

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.eoku.network.RetrofitBuilder
import com.example.eoku.network.serviceApi
import com.example.eoku.storage.sharedPreferences
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import com.user.network.getterSetter.EditUserById
import com.user.network.getterSetter.updatePhotos
import com.user.utils.Email
import com.user.utils.FileUtil
import com.user.utils.JWTUtil
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileUpActivity : AppCompatActivity() {

    private var GALLERY_REQUEST_CODE = 10
    private var CAMERA_REQUEST_CODE = 11
    private var cameraFilePath: String? = null
    private var selectImage: Uri? = null
    private var idUser: String? = null

    private lateinit var profile: CardView
    private lateinit var img_profile: ImageView
    private lateinit var layout_name: TextInputLayout
    private lateinit var layout_email: TextInputLayout
    private lateinit var layout_password: TextInputLayout
    private lateinit var layout_phone: TextInputLayout
    private lateinit var layout_address: TextInputLayout
    private lateinit var radiouLayout:LinearLayout
    private lateinit var edt_name: TextInputEditText
    private lateinit var edt_email: TextInputEditText
    private lateinit var edt_password: TextInputEditText
    private lateinit var edt_phone: TextInputEditText
    private lateinit var edt_address: TextInputEditText
    private lateinit var radio_type:RadioGroup
    private lateinit var radio_admin:RadioButton
    private lateinit var radio_user:RadioButton
    private lateinit var progressBars: ProgressBar
    private lateinit var btn_update: Button
    private lateinit var getSharedPreference: sharedPreferences
    private lateinit var data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_up)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            Toast.makeText(this, "Permission storage is not granted", Toast.LENGTH_LONG).show()
            onBackPressed()
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            Toast.makeText(this, "Permission camera is not granted", Toast.LENGTH_LONG).show()
            onBackPressed()
        }

        supportActionBar?.title = "Register Management"
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getSharedPreference = sharedPreferences(this)
        data = JWTUtil.decoded(getSharedPreference.getString(getSharedPreference._token).toString())

        profile = findViewById(R.id.profile)
        img_profile = findViewById(R.id.img_profile)
        layout_name = findViewById(R.id.layout_name)
        layout_email = findViewById(R.id.layout_email)
        layout_password = findViewById(R.id.layout_password)
        layout_address = findViewById(R.id.layout_address)
        layout_phone = findViewById(R.id.layout_phone)
        edt_name = findViewById(R.id.edt_name)
        edt_email = findViewById(R.id.edt_email)
        edt_password = findViewById(R.id.edt_password)
        edt_phone = findViewById(R.id.edt_phone)
        edt_address = findViewById(R.id.edt_address)
        btn_update = findViewById(R.id.btn_update)
        progressBars = findViewById(R.id.progressBars)
        radiouLayout = findViewById(R.id.radioLayout)
        radio_type = findViewById(R.id.radio_type)
        radio_user = findViewById(R.id.radio_user)
        radio_admin = findViewById(R.id.radio_admin)

        Picasso.get()
            .load(this.intent.getStringExtra("photo"))
            .placeholder(R.drawable.profile_logo)
            .resize(200, 200)
            .centerCrop()
            .into(img_profile)

        profile.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            val animals = arrayOf("Galery", "Camera")
            builder.setItems(animals) { dialog, which ->
                when (which) {
                    0 -> { /* Galery */
                        pickFromGallery()
                    }
                    1 -> { /* Camera */
                        pickFromCamera()
                    }
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        btn_update.setOnClickListener {
            if (cameraFilePath != null) {
                uploadPhoto()
            }
            validation()
        }
    }

    private fun validation() {
        if (edt_name.text?.length == 0) {
            layout_name.error = "Name is require"
        } else if (edt_email.text?.length == 0) {
            layout_email.error = "Email is require"
        } else if (edt_phone.text?.length == 0) {
            layout_phone.error = "Phone is require"
        } else if (edt_address.text?.length == 0) {
            layout_address.error = "Address is require"
        } else if (edt_name.text!!.length <= 5) {
            layout_name.error = "Name must longest 5 character"
        } else if (edt_email.text!!.length <= 5) {
            layout_email.error = "Email must longest 5 character"
        } else if (edt_address.text!!.length <= 5) {
            layout_address.error = "Address must longest 5 character"
        } else if (edt_phone.text!!.length <= 8) {
            layout_phone.error = "Phone must longest 8 character"
        } else if (!Email.isValidEmail(edt_email.text.toString())) {
            layout_email.error = "Email must longest 5 character"
        } else if (edt_password.text!!.isNotEmpty()) {
            if (edt_password.text!!.length <= 5) {
                layout_password.error = "Password must longest 5 character"
            } else {
                val accountType:RadioButton = findViewById(radio_type.checkedRadioButtonId)
                UpdateAll(
                    edt_name.text.toString(),
                    edt_email.text.toString(),
                    edt_password.text.toString(),
                    edt_phone.text.toString(),
                    intent.getStringExtra("gender")!!.toString(),
                    edt_address.text.toString(),
                    accountType.text.toString()
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(
            Intent(this, ListUserActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        return true
    }


    fun UpdateAll(
        getName: String,
        getEmail: String,
        getPassword: String,
        getNotlpn: String,
        getGender: String,
        getAddress: String,
        getAccountType:String
    ) {
        showLoading()

        val json = JSONObject()
        json.put("name", getName)
        json.put("email", getEmail)
        json.put("password", getPassword)
        json.put("no_tlpn", getNotlpn)
        json.put("gender", getGender)
        json.put("address", getAddress)
        json.put("role", getAccountType)

        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val requestBody: RequestBody =
            RequestBody.create(MediaType.parse("application/json"), json.toString())
        val token =
            "Bearer " + getSharedPreference.getString(getSharedPreference._token).toString();

        callApi.editUserById(token, intent.getIntExtra("id", 0), requestBody)
            .enqueue(object : Callback<EditUserById> {
                override fun onFailure(call: Call<EditUserById>, t: Throwable) {
                    hiddeLoading()
                    Log.e("FETCH FAIL", t.message.toString())
                    Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG).show()
                }


                override fun onResponse(
                    call: Call<EditUserById>,
                    response: Response<EditUserById>
                ) {
                    hiddeLoading()
                    Log.i("FETCH SUCCES", response.body().toString())
                    if (response.isSuccessful && response.code() == 200 && response.body()?.status == true) {
                        if (data.getInt("id") == intent.getIntExtra("id", 0)) {
                            Toast.makeText(
                                applicationContext,
                                "Success, you must login again",
                                Toast.LENGTH_SHORT
                            ).show()
                            getSharedPreference.remove(getSharedPreference._token)
                            startActivity(
                                Intent(applicationContext, LoginActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "success edit profile",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(applicationContext, ListUserActivity::class.java))
                            finish()
                        }
                    } else {
                        val errResponse =
                            JSONObject(response.errorBody()?.string().toString()).getString("message")
                        Toast.makeText(applicationContext, errResponse, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    fun uploadPhoto() {
        showLoading()

        val retrofit = RetrofitBuilder.create()
        val callApi = retrofit.create(serviceApi::class.java)
        val token =
            "Bearer " + getSharedPreference.getString(getSharedPreference._token).toString();

        val photoProfile = MultipartBody.Part.createFormData(
            "photo",
            File(cameraFilePath!!).name,
            RequestBody.create(MediaType.parse("images/*"), File(cameraFilePath!!))
        )
        callApi.updatePhoto(token, idUser.toString(), photoProfile)
            .enqueue(object : Callback<updatePhotos> {
                override fun onFailure(call: Call<updatePhotos>, t: Throwable) {
                    hiddeLoading()
                    Log.e("FETCH FAIL", t.message.toString())
                    Toast.makeText(applicationContext, "Gagal ke server!", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<updatePhotos>,
                    response: Response<updatePhotos>
                ) {
                    hiddeLoading()
                    Log.i("FETCH SUCCES", response.body().toString())
                    if (response.isSuccessful && response.code() == 200 && response.body()?.status == true) {
                        Toast.makeText(
                            applicationContext,
                            "Photo was successfully update",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errResponse =
                            JSONObject(response.errorBody()?.string().toString()).getString("message")
                        if (errResponse == "Unauthorized") {
                            getSharedPreference.remove(getSharedPreference._token)
                            Toast.makeText(
                                applicationContext,
                                "Session Expired",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            startActivity(
                                Intent(applicationContext, LoginActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        } else {
                            Toast.makeText(applicationContext, errResponse, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }

            })
    }

    override fun onStart() {
        super.onStart()

        idUser = this.intent.getIntExtra("id", 0)?.toString()
        edt_name.setText(this.intent.getStringExtra("name")?.toString())
        edt_email.setText(this.intent.getStringExtra("email")?.toString())
        edt_phone.setText(this.intent.getStringExtra("phone")?.toString())
        edt_address.setText(this.intent.getStringExtra("address")?.toString())
        val accountType = this.intent.getStringExtra("role")?.toString()
        if(accountType == "user"){
            radiouLayout.visibility = View.GONE
        }else{
            radio_user.isChecked = true
            radio_admin.isChecked = false
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir     /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        cameraFilePath = image.absolutePath
        return image
    }


    private fun pickFromGallery() { //Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun pickFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                createImageFile()
            )
        )
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                //data.getData returns the content URI for the selected Image
                selectImage = data?.data
                cameraFilePath = FileUtil.getRealPathFromURI(selectImage!!, this).toString()
                img_profile.setImageURI(selectImage)
            }
            CAMERA_REQUEST_CODE -> {
                // camera
                img_profile.setImageURI(Uri.parse(cameraFilePath));
            }
        }
    }


    fun showLoading() {
        btn_update.visibility = View.GONE
        progressBars.visibility = View.VISIBLE
    }

    fun hiddeLoading() {
        btn_update.visibility = View.VISIBLE
        progressBars.visibility = View.GONE
    }
}

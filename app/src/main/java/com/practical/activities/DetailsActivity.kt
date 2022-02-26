package com.practical.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.practical.R
import com.practical.databinding.ActivityDetailsBinding
import com.practical.dbHelper.UsersRepository
import com.practical.models.UserDetails
import com.practical.utils.ImageFilePath
import com.practical.utils.Utils
import java.io.File


class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var usersRepository: UsersRepository? = null
    private var mImagePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()
    }

    private fun initialization() {
        usersRepository = UsersRepository(this)

        val data = intent?.extras
        if (data != null) {
            val firstName = data.getString(Utils.FIRST_NAME)
            val lastName = data.getString(Utils.LAST_NAME)
            val address = data.getString(Utils.ADDRESS)
            val phoneNumber = data.getString(Utils.MOBILE_NUMBER)
            val profileImagePath = data.getString(Utils.PROFILE_IMAGE)
            val editAction = data.getString(Utils.EDIT_ACTION)

            if (!firstName.isNullOrEmpty()){
                binding.edtFirstName.setText(firstName)
            }

            if (!lastName.isNullOrEmpty()) {
                binding.edtLastName.setText(lastName)
            }

            if (!address.isNullOrEmpty()) {
                updateLocation(address)
            }

            if (!phoneNumber.isNullOrEmpty()) {
                binding.edtMobileNumber.setText(phoneNumber)
            }

            if (!profileImagePath.isNullOrEmpty()) {
                mImagePath = profileImagePath
                Glide.with(this).load(File(profileImagePath)).placeholder(R.drawable.ic_user).into(binding.imgProfile)
            }

            if (!editAction.isNullOrEmpty() && editAction == Utils.EDIT_ACTION) {
                binding.btnSave.text = editAction
            } else {
                binding.btnSave.text = getString(R.string.str_save)
            }
        }

        binding.btnSave.setOnClickListener {
            saveUserDetails()
        }

        binding.imgLocation.setOnClickListener { chooseLocation() }

        binding.imgProfile.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission()
            } else {
                chooseImage()
            }
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Utils.READ_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.READ_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseImage()
        } else {
            requestStoragePermission()
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageRequestLauncher.launch(Intent.createChooser(intent,"Choose Profile Image"))

    }

    private var imageRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                mImagePath = uri?.let { ImageFilePath.getPath(this, it) }.toString()
                updateProfileImage(mImagePath)
            }
        }

    private fun updateProfileImage(mImagePath: String) {
        Glide.with(this)
            .load(File(mImagePath))
            .placeholder(R.drawable.ic_user)
            .into(binding.imgProfile)
    }

    private fun chooseLocation() {
        val intent = Intent(this@DetailsActivity, MapsActivity::class.java)
        intentLauncher.launch(intent)
    }

    private var intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val location = data?.extras?.getString(Utils.ADDRESS)
                updateLocation(location)
            }
        }

    private fun updateLocation(location: String?) {
        binding.edtAddress.setText(location)
    }

    private fun saveUserDetails() {
        when {
            binding.edtFirstName.text.toString() == "" -> {
                Toast.makeText(
                    this,
                    "Please enter first name",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.edtLastName.text.toString() == "" -> {
                Toast.makeText(
                    this,
                    "Please enter last name",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.edtAddress.text.toString() == "" -> {
                Toast.makeText(this, "Please select address.", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.edtMobileNumber.text.toString() == "" -> {
                Toast.makeText(
                    this,
                    "Please enter mobile number.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.edtMobileNumber.text.toString().length != 10 -> {
                Toast.makeText(
                    this,
                    "Please enter valid mobile number.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val firstName = binding.edtFirstName.text.toString()
                val lastName = binding.edtLastName.text.toString()
                val address = binding.edtAddress.text.toString()
                val mobileNumber = binding.edtMobileNumber.text.toString()

                val user = UserDetails(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = mobileNumber,
                    address = address,
                    profileImagePath = mImagePath
                )
                if (binding.btnSave.text == Utils.EDIT_ACTION){
                    usersRepository?.updateUser(user)
                } else {
                    usersRepository?.insertUser(user)
                }

                showAlertDialog()
            }
        }

    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialogTitle)
        builder.setMessage(if (binding.btnSave.text == Utils.EDIT_ACTION) R.string.dialogMessageUpdate else R.string.dialogMessage)

        builder.setPositiveButton("Okay") { _, _ ->
            openListingScreen()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun openListingScreen() {
        startActivity(Intent(this@DetailsActivity, MainActivity::class.java))
        finishAffinity()
    }
}
package com.practical.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.practical.adapters.UserListAdapter
import com.practical.databinding.ActivityMainBinding
import com.practical.dbHelper.UsersRepository
import com.practical.listeners.ItemClickListener
import com.practical.models.UserDetails
import com.practical.utils.Utils

class MainActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var mAdapter : UserListAdapter? = null
    private var mUserList : List<UserDetails> = listOf()
    private var usersRepository: UsersRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usersRepository = UsersRepository(this)
        // if no data available the redirect user to maps screen
        if (usersRepository?.getAllUsers().isNullOrEmpty()) {
            startActivity(Intent(this@MainActivity,MapsActivity::class.java))
            finish()
        }


        initialization()

        setListeners()

    }

    private fun setListeners() {
        binding.txtNewLocation.setOnClickListener {
            startActivity(Intent(this,MapsActivity::class.java))
        }
    }

    private fun initialization() {

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        mAdapter = UserListAdapter(this,mUserList,this)
        binding.rvUsers.adapter = mAdapter

        getAllUsersList()
    }

    private fun getAllUsersList() {
        mUserList = usersRepository?.getAllUsers() ?: listOf()
        if (!mUserList.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onEditClick(position: Int) {
        val user = mUserList[position]
        val intent = Intent(this,DetailsActivity::class.java)
        intent.putExtra(Utils.FIRST_NAME,user.firstName)
        intent.putExtra(Utils.LAST_NAME,user.lastName)
        intent.putExtra(Utils.ADDRESS,user.address)
        intent.putExtra(Utils.MOBILE_NUMBER,user.phoneNumber)
        intent.putExtra(Utils.PROFILE_IMAGE,user.profileImagePath)
        intent.putExtra(Utils.EDIT_ACTION,Utils.EDIT_ACTION)
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {

        usersRepository?.deleteUser(mUserList[position])
        mUserList = listOf()
        mAdapter = null
        getAllUsersList()
    }
}
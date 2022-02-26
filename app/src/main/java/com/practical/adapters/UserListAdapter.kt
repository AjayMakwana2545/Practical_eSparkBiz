package com.practical.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practical.R
import com.practical.listeners.ItemClickListener
import com.practical.models.UserDetails
import java.io.File
import java.util.*

class UserListAdapter(var mContext : Context, var mUserList: List<UserDetails>, var mListener: ItemClickListener) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val imgEdit: ImageView = itemView.findViewById(R.id.imgEdit)
        val imgDelete: ImageView = itemView.findViewById(R.id.imgDelete)
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtPhoneNumber: TextView = itemView.findViewById(R.id.txtMobileNumber)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_user_details, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUserList[position]

        val username = "${user.firstName} ${user.lastName}"
        holder.txtUserName.text = username

        holder.txtAddress.text = user.address
        holder.txtPhoneNumber.text = user.phoneNumber

        user.profileImagePath?.let { Glide.with(mContext).load(File(it)).placeholder(R.drawable.ic_user).into(holder.imgProfile) }

        holder.imgEdit.setOnClickListener { mListener.onEditClick(position) }
        holder.imgDelete.setOnClickListener { mListener.onDeleteClick(position) }

    }

    override fun getItemCount(): Int {
        return mUserList.size
    }
}
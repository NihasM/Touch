package com.divinetechs.touch.chatlist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.divinetechs.touch.R
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(val context: Context,val userList: ArrayList<Users>):
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)

        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int {

        return userList.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val currentUser= userList[position]
        holder.textName.text=currentUser.name
// Load image using Glide
        /*Glide.with(context)
            .load(currentUser.imageUrl) // assuming you have a field for profile image URL in Users class
            .placeholder(R.drawable.iconprofile) // optional placeholder while loading
            .error(R.drawable.iconprofile) // optional error placeholder
            //.transition(DrawableTransitionOptions.withCrossFade()) // optional transition
            .into(holder.imgProfile)
*/

    }

    class UsersViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val textName = itemView.findViewById<TextView>(R.id.userName)
        val imgProfile = itemView.findViewById<CircleImageView>(R.id.imgprofile)
    }



}
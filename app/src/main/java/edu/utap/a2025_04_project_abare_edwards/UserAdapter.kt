package edu.utap.a2025_04_project_abare_edwards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.a2025_04_project_abare_edwards.database.User

class UserAdapter(
    private var users: List<Pair<String, User>>,
    private val onClick: (String, User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText = view.findViewById<TextView>(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val (uid, user) = users[position]
        holder.nameText.text = user.name
        holder.itemView.setOnClickListener {
            onClick(uid, user)
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateList(newList: List<Pair<String, User>>) {
        users = newList
        notifyDataSetChanged()
    }
}

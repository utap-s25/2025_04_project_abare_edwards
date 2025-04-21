package edu.utap.a2025_04_project_abare_edwards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.a2025_04_project_abare_edwards.database.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GlobalFeedAdapter(
    private val transactions: List<Transaction>,
    private val uidToName: Map<String, String>
) : RecyclerView.Adapter<GlobalFeedAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val detail = itemView.findViewById<TextView>(R.id.txnDetail)
        private val comment = itemView.findViewById<TextView>(R.id.txnComment)
        private val amount = itemView.findViewById<TextView>(R.id.txnAmount)
        private val timestamp = itemView.findViewById<TextView>(R.id.txnTimestamp)
        private val lock = itemView.findViewById<ImageView>(R.id.lockIcon)


        fun bind(transaction: Transaction) {
            val senderName = uidToName[transaction.senderId] ?: "Unknown"
            val receiverName = uidToName[transaction.receiverId] ?: "Unknown"

            detail.text = "$senderName paid $receiverName"
            comment.text = transaction.comment
            amount.text = "$%.2f".format(transaction.amount)
            timestamp.text = formatTimestamp(transaction.timestamp?.toDate())
            lock.visibility = if (transaction.locked) View.VISIBLE else View.GONE
        }

        private fun formatTimestamp(date: Date?): String {
            if (date == null) return ""
            val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
            return sdf.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }
}
package edu.utap.a2025_04_project_abare_edwards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.a2025_04_project_abare_edwards.database.Transaction

class GlobalFeedAdapter(
    private val transactions: List<Transaction>,
    private val uidToName: Map<String, String>
) : RecyclerView.Adapter<GlobalFeedAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderText: TextView = itemView.findViewById(R.id.senderText)
        private val receiverText: TextView = itemView.findViewById(R.id.receiverText)
        private val amountText: TextView = itemView.findViewById(R.id.amountText)
        private val commentText: TextView = itemView.findViewById(R.id.commentText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)

        fun bind(transaction: Transaction) {
            val senderName = uidToName[transaction.senderId] ?: transaction.senderId
            val receiverName = uidToName[transaction.receiverId] ?: transaction.receiverId

            senderText.text = "From: $senderName"
            receiverText.text = "To: $receiverName"
            amountText.text = "$${transaction.amount}"
            commentText.text = transaction.comment
            timestampText.text = transaction.timestamp?.toDate().toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.global_feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }
}
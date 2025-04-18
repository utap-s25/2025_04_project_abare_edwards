package edu.utap.a2025_04_project_abare_edwards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.a2025_04_project_abare_edwards.database.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val currentUid: String,
    private val uidToName: Map<String, String>
) : RecyclerView.Adapter<TransactionAdapter.TxnViewHolder>() {

    class TxnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val detail = itemView.findViewById<TextView>(R.id.txnDetail)
        val comment = itemView.findViewById<TextView>(R.id.txnComment)
        val amount = itemView.findViewById<TextView>(R.id.txnAmount)
        val timestamp = itemView.findViewById<TextView>(R.id.txnTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TxnViewHolder(view)
    }

    override fun onBindViewHolder(holder: TxnViewHolder, position: Int) {
        val txn = transactions[position]
        val youAreSender = txn.senderId == currentUid
        val otherPartyId = if (youAreSender) txn.receiverId else txn.senderId
        val otherPartyName = uidToName[otherPartyId] ?: "Unknown"
        val direction = if (youAreSender) "You paid" else "Received from"

        holder.detail.text = "$direction $otherPartyName"
        holder.comment.text = txn.comment
        holder.amount.text = "$%.2f".format(txn.amount)
        holder.timestamp.text = formatTimestamp(txn.timestamp?.toDate())
    }

    private fun formatTimestamp(date: Date?): String {
        if (date == null) return ""
        val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    override fun getItemCount(): Int = transactions.size
}

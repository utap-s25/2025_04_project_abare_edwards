package edu.utap.a2025_04_project_abare_edwards.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object TransactionStore {

    private val liveTransactionsStore = MutableLiveData<List<Transaction>>()

    val liveTransactions: LiveData<List<Transaction>> get() = liveTransactionsStore

    fun init() {
        FirebaseFirestore.getInstance()
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val transactions = snapshot.documents.mapNotNull {
                        it.toObject(Transaction::class.java)
                    }
                    liveTransactionsStore.postValue(transactions)
                }
            }
    }

    fun anonymizeUserTransactions(userId: String, onComplete: (() -> Unit)? = null) {
        val transactionsTable = FirebaseFirestore.getInstance().collection("transactions")

        transactionsTable
            .whereEqualTo("senderId", userId)
            .get()
            .addOnSuccessListener {
                for (transaction in it) {
                    transactionsTable.document(transaction.id).update("senderId", "deletedUser")
                }
            }
        transactionsTable
            .whereEqualTo("receiverId", userId)
            .get()
            .addOnSuccessListener {
                for (transaction in it) {
                    transactionsTable.document(transaction.id).update("receiverId", "deletedUser")
                }
                onComplete?.invoke()
            }
    }
}
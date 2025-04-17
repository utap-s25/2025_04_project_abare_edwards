package edu.utap.a2025_04_project_abare_edwards

import com.google.firebase.Timestamp

data class Transaction(
    val senderId: String = "",
    val receiverId: String = "",
    val amount: Double = 0.0,
    val comment: String = "",
    val timestamp: Timestamp? = null
)
package edu.utap.a2025_04_project_abare_edwards.database

data class Transaction(
    val senderId: String = "",
    val receiverId: String = "",
    val amount: Double = 0.0,
    val comment: String = "",
    val timestamp: Long = 0L
)
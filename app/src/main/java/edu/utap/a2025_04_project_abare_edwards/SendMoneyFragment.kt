package edu.utap.a2025_04_project_abare_edwards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.a2025_04_project_abare_edwards.databinding.FragmentSendMoneyBinding
import java.util.*

class SendMoneyFragment(private val receiverUid: String, private val receiverName: String) : Fragment() {

    private var _binding: FragmentSendMoneyBinding? = null
    private val binding get() = _binding!!

    private var amountString = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendMoneyBinding.inflate(inflater, container, false)

        binding.name.text = receiverName
        updateAmount()

        val keypadIds = listOf(
            binding.button0, binding.button1, binding.button2,
            binding.button3, binding.button4, binding.button5,
            binding.button6, binding.button7, binding.button8,
            binding.button9, binding.buttonDot
        )

        for (button in keypadIds) {
            button.setOnClickListener {
                amountString += button.text
                updateAmount()
            }
        }

        binding.buttonBackspace.setOnClickListener {
            if (amountString.isNotEmpty()) {
                amountString = amountString.dropLast(1)
                updateAmount()
            }
        }

        binding.buttonSend.setOnClickListener {
            sendTransaction()
        }

        return binding.root
    }

    private fun updateAmount() {
        binding.amount.text = if (amountString.isBlank()) "$0" else "$$amountString"
    }

    private fun sendTransaction() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val senderUid = auth.currentUser?.uid ?: return
        val comment = binding.comment.text.toString()
        val amount = amountString.toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            binding.amount.error = "Enter a valid amount"
            return
        }

        db.collection("users").document(senderUid).get()
            .addOnSuccessListener { senderDoc ->
                val senderBalance = senderDoc.getDouble("balance") ?: 0.0
                if (senderBalance < amount) {
                    Toast.makeText(requireContext(), "Insufficient balance", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                db.collection("users").document(senderUid)
                    .update("balance", senderBalance - amount)
                    .addOnSuccessListener {

                        db.collection("users").document(receiverUid).get()
                            .addOnSuccessListener { receiverDoc ->
                                val receiverBalance = receiverDoc.getDouble("balance") ?: 0.0
                                val newReceiverBalance = receiverBalance + amount

                                db.collection("users").document(receiverUid)
                                    .update("balance", newReceiverBalance)
                                    .addOnSuccessListener {

                                        val txn = hashMapOf(
                                            "senderId" to senderUid,
                                            "receiverId" to receiverUid,
                                            "amount" to amount,
                                            "comment" to comment,
                                            "timestamp" to com.google.firebase.Timestamp.now()
                                        )

                                        db.collection("transactions")
                                            .add(txn)
                                            .addOnSuccessListener {
                                                amountString = ""
                                                binding.comment.setText("")
                                                updateAmount()
                                                binding.name.text = "Sent to $receiverName"
                                                Toast.makeText(requireContext(), "Payment sent to $receiverName", Toast.LENGTH_SHORT).show()
                                                parentFragmentManager.popBackStack()
                                            }
                                            .addOnFailureListener {
                                                binding.name.text = "Transaction failed"
                                            }
                                    }
                                    .addOnFailureListener {
                                        binding.name.text = "Failed to credit receiver"
                                    }
                            }
                            .addOnFailureListener {
                                binding.name.text = "Receiver not found"
                            }
                    }
                    .addOnFailureListener {
                        binding.name.text = "Failed to update sender"
                    }
            }
            .addOnFailureListener {
                binding.name.text = "Could not fetch sender info"
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

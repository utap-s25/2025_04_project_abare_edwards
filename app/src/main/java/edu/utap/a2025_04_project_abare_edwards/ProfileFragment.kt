package edu.utap.a2025_04_project_abare_edwards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.utap.a2025_04_project_abare_edwards.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("name") ?: "Unknown"
                    val balance = doc.getDouble("balance") ?: 0.0

                    binding.profileName.text = name
                    binding.profileBalance.text = "$%.2f".format(balance)
                }
                .addOnFailureListener {
                    binding.profileName.text = "Error"
                    binding.profileBalance.text = "-"
                }
        }

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root
        val db = FirebaseFirestore.getInstance()

        db.collection("users").get().addOnSuccessListener { userSnapshot ->
            val uidToName = userSnapshot.documents.associate { doc ->
                doc.id to (doc.getString("name") ?: "Unknown")
            }

            // Step 2: Real-time listener for all transactions
            db.collection("transactions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        Log.w("Firestore", "Listen failed", error)
                        return@addSnapshotListener
                    }

                    val txns = snapshot.documents
                        .mapNotNull { it.toObject(Transaction::class.java) }
                        .filter { it.senderId == currentUid || it.receiverId == currentUid }
                        .sortedByDescending { it.timestamp }

                    binding.profileTransactionList.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = TransactionAdapter(txns, currentUid, uidToName)
                    }
                }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
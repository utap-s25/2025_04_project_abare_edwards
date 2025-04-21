package edu.utap.a2025_04_project_abare_edwards

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.a2025_04_project_abare_edwards.database.TransactionStore
import edu.utap.a2025_04_project_abare_edwards.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    private fun setupButtons() {
        binding.changeNameBtn.setOnClickListener {
            val editText = EditText(requireContext())
            AlertDialog.Builder(requireContext())
                .setTitle("Change Name")
                .setMessage("Enter your new name:")
                .setView(editText)
                .setPositiveButton("OK") { _, _ ->
                    val newName = editText.text.toString().trim()
                    val uid = auth.currentUser?.uid
                    if (uid != null && newName.isNotEmpty()) {
                        db.collection("users").document(uid)
                            .update("name", newName)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.deleteHistoryBtn.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Account History Anonymization")
                .setMessage("Are you sure you want to anonymize your account history?")
                .setPositiveButton("Anonymize") { _, _ ->
                    TransactionStore.anonymizeUserTransactions(uid) {
                        Toast.makeText(requireContext(), "History anonymized", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.deleteAccountBtn.setOnClickListener {
            val user = auth.currentUser ?: return@setOnClickListener
            val uid = user.uid

            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Account Deletion")
                .setMessage("Are you sure you want to permanently delete your account?")
                .setPositiveButton("Delete") { _, _ ->
                    TransactionStore.anonymizeUserTransactions(uid) {
                        db.collection("users").document(uid).delete()
                        user.delete().addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Account deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.overrideBalanceBtn.setOnClickListener {
            val editText = EditText(requireContext())
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            AlertDialog.Builder(requireContext())
                .setTitle("Override Balance")
                .setMessage("Enter new balance:")
                .setView(editText)
                .setPositiveButton("OK") { _, _ ->
                    val balance = editText.text.toString().toDoubleOrNull()
                    val uid = auth.currentUser?.uid
                    if (uid != null && balance != null) {
                        db.collection("users").document(uid)
                            .update("balance", balance)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Balance updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.signOut.setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package edu.utap.a2025_04_project_abare_edwards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import edu.utap.a2025_04_project_abare_edwards.database.TransactionStore
import edu.utap.a2025_04_project_abare_edwards.database.UserStore
import edu.utap.a2025_04_project_abare_edwards.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.settingsBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            UserStore.liveUsers.observe(viewLifecycleOwner) { users ->
                val user = users.find { it.uid == uid }
                val name = user?.name ?: "Unknown"
                val balance = user?.balance ?: 0.0

                binding.profileName.text = name
                binding.profileBalance.text = "$%.2f".format(balance)
            }
        }

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root

        UserStore.uidToNameMap.observe(viewLifecycleOwner) { uidToName ->
            TransactionStore.liveTransactions.observe(viewLifecycleOwner) { allTransactions ->
                val userTransactions = allTransactions
                    .filter { it.senderId == currentUid || it.receiverId == currentUid }
                    .sortedByDescending { it.timestamp }

                binding.profileTransactionList.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = TransactionAdapter(userTransactions, currentUid, uidToName)
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
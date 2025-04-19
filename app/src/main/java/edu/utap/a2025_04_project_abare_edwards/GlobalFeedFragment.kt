package edu.utap.a2025_04_project_abare_edwards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.a2025_04_project_abare_edwards.database.Transaction
import edu.utap.a2025_04_project_abare_edwards.database.TransactionStore
import edu.utap.a2025_04_project_abare_edwards.databinding.FragmentGlobalFeedBinding

class GlobalFeedFragment : Fragment() {
    private var _binding: FragmentGlobalFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GlobalFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGlobalFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Layout manager only needs to be set once
        binding.globalFeedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch UID → Name map
        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { snapshot ->
            val uidToName = snapshot.documents.associate { doc ->
                doc.id to (doc.getString("name") ?: "Unknown")
            }

            // Observe live transactions and populate feed with names
            TransactionStore.liveTransactions.observe(viewLifecycleOwner) { transactions ->
                adapter = GlobalFeedAdapter(transactions, uidToName)
                binding.globalFeedRecyclerView.adapter = adapter
            }
        }
    }
}
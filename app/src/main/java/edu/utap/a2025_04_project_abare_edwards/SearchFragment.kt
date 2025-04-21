package edu.utap.a2025_04_project_abare_edwards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.a2025_04_project_abare_edwards.database.User
import edu.utap.a2025_04_project_abare_edwards.database.UserStore
import edu.utap.a2025_04_project_abare_edwards.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var allUsers: List<Pair<String, User>>
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root

        UserStore.liveUsers.observe(viewLifecycleOwner) { users ->
            allUsers = users
                .filter { it.uid != currentUid }
                .map { it.uid to it }

            adapter = UserAdapter(allUsers) { uid, user ->
                val sendMoneyFragment = SendMoneyFragment(uid, user.name)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, sendMoneyFragment)
                    .addToBackStack(null)
                    .commit()
            }


            binding.peopleRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@SearchFragment.adapter
            }

            binding.searchBar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = s.toString().lowercase()
                    val filtered = allUsers.filter { it.second.name.lowercase().contains(query) }
                    adapter.updateList(filtered)
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

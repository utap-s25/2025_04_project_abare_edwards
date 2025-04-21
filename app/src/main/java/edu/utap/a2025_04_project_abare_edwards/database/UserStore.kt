package edu.utap.a2025_04_project_abare_edwards.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

object UserStore {

    private val liveUsersStore = MutableLiveData<List<User>>()
    val liveUsers: LiveData<List<User>> get() = liveUsersStore

    private val liveUidToNameMap = MutableLiveData<Map<String, String>>()
    val uidToNameMap: LiveData<Map<String, String>> get() = liveUidToNameMap

    fun init() {
        FirebaseFirestore.getInstance()
            .collection("users")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val users = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)?.apply { uid = doc.id }
                    }
                    liveUsersStore.postValue(users)

                    val map = users.associate { it.uid to it.name }
                    liveUidToNameMap.postValue(map)
                }
            }
    }
}
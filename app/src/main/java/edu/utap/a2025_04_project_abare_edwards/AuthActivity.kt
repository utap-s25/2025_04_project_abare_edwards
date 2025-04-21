package edu.utap.a2025_04_project_abare_edwards

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.a2025_04_project_abare_edwards.database.User

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var working = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val registerBtn = findViewById<Button>(R.id.registerButton)

        loginBtn.setOnClickListener {
            loginBtn.isEnabled = false
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if (!working && email.isNotEmpty() && password.isNotEmpty()) {
                working = true
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        Log.e("Auth", "Login error", it)
                    }
            }
            loginBtn.isEnabled = true
            working = false
        }

        registerBtn.setOnClickListener {
            registerBtn.isEnabled = false
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if (!working && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val uid = it.user?.uid
                        if (uid != null) {
                            val user = User(name = email.substringBefore("@"), balance = 0.0)
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener {
                                    Log.d("Auth", "User Firestore document created")
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Auth", "Firestore user creation failed", e)
                                    Toast.makeText(
                                        this,
                                        "Failed to save user to database",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        Log.e("Auth", "Firebase Auth registration failed", it)
                    }
            }
            registerBtn.isEnabled = true
            working = false
        }
    }
}

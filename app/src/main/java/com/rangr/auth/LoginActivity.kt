package com.rangr.auth

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            LoginScreen()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // TODO
        }
    }

    @Composable
    fun LoginScreen() {

        val isAuthenticated = authViewModel.isAuthenticated.observeAsState(false)
        val currentUser = auth.currentUser
        if (isAuthenticated.value) {
            LoggedIn(auth.currentUser!!)

        } else {
            SignUp()
        }
    }

    private @Composable
    fun LoggedIn(currentUser: FirebaseUser) {
        Column {

            Text(text = "Logged in")

            currentUser.let {
                // Name, email address, and profile photo Url
                val name = it.displayName
                val email = it.email
                val photoUrl = it.photoUrl

                // Check if user's email is verified
                val emailVerified = it.isEmailVerified

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                val uid = it.uid

                Text(text = "Hi $name!")
                Text(text = "Hi $email!")

            Button(onClick = {
                auth.signOut()
                Toast.makeText(baseContext, "Logged out", Toast.LENGTH_SHORT).show()
            }) {
                Text("Sign out")
            }
            }
        }

    }

    @Composable
    fun SignUp() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                createUserWithEmailAndPassword(email, password) { resultMessage ->
                    Toast.makeText(baseContext, "Logged in", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Sign Up")
            }
        }

    }

    private fun createUserWithEmailAndPassword(email: String, password: String, onResult: (String) -> Unit) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success
                        val user = auth.currentUser
                        onResult("Account Created: ${user?.email}")
                    } else {
                        // If sign up fails, display a message to the user.
                        onResult("Authentication failed.")
                    }
                }
        } else {
            onResult("Please enter email and password.")
        }
    }

    public fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    public fun updateUI(user: FirebaseUser?) {
    }
}

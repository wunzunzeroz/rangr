package com.rangr.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private var _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated = _isAuthenticated

    init {
        _isAuthenticated.value = auth.currentUser != null
         auth.addAuthStateListener {
             _isAuthenticated.value = it.currentUser != null
         }
    }
}
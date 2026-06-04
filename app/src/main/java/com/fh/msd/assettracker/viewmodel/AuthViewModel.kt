package com.fh.msd.assettracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fh.msd.assettracker.R
import com.fh.msd.assettracker.constants.FirebaseConstants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val _toastMessage = MutableStateFlow<String?>(null);
    val toastMessage = _toastMessage.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _navigateToHome = MutableSharedFlow<Boolean>()
    val navigateToHome = _navigateToHome.asSharedFlow()

    private fun getString(resId: Int): String = getApplication<Application>().getString(resId)

    fun login(email: String, password: String) {
        _toastMessage.value = null
        if (email.isEmpty() || password.isEmpty()) {
            _toastMessage.value = getString(R.string.err_invalid_values)
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                _toastMessage.value = getString(R.string.msg_login_success)
                _isLoggedIn.value = true
                viewModelScope.launch {
                    _navigateToHome.emit(true)
                }
            }.addOnFailureListener { exception ->
                _toastMessage.value = exception.message ?: getString(R.string.err_login_unknown)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }

    fun register(
        firstname: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        agreeTermsAndConditions: Boolean
    ) {
        _toastMessage.value = null
        if (firstname.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || password.length < 6 || (password != confirmPassword) || !agreeTermsAndConditions) {
            if (password.length < 6) {
                _toastMessage.value = getString(R.string.err_password_short)
            } else if ((password != confirmPassword)) {
                _toastMessage.value = getString(R.string.err_password_mismatch)
            } else if (!agreeTermsAndConditions) {
                _toastMessage.value = getString(R.string.err_accept_terms)
            } else {
                _toastMessage.value = getString(R.string.err_fill_all_fields)
            }
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstname $lastName").build()
                    )
                    val firebaseConstants = FirebaseConstants()

                    val user = hashMapOf(
                        firebaseConstants.firstname to firstname,
                        firebaseConstants.lastname to lastName,
                        firebaseConstants.email to email,
                        firebaseConstants.uid to authResult.user?.uid
                    )

                    db.collection(firebaseConstants.userCollection).add(user)
                        .addOnSuccessListener {
                            _toastMessage.value = getString(R.string.msg_register_success)
                            _isLoggedIn.value = true
                            viewModelScope.launch {
                                _navigateToHome.emit(true)
                            }
                        }.addOnFailureListener { exception ->
                            _toastMessage.value = exception.message ?: getString(R.string.err_register_unknown)
                        }

                }.addOnFailureListener { exception ->
                    _toastMessage.value = exception.message ?: getString(R.string.err_register_unknown)
                }
        }
    }

    fun forgotPassword(email: String) {
        _toastMessage.value = null
        if (email.isEmpty()) {
            _toastMessage.value = getString(R.string.err_provide_email)
        } else {
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                _toastMessage.value = getString(R.string.msg_forgot_password_success)
            }.addOnFailureListener { exception ->
                _toastMessage.value =
                    exception.message ?: getString(R.string.err_forgot_password_unknown)
            }
        }
    }
}
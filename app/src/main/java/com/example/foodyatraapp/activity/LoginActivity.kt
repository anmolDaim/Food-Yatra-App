package com.example.foodyatraapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodyatraapp.R
import com.example.foodyatraapp.dataModel.userModel
import com.example.foodyatraapp.databinding.ActivityLoginBinding
import com.example.foodyatraapp.databinding.ActivityStartBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//         Initialize GoogleSignInOptions
        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
//        Initialize GoogleSignInClient
        googleSignInClient= GoogleSignIn.getClient(this, googleSignInOptions)


        auth= Firebase.auth
        database= Firebase.database.reference

        binding.loginButton.setOnClickListener{
            email=binding.loginEmail.text.toString().trim()
            password=binding.loginPassword.text.toString().trim()

            if (email.isBlank()||password.isBlank()){
                Toast.makeText(this, "Please fill all details🥹", Toast.LENGTH_SHORT).show()
            }else{
                createUserAccount()
            }
        }
        binding.loginGoogle.setOnClickListener{
            val signIntent=googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.donthavebtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
//     Inside your Activity class
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credential =GoogleAuthProvider.getCredential(account?.idToken,null)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else {
                Toast.makeText(this, "Google SignIn is failed", Toast.LENGTH_SHORT).show()
            }
        }


    private fun createUserAccount() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                task->
            if(task.isSuccessful){
                val user=auth.currentUser
                updateUi(user)
            }else{
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        task->
                    if (task.isSuccessful){
//                        saveUserData()
                        val user=auth.currentUser
                        updateUi(user)
                    }else{
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,SignUpActivity::class.java))
                        Log.d("Account","createUSerAccount: Authentication failed",task.exception)
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if(currentUser!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun updateUi(user: FirebaseUser?) {
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
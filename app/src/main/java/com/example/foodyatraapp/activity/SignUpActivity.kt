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
import com.example.foodyatraapp.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var userName:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient :GoogleSignInClient


    private val binding: ActivitySignUpBinding by lazy{
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth=Firebase.auth
        database=Firebase.database.reference

        binding.createAccountButton.setOnClickListener{
            email = binding.emailAddress.text.toString().trim()
            userName = binding.nameEd.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(email.isBlank()||userName.isBlank()||password.isBlank()){
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            }else{
                cresteAccount(email,password)
            }

        }
        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(this, googleSignInOptions)
        binding.googleButton.setOnClickListener{
            val signIntent=googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.AlreadyHaveAnAcc.setOnClickListener{
            intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
    // Inside your Activity class
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

//


    private fun cresteAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account","create Account :Failure",task.exception)
            }
        }
    }

    private fun saveUserData() {
        userName=binding.nameEd.text.toString()
        email=binding.emailAddress.text.toString().trim()
        password=binding.password.text.toString().trim()
        val user = userModel(userName, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        // Save user data to Firebase Database
        database.child("user").child(userId).setValue(user)

    }
}
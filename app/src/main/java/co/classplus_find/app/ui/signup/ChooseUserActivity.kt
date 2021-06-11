package co.classplus_find.app.ui.signup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import co.classplus_find.app.MainActivity
import co.classplus_find.app.R
import co.classplus_find.app.data.PreferenceHelper.Companion.PREF_FILE
import co.classplus_find.app.data.PreferenceHelper.Companion.PREF_IS_TUTOR
import co.classplus_find.app.databinding.ActivityChooseUserBinding
import co.classplus_find.app.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ChooseUserActivity : AppCompatActivity() {

    lateinit var binding : ActivityChooseUserBinding
    private lateinit var mPrefs : SharedPreferences
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    var isTeacherSelected = -1

    companion object{
        var TAG = "ChooseUserActivity"
        var RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_user)
        binding.lifecycleOwner = this
        setupSignIn()
        setupData()
        setupListeners()
    }

    private fun setupSignIn(){
        // Initialize Firebase Auth
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("286512318525-brsna87u1if8t2ramhrli67ro6314lud.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private fun setupData(){
        mPrefs = (this as Context).getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupListeners(){
        binding.llTeacher.setOnClickListener {
            mPrefs.edit().putInt(PREF_IS_TUTOR, 1).apply()
            binding.llTeacher.background = ContextCompat.getDrawable(this, R.drawable.border)
            binding.llStudent.background = ContextCompat.getDrawable(this, R.color.white)
            isTeacherSelected = 1
        }
        binding.llStudent.setOnClickListener {
            mPrefs.edit().putInt(PREF_IS_TUTOR, 0).apply()
            binding.llStudent.background = ContextCompat.getDrawable(this, R.drawable.border)
            binding.llTeacher.background = ContextCompat.getDrawable(this, R.color.white)
            isTeacherSelected = 0
        }
        binding.signInButton.setOnClickListener {
            if(isTeacherSelected == -1){
                binding.clChooseUser.snackbar("Please select a User")
            }
            else {
                signIn()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id)
            firebaseAuthWithGoogle(account?.idToken!!)
            this.toast("Signed in")
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            this.toast("Sign in failed")

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    this.toast("Sign in failed")
                }
            }
    }


}
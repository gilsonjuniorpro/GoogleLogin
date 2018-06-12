package br.googlelogin.com

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java!!.getSimpleName()

    private val RC_SIGN_IN = 234

    var mGoogleSignInClient: GoogleSignInClient? = null

    var mAuth: FirebaseAuth? = null

    var fbUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)

        fbUser = mAuth!!.getCurrentUser()

        sign_in_button.setOnClickListener(View.OnClickListener {
            signIn()
        })

        sign_out_button.setOnClickListener(View.OnClickListener {
            signOut()
        })
    }


    private fun updateUI(user: GoogleSignInAccount?) {
        if(user != null || fbUser != null){
            detail.text = user!!.displayName
            sign_in_button.visibility = View.INVISIBLE
            sign_out_button.visibility = View.VISIBLE
        }else{
            detail.text = "No Access"
            sign_in_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.INVISIBLE
        }
    }


    override fun onStart() {
        super.onStart()

        if (mAuth!!.getCurrentUser() != null) {
            fbUser = mAuth!!.getCurrentUser()
            updateUI(null)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task);
        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.d(TAG, "error: " + e.message)
            updateUI(null)
        }

    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun signOut() {
        mAuth!!.signOut()
        mGoogleSignInClient!!.signOut().addOnCompleteListener(this
        ) {
            updateUI(null)
        }
    }
}

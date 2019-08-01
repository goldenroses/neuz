package inc.nyenjes.neuz.activities.registration

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import inc.nyenjes.neuz.R
import inc.nyenjes.neuz.activities.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG: String = "LoginActivity";
    //Firebase
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login);


        setupFirebaseAuth()

        buttonSignIn.setOnClickListener {

            //check if the fields are filled out
            if (!isEmpty(editTextEmail!!.text.toString()) && !isEmpty(editTextPassword!!.text.toString())) {
                Log.d(TAG, "onClick: attempting to authenticate.");

                showDialog()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
                    .addOnCompleteListener {
                        hideDialog()
                    }.addOnFailureListener {
                        fun onFailure(e: Exception) {
                            hideDialog()
                            Toast.makeText(this, "Login/Username Invalid", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        }

        link_register.setOnClickListener {
            val intent: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        link_forgot_password.setOnClickListener {
            val dialog: PasswordResetDialog = PasswordResetDialog()
            dialog.show(supportFragmentManager, "PasswordResetDialog")
        }

        link_resend_verification_email.setOnClickListener {
            val dialog: ResendVerificationDialog = ResendVerificationDialog()
            dialog.show(supportFragmentManager, "ResendVerificationDialog")
        }

    }


    private fun isEmpty(string: String): Boolean {
        return string.equals("");
    }


    private fun showDialog() {
        progressBar!!.setVisibility(View.VISIBLE);

    }

    private fun hideDialog() {
        if (progressBar!!.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private fun hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                if (user.isEmailVerified()) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT)
                        .show();

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT)
                        .show()
                    FirebaseAuth.getInstance().signOut()
                }
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)

    }
}
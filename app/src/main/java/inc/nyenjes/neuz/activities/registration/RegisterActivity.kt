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
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity: AppCompatActivity() {

    private val TAG = "RegisterActivity"

    private val DOMAIN_NAME = "rose.com"
    private var firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            Log.d(TAG, "onClick: attempting to register.")

            //check for null valued EditText fields
            if (!isEmpty(editTextEmail.text.toString())
                && !isEmpty(editTextPassword.text.toString())
                && !isEmpty(editTextConfirmPassword.text.toString())
            ) {

                //check if user has a company email address
                //
                if (true) {

                    //check if passwords match
                    if (doStringsMatch(editTextPassword.text.toString(), editTextConfirmPassword.text.toString())) {

                        //Initiate registration task
                        registerNewEmail(editTextEmail.text.toString(), editTextPassword.text.toString())
                    } else {
                        Toast.makeText(this@RegisterActivity, "Passwords do not Match", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@RegisterActivity, "Please Register with Company Email", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(this@RegisterActivity, "You must fill out all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        hideSoftKeyboard()

    }

    fun registerNewEmail(email: String, password: String) {

        showDialog()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)

                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().currentUser!!.uid)

                    FirebaseAuth.getInstance().signOut()

                    //redirect the user to the login screen
                    redirectLoginScreen()
                }
                else if (!task.isSuccessful) {
                    Toast.makeText(
                        this, "Unable to Register",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideDialog()

                // ...
            }
    }



    private fun isValidDomain(email: String): Boolean {
        Log.d(TAG, "isValidDomain: verifying email has correct domain: $email")
        val domain = email.substring(email.indexOf("@") + 1).toLowerCase()
        Log.d(TAG, "isValidDomain: users domain: $domain")
        return domain == DOMAIN_NAME
    }

    /**
     * Redirects the user to the login screen
     */
    private fun redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.")

        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun doStringsMatch(s1: String, s2: String): Boolean {
        return s1 == s2
    }

    private fun isEmpty(string: String): Boolean {
        return string == ""
    }


    private fun showDialog() {
        progressBar.setVisibility(View.VISIBLE)

    }

    private fun hideDialog() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE)
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
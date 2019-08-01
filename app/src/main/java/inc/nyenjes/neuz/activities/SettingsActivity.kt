package inc.nyenjes.neuz.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import inc.nyenjes.neuz.R
import inc.nyenjes.neuz.activities.registration.LoginActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SettingsActivity"

    private val DOMAIN_NAME = "tabian.ca"

    //firebase
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        Log.d(TAG, "onCreate: started.")

        setupFirebaseAuth()

        setCurrentEmail()

        btn_save.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onClick: attempting to save settings.")

            //make sure email and current password fields are filled
            if (!isEmpty(editTextEmail.text.toString()) && !isEmpty(editTextCurrentPassword.text.toString())) {

                /*
                        ------ Change Email Task -----
                         */
                //if the current email doesn't equal what's in the EditText field then attempt
                //to edit
                if (FirebaseAuth.getInstance().currentUser!!.email != editTextEmail.text.toString()) {

                    //verify that user is changing to a company email address
//                    isValidDomain(editTextEmail.text.toString()
                    if (true) {
                        editUserEmail()
                    } else {
                        Toast.makeText(this@SettingsActivity, "Invalid Domain", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@SettingsActivity, "no changes were made", Toast.LENGTH_SHORT).show()
                }


            } else {
                Toast.makeText(
                    this@SettingsActivity,
                    "Email and Current Password Fields Must be Filled to Save",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        linkChangePassword.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onClick: sending password reset link")

            /*
                    ------ Reset Password Link -----
                    */
            sendResetPasswordLink()
        })



        hideSoftKeyboard()
    }

    private fun sendResetPasswordLink() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser!!.email!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: Password Reset Email sent.")
                    Toast.makeText(
                        this@SettingsActivity, "Sent Password Reset Link to Email",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.d(TAG, "onComplete: No user associated with that email.")

                    Toast.makeText(
                        this@SettingsActivity, "No User Associated with that Email.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun editUserEmail() {
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        showDialog()

        val credential = EmailAuthProvider
            .getCredential(FirebaseAuth.getInstance().currentUser!!.email!!, editTextCurrentPassword.text.toString())
        Log.d(
            TAG, "editUserEmail: reauthenticating with:  \n email " + FirebaseAuth.getInstance().currentUser!!.email
                    + " \n passowrd: " + editTextCurrentPassword.text.toString()
        )


        FirebaseAuth.getInstance().currentUser!!.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: reauthenticate success.")

                    //make sure the domain is valid isValidDomain(editTextEmail.text.toString()
                    if (true) {

                        ///////////////////now check to see if the email is not already present in the database
                        FirebaseAuth.getInstance().fetchProvidersForEmail(editTextEmail.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    ///////// getProviders().size() will return size 1 if email ID is in use.

                                    Log.d(TAG, "onComplete: RESULT: " + task.result!!.providers!!.size)
                                    if (task.result!!.providers!!.size == 1) {
                                        Log.d(TAG, "onComplete: That email is already in use.")
                                        hideDialog()
                                        Toast.makeText(
                                            this@SettingsActivity,
                                            "That email is already in use",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Log.d(TAG, "onComplete: That email is available.")

                                        /////////////////////add new email
                                        FirebaseAuth.getInstance().currentUser!!.updateEmail(editTextEmail.text.toString())
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(TAG, "onComplete: User email address updated.")
                                                    Toast.makeText(
                                                        this@SettingsActivity,
                                                        "Updated email",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    sendVerificationEmail()
                                                    FirebaseAuth.getInstance().signOut()
                                                } else {
                                                    Log.d(TAG, "onComplete: Could not update email.")
                                                    Toast.makeText(
                                                        this@SettingsActivity,
                                                        "unable to update email",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                hideDialog()
                                            }
                                            .addOnFailureListener {
                                                hideDialog()
                                                Toast.makeText(
                                                    this@SettingsActivity,
                                                    "unable to update email",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }


                                    }

                                }
                            }
                            .addOnFailureListener {
                                hideDialog()
                                Toast.makeText(this@SettingsActivity, "unable to update email", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } else {
                        Toast.makeText(this@SettingsActivity, "you must use a company email", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.d(TAG, "onComplete: Incorrect Password")
                    Toast.makeText(this@SettingsActivity, "Incorrect Password", Toast.LENGTH_SHORT).show()
                    hideDialog()
                }
            }
            .addOnFailureListener {
                hideDialog()
                Toast.makeText(this@SettingsActivity, "“unable to update email”", Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * sends an email verification link to the user
     */
    fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@SettingsActivity, "Sent Verification Email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@SettingsActivity, "Couldn't Verification Send Email", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setCurrentEmail() {
        Log.d(TAG, "setCurrentEmail: setting current email to EditText field")

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            Log.d(TAG, "setCurrentEmail: user is NOT null.")

            val email = user.email

            Log.d(TAG, "setCurrentEmail: got the email: " + email!!)

            editTextEmail.setText(email)
        }
    }

    /**
     * Returns True if the user's email contains '@tabian.ca'
     * @param email
     * @return
     */
    private fun isValidDomain(email: String): Boolean {
        Log.d(TAG, "isValidDomain: verifying email has correct domain: $email")
        val domain = email.substring(email.indexOf("@") + 1).toLowerCase()
        Log.d(TAG, "isValidDomain: users domain: $domain")
        return domain == DOMAIN_NAME
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

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private fun isEmpty(string: String): Boolean {
        return string == ""
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    private fun checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state.")

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.")

            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "checkAuthenticationState: user is authenticated.")
        }
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
         */
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
                //toastMessage("Successfully signed in with: " + user.getEmail());


            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
                Toast.makeText(this@SettingsActivity, "Signed out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            // ...
        }
    }

    public override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }
    }
}
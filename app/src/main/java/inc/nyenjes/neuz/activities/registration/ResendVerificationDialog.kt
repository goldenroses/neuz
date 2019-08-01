package inc.nyenjes.neuz.activities.registration

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import inc.nyenjes.neuz.R
import kotlinx.android.synthetic.main.dialog_resend_verification.*

class ResendVerificationDialog: DialogFragment() {

    private val TAG = "ResendVerificationDialo"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.dialog_resend_verification, container, false)

        val confirmDialog = view.findViewById(R.id.dialogConfirm) as TextView
        confirmDialog.setOnClickListener {
            Log.d(TAG, "onClick: attempting to resend verification email.")

            if (!isEmpty(confirm_email.text.toString()) && !isEmpty(confirm_password.text.toString())) {

                //temporarily authenticate and resend verification email
                authenticateAndResendEmail(
                    confirm_email.text.toString(),
                    confirm_password.text.toString()
                )
            } else {
                Toast.makeText(context, "all fields must be filled out", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel button for closing the dialog
        val cancelDialog = view.findViewById(R.id.dialogCancel) as TextView
        cancelDialog.setOnClickListener { dialog.dismiss() }

        return view
    }
    
       /**
 * reauthenticate so we can send a verification email again
 * @param email
 * @param password
 */
    private fun authenticateAndResendEmail(email:String, password:String) {
           val credential = EmailAuthProvider
               .getCredential(email, password)
           FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{task ->
               if (task.isSuccessful()) {
                   Log.d(TAG, "onComplete: reauthenticate success.")
                   sendVerificationEmail()
                   FirebaseAuth.getInstance().signOut()
                   dialog.dismiss()
               }
           }.addOnFailureListener{
               Toast.makeText(context, "Invalid Credentials. \nReset your password and try again", Toast.LENGTH_SHORT)
                   .show()
               dialog.dismiss()
           }

       } 

    /**
     * sends an email verification link to the user
     */
    fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Sent Verification Email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "couldn't send email", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun isEmpty(string: String): Boolean {
        return string == ""
    }

}
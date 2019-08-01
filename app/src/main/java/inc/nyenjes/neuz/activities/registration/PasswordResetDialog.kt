package inc.nyenjes.neuz.activities.registration

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import inc.nyenjes.neuz.R
import kotlinx.android.synthetic.main.dialog_resetpassword.*

class PasswordResetDialog : DialogFragment() {

    private val TAG = "PasswordResetDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.dialog_resetpassword, container, false)

        val confirmDialog = view.findViewById<View>(R.id.dialogConfirm) as TextView
        confirmDialog.setOnClickListener {
            val emailText = email_password_reset.text.toString()
            if (!isEmpty(emailText)) {
                Log.d(TAG, "onClick: attempting to send reset link to: " + emailText)
                sendPasswordResetEmail(emailText)
                dialog.dismiss()
            }
        }

        return view
    }

    fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: Password Reset Email sent.")
//                    Toast.makeText(this@LoginActivity, "Password Reset Link Sent to Email",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    Log.d(TAG, "onComplete: No user associated with that email.")
//                    Toast.makeText(
//                        context, "No User is Associated with that Email",
//                        Toast.LENGTH_SHORT
//                    ).show()

                }
            }
    }


    private fun isEmpty(string: String): Boolean {
        return string == ""
    }

}
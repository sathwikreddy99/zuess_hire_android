package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.algolia.search.model.rule.Edit
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.zuess.zuess_android.R

class passwordReset : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val resetButton = view.findViewById<Button>(R.id.resetPasswordButton)
        val email = view.findViewById<EditText>(R.id.passwordResetEmail)
        val backButton = view.findViewById<ImageView>(R.id.passwordResetPageBackButton)

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        fun isEmailValid(email : String) : Boolean{
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        resetButton.setOnClickListener {
            if (email.text.toString() != null && isEmailValid(email.text.toString())){
                auth.sendPasswordResetEmail(email.text.toString())
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Snackbar.make(view,"reset link has been sent to your email",3000).show()
                            Toast.makeText(requireContext(),"reset link has been sent to your email",Toast.LENGTH_LONG).show()
                            requireActivity().onBackPressed()
                        }else{
                            Toast.makeText(requireContext(),"failed",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}
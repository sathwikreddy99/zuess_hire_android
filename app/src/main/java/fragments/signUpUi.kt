package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.zuess.zuess_android.R
import org.w3c.dom.Text
import java.util.regex.Matcher
import java.util.regex.Pattern

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception


class signUpUi : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val signUpButton = view.findViewById<Button>(R.id.signUpUiSignUpButton)
        val loginButton = view.findViewById<TextView>(R.id.signUpUiLogin)
        val firstName = view.findViewById<EditText>(R.id.signUpFirstName)
        val lastName = view.findViewById<EditText>(R.id.signUpLastName)
        val email = view.findViewById<EditText>(R.id.signUpUiEmail)
        val password = view.findViewById<TextInputEditText>(R.id.signUpUiPassword)
        val skipButton = view.findViewById<TextView>(R.id.signUpUiSkip)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val loadingDailog = loadingDailog(requireContext())

        fun isEmailValid(email : String) : Boolean{
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }


        skipButton.setOnClickListener {
            navController.navigate(signUpUiDirections.actionSignUpUi2ToSearchUi())
        }

        loginButton.setOnClickListener {
            navController.navigate(signUpUiDirections.actionSignUpUi2ToLoginui())
        }

        signUpButton.setOnClickListener {
            if (firstName.text.isEmpty()){
                firstName.setError("first name cannot be empty")
            }else if (lastName.text.isEmpty()){
                lastName.setError("last name cannot be empty")
            }else if (!isEmailValid(email.text.toString())){
                email.setError("email pattern not valid")
            }else if (password.text?.length!! < 8){
                password.setError("password should not be less than 8 characters")
            }else{
                loadingDailog.showDialog()
                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener { task ->
                        loadingDailog.dismissDialog()
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success")
                            Toast.makeText(requireContext(), "signUp successful",
                                Toast.LENGTH_LONG).show()
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val data = hashMapOf<String,Any>(
                                "first_name" to firstName.text.toString(),
                                "last_name" to lastName.text.toString(),
                                "email_id" to email.text.toString(),
                                "userId" to userId.toString()
                            )
                            db.collection("users").add(data).addOnSuccessListener {
                                navController.navigate(signUpUiDirections.actionSignUpUi2ToSearchUi())
                            }
                            val user = auth.currentUser
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                password.setError("weak password")
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                email.setError("email id not valid")
                            } catch (e: FirebaseAuthUserCollisionException) {
                                email.setError("email already exists")
                            } catch (e: Exception) {
                                Log.e("", e.message!!)
                            }
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(requireContext(), "sign up failed.",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }


    }
}
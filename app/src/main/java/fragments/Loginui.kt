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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.compose.navArgument
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.zuess.zuess_android.R
import viewmodels.userViewModel


class loginui : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.actionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loginui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val model: userViewModel by viewModels()


        super.onViewCreated(view, savedInstanceState)

        val loadingDailog =loadingDailog(requireContext())
        //getting fields
        val email = view.findViewById<EditText>(R.id.loginEmailField)
        val password = view.findViewById<EditText>(R.id.loginPasswordField)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val skipButton = view.findViewById<TextView>(R.id.loginUiSkip)
        val signUpButton = view.findViewById<Button>(R.id.loginUiSignupButton)
        val forgotPassword = view.findViewById<Button>(R.id.loginUIForgotPassword)
        val navController = view.findNavController()
        var loginButtonClicked = false


        //login button listener
        loginButton.setOnClickListener{
            loginButtonClicked = true
            Log.d("","in login button")
            if(email.text.isNullOrEmpty()){
                email.error = "email cannot be empty"
            }else if(password.text.isEmpty()){
                password.error = "password cannot be empty"
            }else {
                loadingDailog.showDialog()
//                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                model.loginUser(email.text.toString(),password.text.toString(),requireContext())
            }
        }

        skipButton.setOnClickListener {
            navController.navigate(loginuiDirections.actionLoginuiToSearchUi())
        }

        signUpButton.setOnClickListener {
            navController.navigate(loginuiDirections.actionLoginuiToSignUpUi2())
        }

        forgotPassword.setOnClickListener {
            navController.navigate(loginuiDirections.actionLoginuiToPasswordReset())
        }

        //navigation logic when login button is pressed
        model.userLiveData.observe(viewLifecycleOwner, Observer { user->
            if (loginButtonClicked){
                loadingDailog.dismissDialog()
            }
            if(user==null && loginButtonClicked){
                Log.i("","error")
                email.setError("incorrect email or password")
                password.setError("incorrect email or password")
            }else if (user != null){
//                var i = Intent(activity, HomeActivity:: class.java)
//                startActivity(i)
                navController.navigate(fragments.loginuiDirections.actionLoginuiToSearchUi())

            }
        })

    }

}
package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.zuess.zuess_android.HomeActivity
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

        //getting fields
        val email = view.findViewById<EditText>(R.id.loginEmailField)
        val password = view.findViewById<EditText>(R.id.loginPasswordField)
        val loginButton = view.findViewById<Button>(R.id.loginButton)


        //login button listener
        loginButton.setOnClickListener{
            Log.d("","in login button")
            if(email.text.isNullOrEmpty()){
                email.error = "email cannot be empty"
            }else {
//                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                model.loginUser(email.text.toString(),password.text.toString())
            }
        }

        val navController = view.findNavController()

        //navigation logic when login button is pressed
        model.userLiveData.observe(viewLifecycleOwner, Observer { user->
            if(user==null){
                Log.i("","error")
            }else{
                var i = Intent(activity, HomeActivity:: class.java)
                startActivity(i)

            }
        })

    }

}
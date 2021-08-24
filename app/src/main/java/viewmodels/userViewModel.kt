package viewmodels

import Authentication
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.zuess.zuess_android.MainApplication

class userViewModel : ViewModel(){

    var  auth : Authentication = Authentication()
    var userLiveData: MutableLiveData<String> = auth.getUserLiveData()


    fun loginUser(email:String,password:String){
        auth.signIn(email,password)
    }
    fun signOutUser(){
        auth.signOut()
    }





}

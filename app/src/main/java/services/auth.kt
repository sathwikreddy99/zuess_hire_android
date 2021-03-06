


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fragments.loadingDailog


public class Authentication {
    private lateinit var auth: FirebaseAuth
    var userUid : String =""
    val userMutableLiveData : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

// sign in function
    fun signIn(email : String,password : String ,context: Context){
        var result: FirebaseUser?
        auth= FirebaseAuth.getInstance()
//        val loadingDailog = loadingDailog(context)
//        loadingDailog.showDialog()
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                task ->
//                loadingDailog.showDialog()
                if(task.isSuccessful){
                    result = auth.currentUser
                    userMutableLiveData.postValue(result?.uid)
                    userUid = result?.uid ?: ""
                }else{
                    userMutableLiveData.postValue(null)
                    Log.e("","LoginFailed")
                }
            }

    }

    //sign up function
    fun signUp(email : String,password : String ): FirebaseUser? {
        var result : FirebaseUser? = null
        auth= FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                task ->
                if (task.isSuccessful){
                    result = auth.currentUser
                    userMutableLiveData.postValue(result?.uid)
                    userUid = result?.uid.toString()
                }
            }
        return result
    }

    //signout function
    fun signOut(){
        auth= FirebaseAuth.getInstance()
        auth.signOut()
        userMutableLiveData.postValue(null)
    }
    //getting current user
    fun currentUser(): FirebaseUser?{
        auth= FirebaseAuth.getInstance()
        return auth.currentUser
    }

    //sending user livedata
    fun getUserLiveData(): MutableLiveData<String> {
        auth = FirebaseAuth.getInstance()
        userMutableLiveData.postValue(auth.currentUser?.uid)
        return userMutableLiveData
    }

}





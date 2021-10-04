package services

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObjects
import com.google.firestore.v1.MapValue
import data_models.userData
import kotlinx.coroutines.flow.merge
import java.util.*

//code to get our current user profile

class getUserProfile {

    //intialising variable
    lateinit var db : FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var userUid : String =""
    val user: MutableLiveData<userData> by lazy {
        MutableLiveData<userData>()
    }

    //gets current user
    fun getCurrentUser(){
        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        userUid = auth.currentUser?.uid.toString()
    }

    //gets entire user profile
    fun getUserProfile(){
        db = FirebaseFirestore.getInstance()
        Log.i("","user id: $userUid")

        if(userUid!=null){
            db.collection("users").whereEqualTo("first_name","Anshu")
                .get()
                .addOnSuccessListener {result ->
                    if(!result.documents.isEmpty()) {

                        for (document in result) {
                            val data = document.data
                            val list: Map<String,Map<String,Any>> = document["services_offered"] as Map<String, Map<String, Any>>
                            val oList : MutableList<Map<String, Any>> = mutableListOf()
                            val jobTitles : Map<String, Any> = document["job_titles"] as Map<String, Any>
//                            val key : List<String> = document["keywords"] as List<String>
                            for((k,v) in list){
                                oList.add(v)
                            }

                            //userData is a data model
                            user.postValue(userData(
                                data["first_name"].toString(),
                                data["last_name"].toString(),
                                data["profile_photo"].toString(),
                                document["job_titles"],
                                data["rating"],
                                oList,
                                data["price_per_hour"]
                            ))
                        }
                    }
                }

        }
    }

    //returns user live data
    fun userLiveData(): MutableLiveData<userData> {

        return user
    }

//    fun addData(){
//        db= FirebaseFirestore.getInstance()
//        var id = ""
//        var arjun = hashMapOf(
//            "job_titles" to hashMapOf(
//                "0" to "lover",
//                "1" to "doctor",
//                "2" to "hater"),
//            "rating" to 4.5,
//            "price_per_hour" to 200,
//            "services_offered" to hashMapOf(
//                "0" to hashMapOf(
//                "type" to "surgeries",
//                "price" to 2000
//            ),
//                "1" to hashMapOf(
//                "type" to "consultancy",
//                "price" to 3000
//            ),
//                "2" to hashMapOf(
//                "type" to "nutrition",
//                "price" to 1500
//            ))
//
//
//        )
//        var doc=db.collection("users").whereEqualTo("first_name","Anshu")
//            .get().addOnSuccessListener { result->
//                for(document in result){
//                    id = document.id
//                    db.collection("users").document(id).set(arjun, SetOptions.merge())
//                }
//            }
//
//    }

}
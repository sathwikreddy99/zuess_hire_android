package viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.search.model.rule.Alternatives
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import fragments.freelancerEdit
import fragments.loadingDailog
import java.util.*

class freelancerEditViewModel : ViewModel() {

    var desPhotosList : MutableList<ByteArray> = arrayListOf()
    var profilePhoto : ByteArray? = null
    val editDone : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun firstTime( desImages : List<ByteArray>,
                   profilePhoto : ByteArray,
                   data1 : Map<String,Any>,
                   location: LatLng?,
                   context : Context){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val loadingDailog : loadingDailog = loadingDailog(context)
        var storageRef = FirebaseStorage.getInstance().reference
        var urls : MutableList<String> = arrayListOf()
        var paths : MutableList<String> = arrayListOf()


        loadingDailog.showDialog()

        //upload profile photo
        storageRef.child("freelancers/$userId/"+"profile_picture"+".jpg").putBytes(profilePhoto)
            .addOnSuccessListener {
                storageRef.child("freelancers/$userId/"+"profile_picture"+".jpg").downloadUrl
                    .addOnSuccessListener { profilePhotoUrl ->
                        //upload description photos
                        for (i in 0..3){
                            var rString = UUID.randomUUID().toString()
                            storageRef.child("freelancers/$userId/"+rString+".jpg").putBytes(desImages[i])
                                .addOnSuccessListener {
                                    paths.add("freelancers/$userId/"+rString+".jpg")
                                    Log.i("","in cloud storage successs listerner")
                                    storageRef.child("freelancers/$userId/"+rString+".jpg").downloadUrl
                                        .addOnSuccessListener {url->
                                            urls.add(url.toString())
                                            Log.i("for loop no","$i")
                                            //adding data to firebase
                                            if (i == 3){
                                                var data = hashMapOf<String,Any?>(
                                                    "name" to data1["name"],
                                                    "job_titles" to data1["job_titles"],
                                                    "location" to data1["location"],
                                                    "description" to data1["description"],
                                                    "services" to data1["services"],
                                                    "profile_photo" to hashMapOf<String,Any>(
                                                        "url" to profilePhotoUrl.toString(),
                                                        "path" to "freelancers/$userId/"+"profile_picture"+".jpg"
                                                    ),
                                                    "_geoloc" to hashMapOf<String,Double?>(
                                                        "lat" to location?.latitude,
                                                        "lng" to location?.longitude
                                                     ),
                                                    "uid" to userId.toString(),
                                                    "description_photos" to hashMapOf<String,Any>(
                                                        "0" to hashMapOf<String, Any>(
                                                            "url" to urls[0],
                                                            "path" to paths[0]
                                                        ),
                                                        "1" to hashMapOf<String, Any>(
                                                            "url" to urls[1],
                                                            "path" to paths[1]
                                                        ),
                                                        "2" to hashMapOf<String, Any>(
                                                            "url" to urls[2],
                                                            "path" to paths[2]
                                                        ),
                                                        "3" to hashMapOf<String, Any>(
                                                            "url" to urls[3],
                                                            "path" to paths[3]
                                                        )
                                                    )
                                                )
                                                db.collection("freelancers").add(data)
                                                    .addOnSuccessListener { documentReference ->
                                                        loadingDailog.dismissDialog()
                                                        editDone.postValue(true)
                                                    }
                                                    .addOnFailureListener { e ->
                                                        loadingDailog.dismissDialog()
                                                        Log.w("", "Error adding document", e)
                                                    }
                                            }
                                        }

                                }.addOnFailureListener{
                                    loadingDailog.dismissDialog()
                                    Toast.makeText(context, "Upload failed, try again", Toast.LENGTH_SHORT)
                                }
                        }

                    }
                    }
            }



    fun noPhotosChanged(data1 : Map<String, Any>, docId : String,location: LatLng?,
                        context: Context){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val loadingDailog : loadingDailog = loadingDailog(context)

        var data = hashMapOf<String,Any?>(
            "name" to data1["name"],
            "job_titles" to data1["job_titles"],
            "_geoloc" to hashMapOf<String,Double?>(
                "lat" to location?.latitude,
                "lng" to location?.longitude
            ),
            "description" to data1["description"],
            "services" to data1["services"],
        )
        db.collection("freelancers").document(docId).set(data, SetOptions.merge())


        //changing their names in chats document
        db.collection("chats").whereEqualTo("user_1.uid",userId).get()
            .addOnSuccessListener { snap->

                if (snap.documents.size != 0){
                    var nameChange = hashMapOf<String,Any?>(
                        "user_1" to hashMapOf<String,Any?>(
                            "name" to data1["name"],
                            "profile_photo" to snap.documents[0].get("user_1.profile_photo"),
                            "uid" to userId
                        )
                    )
                    for (i in 0..snap.documents.size-1){
                        db.collection("chats").document(snap.documents[i].id).set(nameChange, SetOptions.merge())
                            .addOnSuccessListener {
                                if (i == snap.documents.size-1){
                                    editDone.postValue(true)
                                }
                            }
                    }
                }
            }
        db.collection("chats").whereEqualTo("user_2.uid",userId).get()
            .addOnSuccessListener { snap->
                if (snap.documents.size != 0){
                    var nameChange = hashMapOf<String,Any?>(
                        "user_2" to hashMapOf<String,Any?>(
                            "name" to data1["name"],
                            "profile_photo" to snap.documents[0].get("user_2.profile_photo"),
                            "uid" to userId
                        )
                    )
                    for (i in 0..snap.documents.size-1){
                        db.collection("chats").document(snap.documents[i].id).set(nameChange, SetOptions.merge())
                            .addOnSuccessListener {
                                if (i == snap.documents.size-1){
                                    editDone.postValue(true)
                                }
                            }
                    }
                }
            }
    }

    fun profilPhotoChanged(profilePhoto: ByteArray, data1 : Map<String, Any>, docId : String,location: LatLng?, desPhotosChanged : Boolean, context: Context){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val loadingDailog : loadingDailog = loadingDailog(context)
        var storageRef = FirebaseStorage.getInstance().reference

        db.collection("freelancers").document(docId).get().addOnSuccessListener {
            snapshot->
            // deleting old profile photo
            storageRef.child(snapshot.get("profile_photo.path").toString()).delete()
                .addOnSuccessListener {
                    //uploading new profile photo
                    storageRef.child("freelancers/$userId/"+"profile_picture"+".jpg").putBytes(profilePhoto)
                        .addOnSuccessListener {
                            storageRef.child("freelancers/$userId/" + "profile_picture" + ".jpg").downloadUrl
                                .addOnSuccessListener { url ->
                                    var data = hashMapOf<String,Any?>(
                                        "name" to data1["name"],
                                        "job_titles" to data1["job_titles"],
                                        "_geoloc" to hashMapOf<String,Double?>(
                                            "lat" to location?.latitude,
                                            "lng" to location?.longitude
                                        ),
                                        "description" to data1["description"],
                                        "profile_photo" to hashMapOf<String,Any>(
                                            "url" to url.toString(),
                                            "path" to "freelancers/$userId/"+"profile_picture"+".jpg"
                                        ),
                                        "services" to data1["services"],
                                    )
                                    db.collection("freelancers").document(docId).set(data, SetOptions.merge())
                                        .addOnSuccessListener {
                                            if (!desPhotosChanged){
                                                editDone.postValue(true)
                                            }
                                        }

                                    //changing their names in chats document
                                    db.collection("chats").whereEqualTo("user_1.uid",userId).get()
                                        .addOnSuccessListener { vv->
                                            var nameChange = hashMapOf<String,Any?>(
                                                "user_1" to hashMapOf<String,Any?>(
                                                    "name" to data1["name"],
                                                    "profile_photo" to url.toString(),
                                                    "uid" to userId
                                                )
                                            )
                                            if(vv.documents.size != 0){
                                                for (i in 0..vv.documents.size-1){
                                                    db.collection("chats").document(vv.documents[i].id).set(nameChange, SetOptions.merge())
                                                }
                                            }
                                        }
                                    db.collection("chats").whereEqualTo("user_2.uid",userId).get()
                                        .addOnSuccessListener {vv->
                                            var nameChange = hashMapOf<String,Any?>(
                                                "user_2" to hashMapOf<String,Any?>(
                                                    "name" to data1["name"],
                                                    "profile_photo" to url.toString(),
                                                    "uid" to userId
                                                )
                                            )
                                            if(vv.documents.size != 0){
                                                for (i in 0..vv.documents.size-1){
                                                    db.collection("chats").document(vv.documents[i].id).set(nameChange, SetOptions.merge())
                                                }
                                            }                                        }

                                }
                        }
                }
        }

    }

    fun desPhotosChanged(desImages : List<ByteArray>,data1 : Map<String, Any>,context: Context,location: LatLng?, docId: String){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val loadingDailog : loadingDailog = loadingDailog(context)
        var storageRef = FirebaseStorage.getInstance().reference

        db.collection("freelancers").document(docId).get().addOnSuccessListener {
            snapshot->
            if (snapshot.data != null){
                for (i in 0..3){
                    Log.i("desphotos path","${desImages[i]}")
                    storageRef.child(snapshot.get("description_photos.$i.path").toString()).delete()
                        .addOnSuccessListener {
                            if (i==3){
                                var paths : MutableList<String> = arrayListOf()
                                var urls : MutableList<String> = arrayListOf()
                                for (j in 0..3){
                                    var rString = System.nanoTime()
                                    storageRef.child("freelancers/$userId/"+rString+".jpg").putBytes(desImages[j])
                                        .addOnSuccessListener {
                                            paths.add("freelancers/$userId/"+rString+".jpg")
                                            Log.i("","in cloud storage successs listerner")
                                            storageRef.child("freelancers/$userId/"+rString+".jpg").downloadUrl
                                                .addOnSuccessListener {url->
                                                    urls.add(url.toString())
                                                    //adding data to firebase
                                                    if (j == 3){
                                                        var data = hashMapOf<String,Any?>(
                                                            "name" to data1["name"],
                                                            "job_titles" to data1["job_titles"],
                                                            "_geoloc" to hashMapOf<String,Double?>(
                                                                "lat" to location?.latitude,
                                                                "lng" to location?.longitude
                                                            ),
                                                            "description" to data1["description"],
                                                            "services" to data1["services"],
                                                            "description_photos" to hashMapOf<String,Any>(
                                                                "0" to hashMapOf<String, Any>(
                                                                    "url" to urls[0],
                                                                    "path" to paths[0]
                                                                ),
                                                                "1" to hashMapOf<String, Any>(
                                                                    "url" to urls[1],
                                                                    "path" to paths[1]
                                                                ),
                                                                "2" to hashMapOf<String, Any>(
                                                                    "url" to urls[2],
                                                                    "path" to paths[2]
                                                                ),
                                                                "3" to hashMapOf<String, Any>(
                                                                    "url" to urls[3],
                                                                    "path" to paths[3]
                                                                )
                                                            )
                                                        )
                                                        db.collection("freelancers").document(docId)
                                                            .set(data, SetOptions.merge())
                                                            .addOnSuccessListener { documentReference ->
//                                                                loadingDailog.dismissDialog()
                                                                editDone.postValue(true)
                                                            }
                                                            .addOnFailureListener { e ->
//                                                                loadingDailog.dismissDialog()
                                                                Log.w("", "Error adding document", e)
                                                            }

                                                        //changing their names in chats document
                                                        db.collection("chats").whereEqualTo("user_1.uid",userId).get()
                                                            .addOnSuccessListener { snap->
                                                                if (snap.documents.size != 0){
                                                                    var nameChange = hashMapOf<String,Any?>(
                                                                        "user_1" to hashMapOf<String,Any?>(
                                                                            "name" to data1["name"],
                                                                            "profile_photo" to snap.documents[0].get("user_1.profile_photo"),
                                                                            "uid" to userId
                                                                        )
                                                                    )
                                                                    for (i in 0..snap.documents.size-1){
                                                                        db.collection("chats").document(snap.documents[i].id).set(nameChange, SetOptions.merge())
                                                                            .addOnSuccessListener {
//                                                                                if (i == snap.documents.size-1){
//                                                                                    editDone.postValue(true)
//                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                            }
                                                        db.collection("chats").whereEqualTo("user_2.uid",userId).get()
                                                            .addOnSuccessListener { snap->
                                                                if (snap.documents.size != 0){
                                                                    var nameChange = hashMapOf<String,Any?>(
                                                                        "user_2" to hashMapOf<String,Any?>(
                                                                            "name" to data1["name"],
                                                                            "profile_photo" to snap.documents[0].get("user_2.profile_photo"),
                                                                            "uid" to userId
                                                                        )
                                                                    )
                                                                    for (i in 0..snap.documents.size-1){
                                                                        db.collection("chats").document(snap.documents[i].id).set(nameChange, SetOptions.merge())
                                                                            .addOnSuccessListener {
//                                                                                if (i == snap.documents.size-1){
//                                                                                    editDone.postValue(true)
//                                                                                }
                                                                            }
                                                                    }
                                                                }                                                            }
                                                    }
                                                }

                                        }.addOnFailureListener{
                                            loadingDailog.dismissDialog()
                                            Toast.makeText(context, "Upload failed, try again", Toast.LENGTH_SHORT)
                                        }
                                }
                            }
                        }
                }
            }
        }
    }
}
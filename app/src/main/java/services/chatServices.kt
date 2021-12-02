package services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.snap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.errorprone.annotations.Var
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import data_models.userData
import fragments.loadingDailog
import java.util.*

class chatServices {

    //list of chats for the user
    val chatList : MutableLiveData<List<DocumentSnapshot>> by lazy {
        MutableLiveData<List<DocumentSnapshot>>()
    }
    // no of unread messages per chat
    val unreadMessagesNoList : MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }
//    list of messages in the chat
    val messagesList : MutableLiveData<List<DocumentSnapshot>> by lazy {
        MutableLiveData<List<DocumentSnapshot>>()
    }

    val messagesMap : MutableLiveData<Map<String,Any>> by lazy {
        MutableLiveData<Map<String,Any>>()
    }


    // getting the list of chats for the user
    fun getChats(context: Context){
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val loadingDailog = loadingDailog(context)
        db.collection("chats").whereArrayContains("users",userId.toString())
            .orderBy("time_last",Query.Direction.DESCENDING)
            .addSnapshotListener{
            snapshot,e  ->
            if (e != null) {
                Log.w("", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                Log.d("in getchats", "$userId ")
                var list = snapshot?.documents
//                chatList.postValue(list)
//                chatList.value?.get(0)?.id
//                Log.i("chats list","${chatList.value}")
                //to get no of unread messages
                for(doc in list){
                    Log.i("chats list","${list.size} , these are index values")
                    db.collection("chats").document(doc.id).collection("messages")
                        .whereEqualTo("receiver_id", userId)
                        .whereEqualTo("is_seen",false).addSnapshotListener{
                            snapshot,e->
//                            getUnreadMessagesNo(list)
                            val noOfUnreadMessages : MutableList<Int> = arrayListOf()
                            for(i in 0..list.size-1){
                                var doc2 = list[i]
                                db.collection("chats").document(doc2.id).collection("messages")
                                    .whereEqualTo("receiver_id", userId)
                                    .whereEqualTo("is_seen",false).get().addOnSuccessListener {
                                            snapshot ->
                                        var unreadMessages : Int= 0
                                        if (snapshot != null){
                                            unreadMessages = snapshot.documents.size
                                            noOfUnreadMessages.add(unreadMessages)
//                                            unreadMessagesNoList.postValue(noOfUnreadMessages)
                                            Log.i("unreadmessages","in no of unreadMessages snapshot listener if ${noOfUnreadMessages}")
                                        }else{
                                            noOfUnreadMessages.add(0)
//                                            unreadMessagesNoList.postValue(noOfUnreadMessages)
                                            Log.i("unread else","in no of unreadMessages snapshot listener ${noOfUnreadMessages}")
                                        }
                                        if (i == list.size-1){
                                            val map = hashMapOf<String,Any>(
                                                "chat_list" to list,
                                                "unread_messages" to noOfUnreadMessages,
                                            )
                                            messagesMap.postValue(map)
                                        }
                                    }
                            }

                        }
                }
            } else {
                Log.d("", "data: null")
            }

        }
    }

    //get unread messages count
//    fun getUnreadMessagesNo(list : MutableList<DocumentSnapshot>){
//        val db = FirebaseFirestore.getInstance()
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//        val noOfUnreadMessages : MutableList<Int> = arrayListOf()
////        for(doc in list){
////            db.collection("chats").document(doc.id).collection("messages")
////                .whereEqualTo("receiver_id", userId)
////                .whereEqualTo("is_seen",false).get().addOnSuccessListener {
////                    snapshot ->
////                    var unreadMessages : Int= 0
////                    if (snapshot != null){
////                        unreadMessages = snapshot.documents.size
////                        noOfUnreadMessages.add(unreadMessages)
////                        unreadMessagesNoList.postValue(noOfUnreadMessages)
////                        Log.i("unreadmessages","in no of unreadMessages snapshot listener if ${unreadMessagesNoList.value}")
////                    }else{
////                        noOfUnreadMessages.add(0)
////                        unreadMessagesNoList.postValue(noOfUnreadMessages)
////                        Log.i("unread","in no of unreadMessages snapshot listener ${unreadMessagesNoList.value}")
////                    }
////                }
////        }
//
//    }

    //get the list of messages for a specific chat

//    fun getChatMessages(id : String){
//        val db = FirebaseFirestore.getInstance()
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//        db.collection("chats").document(id).collection("messages").orderBy("time_start")
//            .addSnapshotListener{ snapshot,e ->
//                if (e != null) {
//                    Log.w("", "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//                if (snapshot != null) {
//                    Log.d("", " data: ${snapshot.metadata}")
//                    var list = snapshot?.documents
//                    messagesList.postValue(list)
//                    messagesList.value?.get(0)?.id
//                    Log.i("in get  chat messages","")
//
//                } else {
//                    Log.d("", "data: null")
//                }
//
//
//            }
//    }

    //sending messages
    fun sendMessages(message: String, id : String,receivingUser: String, type : String){
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        Log.i("","in sendMessages function")
        var data = hashMapOf<String, Any?>(
            "message" to message,
            "time_start" to Timestamp(Date()),
            "sender_id" to userId,
            "receiver_id" to receivingUser,
            "type" to type,
            "is_seen" to false
        )
        val timeUpdate = hashMapOf<String,Any?>(
            "time_last" to Timestamp(Date())
        )

        db.collection("chats").document(id).collection("messages").add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("", "Message sent with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("", "Error adding document", e)
            }
        // updating the time of last message in document
        db.collection("chats").document(id).set(timeUpdate, SetOptions.merge())
    }

    // for uploading images to chat
    fun uploadImage(imageBytes : ByteArray,id : String,receivingUser: String, context: Context){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        var storageRef = FirebaseStorage.getInstance().reference
        var rString = System.currentTimeMillis().toString()
        val loadingDailog : loadingDailog = loadingDailog(context)
        var storageFileName = ""

        loadingDailog.showDialog()
        db.collection("chats").document(id.toString()).get()
            .addOnSuccessListener { snapshot->
                storageFileName = snapshot["user_1.uid"].toString() + "_" + snapshot["user_2.uid"].toString()
                storageRef.child("chat_images/$storageFileName/"+rString+".jpg").putBytes(imageBytes)
                    .addOnSuccessListener {
                        Log.i("","in cloud storage successs listerner")
                        storageRef.child("chat_images/$storageFileName/"+rString+".jpg").downloadUrl
                            .addOnSuccessListener {uri->
                                Log.i("printing image uri","$uri")
                                var data = hashMapOf<String, Any?>(
                                    "message" to uri.toString(),
                                    "time_start" to Timestamp(Date()),
                                    "sender_id" to userId,
                                    "receiver_id" to receivingUser,
                                    "image_reference" to "chat_images/"+rString+".jpg",
                                    "image_path" to "chat_images/$storageFileName/"+rString+".jpg",
                                    "type" to "IMAGE"
                                )

                                db.collection("chats").document(id).collection("messages").add(data)
                                    .addOnSuccessListener { documentReference ->
                                        loadingDailog.dismissDialog()
                                        Log.d("", "Message sent with ID: ${documentReference.id}")
                                    }
                                    .addOnFailureListener { e ->
                                        loadingDailog.dismissDialog()
                                        Log.w("", "Error adding document", e)
                                    }
                            }

                    }.addOnFailureListener{
                        loadingDailog.dismissDialog()
                        Toast.makeText(context, "Upload failed, try again",Toast.LENGTH_SHORT)
                    }

            }


    }


    // return chat list
    fun returnChatList(): MutableLiveData<List<DocumentSnapshot>>{
        return chatList
    }

//    returning messages list
    fun returnMessagesList(): MutableLiveData<List<DocumentSnapshot>>{
        return messagesList
    }

    // returning no of unread messages list
    fun returnUnreadMessagesNo(): MutableLiveData<List<Int>>{
        return unreadMessagesNoList
    }

    fun returnMessagesMap(): MutableLiveData<Map<String,Any>>{
        return messagesMap
    }
}


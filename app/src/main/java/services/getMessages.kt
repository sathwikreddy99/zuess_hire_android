package services

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import data_models.userData
import java.util.*

class getMessages {

    //list of chats for the user
    val chatList : MutableLiveData<List<DocumentSnapshot>> by lazy {
        MutableLiveData<List<DocumentSnapshot>>()
    }
//    list of messages in the chat
    val messagesList : MutableLiveData<List<DocumentSnapshot>> by lazy {
        MutableLiveData<List<DocumentSnapshot>>()
    }
//    receiving user Id
    val receivingUserId : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // getting the list of chats for the user
    fun getChats(){
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("chats").whereArrayContains("users","${userId}").orderBy("time_last")
            .addSnapshotListener{
            snapshot,e  ->
            if (e != null) {
                Log.w("", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                Log.d("", "data: ${snapshot.metadata}")
                var list = snapshot?.documents
                chatList.postValue(list)
                chatList.value?.get(0)?.id
            } else {
                Log.d("", "data: null")
            }

        }
    }

    //get the list of messages for a specific chat

    fun getChatMessages(id : String){
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("chats").document(id).collection("messages").orderBy("time_start")
            .addSnapshotListener{ snapshot,e ->
                if (e != null) {
                    Log.w("", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d("", " data: ${snapshot.metadata}")
                    var list = snapshot?.documents
                    messagesList.postValue(list)
                    messagesList.value?.get(0)?.id
                    Log.i("in get  chat messages","")

                } else {
                    Log.d("", "data: null")
                }


            }
    }

    //sending messages
    fun sendMessages(message: String, id : String,receivingUser: String, type : String){
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val receivingUserNo = getReceivingUser(id)

        Log.i("","in sendMessages function")
        var data = hashMapOf<String, Any?>(
            "message" to message,
            "time_start" to Timestamp(Date()),
            "sender_id" to userId,
            "receiver_id" to receivingUser,
            "type" to type
        )

        db.collection("chats").document(id).collection("messages").add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("", "Message sent with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("", "Error adding document", e)
            }
    }

    //getting the reciever information
    fun getReceivingUser(id: String): MutableLiveData<Int>{
        val db = FirebaseFirestore.getInstance()
        val receivingUser : MutableLiveData<Int> by lazy {
            MutableLiveData<Int>()
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("chats").document(id).get()
            .addOnSuccessListener { document ->
                if(document["user_1.uid"]== userId){
                    receivingUser.postValue(2)
                    Log.i("","in document listener : $receivingUser")
                }else{
                    receivingUser.postValue(1)
                }
            }
        Log.i("","receiving user : $receivingUser")
        return receivingUser
    }

    fun getReceivingUserId(id: String){
        val db = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("chats").document(id).get()
            .addOnSuccessListener { document ->
                if(document["user_1.uid"]== userId){
                    receivingUserId.postValue(document["user_2.uid"].toString())
                    Log.i("","in document listener : $receivingUserId")
                }else{
                    receivingUserId.postValue(document["user_1.uid"].toString())
                }
            }
        Log.i("","receiving user : $receivingUserId")
    }

    // return chat list
    fun returnChatList(): MutableLiveData<List<DocumentSnapshot>>{
        return chatList
    }

//    returning messages list
    fun returnMessagesList(): MutableLiveData<List<DocumentSnapshot>>{
        return messagesList
    }
}


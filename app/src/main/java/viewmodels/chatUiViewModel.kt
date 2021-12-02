package viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.zuess.zuess_android.R

class chatUiViewModel : ViewModel(){

    val messagesList : MutableLiveData<List<DocumentSnapshot>> by lazy {
        MutableLiveData<List<DocumentSnapshot>>()
    }

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
//                    messagesList.value?.get(0)?.id
                    Log.i("in get  chat messages","$list")

                } else {
                    Log.d("", "data: null")
                }


            }
    }
}

// chatUi recycler view adapter
class chatAdapter(val messageDocs : List<DocumentSnapshot>, val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val ITEM_SENT_MESSAGE = 1
    val ITEM_RECIEVE_MESSAGE = 2
    val ITEM_SENT_IMAGE = 3
    val ITEM_RECEIVE_IMAGE = 4



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sender_message_item,parent,false)
            return senderMessageViewHolder(view)
        }else if (viewType == 2){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.receiver_message_item,parent,false)
            return receiverMessageViewHolder(view)
        }else if(viewType ==3){
            Log.i("","in receiver image item")
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sender_image_item,parent,false)
            return senderImageViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.receiver_image_item,parent,false)
            Log.i("","in receiver image item")
            return receiverImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageDocs[position]
        if(holder.javaClass == senderMessageViewHolder::class.java){
            val viewHolder = holder as senderMessageViewHolder
            holder.senderMessage.text = currentMessage["message"].toString()
        }
        else if(holder.javaClass == receiverMessageViewHolder::class.java){
            val viewHolder = holder as receiverMessageViewHolder
            holder.receiverMessage.text = currentMessage["message"].toString()
        }
        else if(holder.javaClass == receiverImageViewHolder::class.java){
            val viewHolder = holder as receiverImageViewHolder
            Glide.with(context).load(currentMessage["message"]).placeholder(R.drawable.user).into(holder.receiverImage)
//            Picasso.get().load(currentMessage["message"].toString())
//                .placeholder(R.drawable.user)
//                .into(holder.receiverImage)


        }else if(holder.javaClass == senderImageViewHolder::class.java){
            val viewHolder = holder as senderImageViewHolder
            Glide.with(context).load(currentMessage["message"]).placeholder(R.drawable.user).into(holder.senderImage)
//            Picasso.get().load(currentMessage["message"].toString())
//                .placeholder(R.drawable.user)
//                .into(holder.senderImage)
        }

//        Log.i("","in binding view holder ${currentMessage["message"].toString()}")


    }

    override fun getItemCount(): Int {
        return messageDocs.size
    }

    override fun getItemViewType(position: Int): Int {
        val currMessage = messageDocs[position]
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId.toString() == currMessage["sender_id"].toString()){
            if(currMessage["type"]=="TEXT"){
                return ITEM_SENT_MESSAGE
            }else{
//                Log.i("********","item image message")
                return ITEM_SENT_IMAGE
            }
        }else{
            if(currMessage["type"]== "TEXT"){
                return  ITEM_RECIEVE_MESSAGE
            }else{
                return ITEM_RECEIVE_IMAGE
            }
        }

    }


    // all view holder classes
    //sender message item
    class senderMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderMessage = itemView.findViewById<TextView>(R.id.sender_message_text_view)
    }
    //sender image item
    class senderImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderImage = itemView.findViewById<ImageView>(R.id.senderMessageImageView)
    }

    //receiver message item
    class receiverMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receiverMessage = itemView.findViewById<TextView>(R.id.receiver_message_text_view)
    }
    //receiver image item
    class receiverImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receiverImage = itemView.findViewById<ImageView>(R.id.receiverMessageImageView)
    }
}
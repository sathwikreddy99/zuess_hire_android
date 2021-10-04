package viewmodels

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import fragments.chatsListUi
import fragments.chatsListUiDirections
import services.getMessages
import java.util.concurrent.RecursiveAction

class chatListUiViewModel: ViewModel(){

    val getMessages : getMessages = getMessages()
    var chatList: MutableLiveData<List<DocumentSnapshot>> = getMessages.returnChatList()
    var messagesList : MutableLiveData<List<DocumentSnapshot>> = getMessages.returnMessagesList()
    val name : MutableLiveData<String> by lazy {MutableLiveData<String>()}
    val picture : MutableLiveData<String> by lazy {MutableLiveData<String>()}
}




// adapter for displaying list of chats
class chatListUiAdapter(
    val context: Context?,
    val givenList: List<DocumentSnapshot>,
    val communicator: FragmentCommunication
): RecyclerView.Adapter<chatListUiAdapter.viewHolder>() {

    class viewHolder(itemView: View, communicator: FragmentCommunication) : RecyclerView.ViewHolder(itemView) {
        var layout = itemView.findViewById<ConstraintLayout>(R.id.chatListItem)
        var name = itemView.findViewById<TextView>(R.id.chatListItemName)
        var extra = itemView.findViewById<TextView>(R.id.chatListItemExtra)
        var image = itemView.findViewById<ImageView>(R.id.chatListItemImage)


    }

    interface FragmentCommunication{
        fun getInfo(docId: String, name: String , picture: String, receivingUid : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item,parent,false)
        return chatListUiAdapter.viewHolder(view, communicator)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentListItem = givenList[position]
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        var getMessages : getMessages = getMessages()
        var receivingUid = ""
        var receivingUserNo : Int = 0

//        checking which user is the receiving user
        if(currentListItem["user_1.uid"]==userId){
            holder.name.text = currentListItem["user_2.name"].toString()
            receivingUid = currentListItem["user_2.uid"].toString()
            receivingUserNo = 2
            Log.i("****","${currentListItem["user_2"]}")
            if (context != null) {
                Glide.with(context).load(currentListItem["user_2.profile_picture"])
                    .placeholder(R.drawable.driver)
                    .into(holder.image)
            }
        }else{
            holder.name.text = currentListItem["user_1.name"].toString()
            receivingUid = currentListItem["user_1.uid"].toString()
            receivingUserNo = 1
            Log.i("****","${currentListItem["user_1.profile_picture"]}")
            if (context != null) {
                Glide.with(context).load(currentListItem["user_1.profile_picture"])
                    .placeholder(R.drawable.driver)
                    .into(holder.image)
            }

        }

        //setting on click listener when certain chat is pressed
        holder.itemView.setOnClickListener {view->
            if(currentListItem["user_1.uid"]==userId){
                val name = currentListItem["user_2.name"].toString()
                val receivingUid = currentListItem["user_2.uid"].toString()
                val profilePicture = currentListItem["user_2.profile_picture"].toString()
                communicator.getInfo(currentListItem.id,name,profilePicture,receivingUid)

            }else{
                val name = currentListItem["user_1.name"].toString()
                val receivingUid = currentListItem["user_1.uid"].toString()
                val profilePicture = currentListItem["user_1.profile_picture"].toString()
                communicator.getInfo(currentListItem.id,name,profilePicture,receivingUid)

            }
//            view.findNavController().navigate(chatsListUiDirections.actionChatsListUiToChatUi2())
        }

    }

    override fun getItemCount(): Int {
        return givenList.size
    }




}



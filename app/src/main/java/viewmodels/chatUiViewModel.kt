package viewmodels

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.zuess.zuess_android.R



// chatUi recycler view adapter
class chatAdapter(val messageDocs : List<DocumentSnapshot>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val ITEM_SENT = 1
    val ITEM_RECIEVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sender_message_item,parent,false)
            return senderViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.receiver_message_item,parent,false)
            return receiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageDocs[position]
        if(holder.javaClass == senderViewHolder::class.java){
            val viewHolder = holder as senderViewHolder
            holder.senderMessage.text = currentMessage["message"].toString()
        }else{
            val viewHolder = holder as receiverViewHolder
            holder.recieverMessage.text = currentMessage["message"].toString()
        }
        Log.i("","in binding view holder ${currentMessage["message"].toString()}")


    }

    override fun getItemCount(): Int {
        return messageDocs.size
    }

    override fun getItemViewType(position: Int): Int {
        val currMessage = messageDocs[position]
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId.toString() == currMessage["sender_id"].toString()){
            return ITEM_SENT
        }else{
            return ITEM_RECIEVE
        }

    }


    class senderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderMessage = itemView.findViewById<TextView>(R.id.sender_message_text_view)
    }

    class receiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recieverMessage = itemView.findViewById<TextView>(R.id.receiver_message_text_view)
    }
}
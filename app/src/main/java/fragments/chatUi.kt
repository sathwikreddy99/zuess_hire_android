package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zuess.zuess_android.R
import viewmodels.chatAdapter
import viewmodels.chatListUiAdapter
import viewmodels.chatListUiViewModel


class chatUi : Fragment() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializing variables
        val chatUiRecyclerView = view.findViewById<RecyclerView>(R.id.chatUiRecyclerView)
        val listModel: chatListUiViewModel by viewModels()
        val sendMessage = view.findViewById<EditText>(R.id.sendMessageText)
        val sendMessageButton = view.findViewById<Button>(R.id.sendMessageButton)
        val backButton = view.findViewById<ImageView>(R.id.chatUiBackButton)
        val profilePictureView = view.findViewById<ImageView>(R.id.chatUiProfilePicture)
        val nameTextView = view.findViewById<TextView>(R.id.chatUiName)
        val name = arguments?.get("name")
        var pictureUrl = arguments?.get("picture")
        var docId = arguments?.get("docId")
        var receiverUid = arguments?.get("receivingUserId")

        nameTextView.text = name.toString()
        Glide.with(this).load(pictureUrl).into(profilePictureView)
        listModel.getMessages.getChatMessages("GsGFZGaQnT2ypp1rvfAx")


        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        chatUiRecyclerView.setLayoutManager(LinearLayoutManager(context));



        listModel.messagesList.observe(viewLifecycleOwner, Observer { list ->
            var adapter: chatAdapter = chatAdapter(list)
            chatUiRecyclerView.adapter = adapter
        })

        sendMessageButton.setOnClickListener {

            listModel.getMessages.sendMessages(sendMessage.text.toString(),docId.toString(),
                receiverUid.toString(),"TEXT")

            sendMessage.text.clear()
        }
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }


}
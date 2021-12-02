package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import viewmodels.chatListUiAdapter
import viewmodels.chatListUiViewModel


class chatsListUi : Fragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats_list_ui, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val db = FirebaseFirestore.getInstance()


        val loadingDailog = loadingDailog(requireContext())
        //change it afterwards
        //chats fragment navigates to login when user is not logged in
        if(FirebaseAuth.getInstance().currentUser == null){
            navController.navigate(chatsListUiDirections.actionChatsListUiToLoginui())
        }
        //bottom navbar implementation
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbar)
        bottomNavbar.setupWithNavController(navController)
        //        NavigationUI.setupWithNavController(bottomNavbar,navController)
        //this is to implement back press for bottom navigation view
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    // Do custom work here
                    bottomNavbar.selectedItemId = R.id.searchUi
                    // if you want onBackPressed() to be called as normal afterwards
//                    if (isEnabled) {
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    }
                }
            })


        val viewModel : chatListUiViewModel by viewModels()
        val chatListRecyclerView = view.findViewById<RecyclerView>(R.id.chatListRecyclerView)
        viewModel.getMessages.getChats(requireContext())

        chatListRecyclerView.setLayoutManager(LinearLayoutManager(context));

//        viewModel.chatList.observe(viewLifecycleOwner, Observer {
//            value ->
//            viewModel.unreadMessages.observe(viewLifecycleOwner,Observer{
//                noOfUnreadMessages->
//                var adapter : chatListUiAdapter = chatListUiAdapter(this.context,value,noOfUnreadMessages,communication)
//                chatListRecyclerView.adapter =adapter
//            })
//
//        })
        viewModel.messagesMap.observe(viewLifecycleOwner, Observer {
            value ->
            val chatList = value["chat_list"] as List<DocumentSnapshot>
            val noOfUnreadMessages = value["unread_messages"] as List<Int>
//            Log.i("chat values", "${chatList} , $noOfUnreadMessages")
            var adapter : chatListUiAdapter = chatListUiAdapter(this.context,chatList,noOfUnreadMessages,communication)
                chatListRecyclerView.adapter =adapter
        })


    }

    // to go to chatui on selecting a specific chat from the list
    var communication: chatListUiAdapter.FragmentCommunication = object :
        chatListUiAdapter.FragmentCommunication {
        override fun getInfo(docId: String, name: String, picture: String, receivingUid: String) {
            val action = chatsListUiDirections.actionChatsListUiToChatUi(name,picture,docId,receivingUid)
            view?.findNavController()?.navigate(action)
        }
    }


}
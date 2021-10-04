package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
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

        //change it afterwards
        //chats fragment navigates to login when user is not logged in
        if(FirebaseAuth.getInstance().currentUser == null){
            navController.navigate(chatsListUiDirections.actionChatsListUiToLoginui())
        }
        //bottom navbar implementation
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbar)
        NavigationUI.setupWithNavController(bottomNavbar,navController)

        val viewModel : chatListUiViewModel by viewModels()
        val chatListRecyclerView = view.findViewById<RecyclerView>(R.id.chatListRecyclerView)
        viewModel.getMessages.getChats()

        chatListRecyclerView.setLayoutManager(LinearLayoutManager(context));

        viewModel.chatList.observe(viewLifecycleOwner, Observer {
            value ->
            var adapter : chatListUiAdapter = chatListUiAdapter(this.context,value,communication)
            chatListRecyclerView.adapter =adapter

        })


    }

    var communication: chatListUiAdapter.FragmentCommunication = object :
        chatListUiAdapter.FragmentCommunication {
        override fun getInfo(docId: String, name: String, picture: String, receivingUid: String) {
            val action = chatsListUiDirections.actionChatsListUiToChatUi2(name,picture,docId,receivingUid)
            view?.findNavController()?.navigate(action)
        }
    }



}
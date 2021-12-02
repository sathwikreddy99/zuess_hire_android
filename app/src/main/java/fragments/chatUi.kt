package fragments

import android.content.ContentResolver
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zuess.zuess_android.R
import viewmodels.chatAdapter
import viewmodels.chatListUiAdapter
import viewmodels.chatListUiViewModel
import android.content.Intent
import android.database.Cursor
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult

import androidx.core.app.ActivityCompat.startActivityForResult
import android.provider.MediaStore

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import io.ktor.http.*
import viewmodels.chatUiViewModel
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor


class chatUi : Fragment() {

    var docId1 : Any? = null
    val db = FirebaseFirestore.getInstance()
    lateinit var listener : ListenerRegistration
    val userId = FirebaseAuth.getInstance().currentUser?.uid

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
        val sendMessage = view.findViewById<EditText>(R.id.sendMessageText)
        val sendMessageButton = view.findViewById<Button>(R.id.sendMessageButton)
        val backButton = view.findViewById<ImageView>(R.id.chatUiBackButton)
        val profilePictureView = view.findViewById<ImageView>(R.id.chatUiProfilePicture)
        val nameTextView = view.findViewById<TextView>(R.id.chatUiName)
        var storageFileName = ""

        val listModel: chatListUiViewModel by viewModels()
        val chatUiModel : chatUiViewModel by viewModels()

        //getting values from arguments passed during navigation
        val name = arguments?.get("name")
        var pictureUrl = arguments?.get("picture")
        var docId = arguments?.get("docId")
        var receiverUid = arguments?.get("receivingUserId")


        //setting all the unread messages as read
        readMessages(docId.toString(),false)
        docId1 = docId



        // setting up the views in chatUI
        nameTextView.text = name.toString()
        Glide.with(this).load(pictureUrl).into(profilePictureView)
        //intializing to get list of messages
        chatUiModel.getChatMessages(docId.toString())

        // setting layout manger for chatui recycler view
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        chatUiRecyclerView.setLayoutManager(LinearLayoutManager(context));


        // listening to list of documents in messages collection of firestore
        chatUiModel.messagesList.observe(viewLifecycleOwner, Observer { list ->
            var adapter: chatAdapter = chatAdapter(list, this.requireContext())
            chatUiRecyclerView.adapter = adapter
            Log.i("chat size","${list.size}")
            Handler(Looper.getMainLooper())
                .postDelayed({ chatUiRecyclerView.scrollToPosition(list.size-1) }, 500)
        })

        // sending message
        sendMessageButton.setOnClickListener {
            //null checker
            if (sendMessage.text.toString() != null){
                listModel.getMessages.sendMessages(sendMessage.text.toString(),docId.toString(),
                    receiverUid.toString(),"TEXT")
            }


            sendMessage.text.clear()
        }
        //implementing back button
        backButton.setOnClickListener {
            readMessages("",true)
            requireActivity().onBackPressed()
        }

        //selecting images from gallery
        val selectImages = view?.findViewById<Button>(R.id.chatUiSelectImage)

        // select images from gallery
        val getImages = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                    result->
                if(result.data?.clipData != null){
                    if(result.data?.clipData!!.itemCount > 10){
                        Snackbar.make(view,"select 10 or less photos",2000).show()
                        Toast.makeText(requireContext(),"select 10 or less photos",Toast.LENGTH_LONG).show()
                    }else{
                        for(i in 0..(result.data!!.clipData!!.itemCount-1)){
                            var uri = result.data!!.clipData!!.getItemAt(i).uri
                            val filePath = arrayOf(MediaStore.Images.Media.DATA)
                            val cursor: Cursor? =
                                context?.contentResolver?.query(uri, filePath, null, null, null)
                            cursor?.moveToFirst()
                            val imagePath: String = cursor!!.getString(cursor.getColumnIndex(filePath[0]))

                            val options = BitmapFactory.Options()
                            options.inJustDecodeBounds = false

                            try{
                                var parcelFD = context?.contentResolver?.openFileDescriptor(uri, "r")
                                val imageSource: FileDescriptor = parcelFD!!.getFileDescriptor()
//                        val bitm = BitmapFactory.decodeFile(imagePath, options)
                                val bitmap : Bitmap = BitmapFactory.decodeFileDescriptor(imageSource)
//                        Log.i("bitmap decode", "$bitmap")
                                Log.i("bitmap decode", "$bitmap")
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byteArray: ByteArray = stream.toByteArray()
                                listModel.getMessages.uploadImage(byteArray,docId.toString(),receiverUid.toString(), this.requireContext())
                                Log.i("image uri :","$byteArray, $uri ,  $imagePath")

                            }catch (e : OutOfMemoryError){
                                Log.i("out of memory","$e")
                            }


                        }
                    }

                }
                Log.i("get media data","${result.data},******** ${result.data?.clipData?.getItemAt(0)?.uri}")

            })

        //selecting images from gallery
        selectImages?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Log.i("","in select images listener")
            Toast.makeText(requireContext(),"choose from gallery",Toast.LENGTH_LONG)
            //passing the intent for launch
            getImages.launch(Intent.createChooser(intent,"select picture"))

        }


//        handling android back button
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                readMessages("",true)
                requireActivity().onBackPressed()
                true
            } else false
        }

    }

    override fun onStop() {
        super.onStop()
        readMessages("",true)
    }

    override fun onResume() {
        super.onResume()
        readMessages(docId1.toString(),false)
        Log.i("","in on resume chat ui $docId1")
    }

    override fun onDestroy() {
        super.onDestroy()
        readMessages("",true)
    }

    //making all the unread messages as read when the chat is opened
    fun readMessages(id: String, destroy : Boolean){

        if (destroy == true){
            listener.remove()
            Log.i("","destroying listener")
        }else{
            listener = db.collection("chats").document(id).collection("messages")
                .whereEqualTo("receiver_id",userId)
                .whereEqualTo("is_seen",false)
                .addSnapshotListener { value, error ->
                    Log.i("","listener working")
                    val listOfDocs = value?.documents
                    if (listOfDocs != null) {
                        for (doc in listOfDocs){
                            var setAsSeen = hashMapOf<String,Boolean>(
                                "is_seen" to true
                            )
                            db.collection("chats").document(id).collection("messages")
                                .document(doc.id).set(setAsSeen, SetOptions.merge())
                        }
                    }
                }
        }
    }




}
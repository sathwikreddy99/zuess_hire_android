package fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.zuess.zuess_android.R
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor


class profilePageEdit : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_page_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val loadingDailog = loadingDailog(requireContext())
        var profilePhotoChanged = false
        var updateProfilePhoto : ByteArray? = null
        var docId = ""
        var storageRef = FirebaseStorage.getInstance().reference
        var profilePhotoExists : Boolean = false
        var profilePhotoPath = ""


        loadingDailog.showDialog()

        //components
        val backButton = view.findViewById<ImageView>(R.id.profilePageEditBackButton)
        val chooseProfilePhoto = view.findViewById<ImageView>(R.id.profilePageEditPhotoButton)
        val profilePhoto = view.findViewById<ImageView>(R.id.profilePageEditProfilePhoto)
        val firstName = view.findViewById<EditText>(R.id.profilePageEditFirstName)
        val lastName = view.findViewById<EditText>(R.id.profilePageEditLastName)
        val saveButton = view.findViewById<TextView>(R.id.profilePageEditSave)

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //getting user data
        db.collection("users").whereEqualTo("userId",userId.toString()).get()
            .addOnSuccessListener {
                snapshot ->
                val data = snapshot.documents[0]
                docId = data.id
                firstName.setText(data["first_name"].toString())
                lastName.setText(data["last_name"].toString())
                Picasso.get().load(data["profile_photo.url"].toString()).into(profilePhoto)

                Log.i("in db","in on create view db")
                if (data["profile_photo.url"] != null){
                    profilePhotoExists =true
                    profilePhotoPath = data["profile_photo.path"].toString()
                }
                loadingDailog.dismissDialog()
            }

        //select profile photo
        val getProfilePhoto = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result->
                if(result.data?.clipData != null){
                    if (result.data!!.clipData!!.itemCount == 1){
                        Log.i("","in get profile photo if statement")
                        var uri = result.data!!.clipData!!.getItemAt(0).uri
//                    val uri : Uri = Uri.EMPTY
                        profilePhotoChanged = true

                        try{
                            var parcelFD = context?.contentResolver?.openFileDescriptor(uri, "r")
                            val imageSource: FileDescriptor = parcelFD!!.getFileDescriptor()
                            val bitmap : Bitmap = BitmapFactory.decodeFileDescriptor(imageSource)
                            Log.i("bitmap decode", "$bitmap")
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val byteArray: ByteArray = stream.toByteArray()
                            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            profilePhoto.setImageBitmap(bitmap)

                            updateProfilePhoto = byteArray
                            Log.i("image uri :","$byteArray, $uri")

                        }catch (e : OutOfMemoryError){
                            Log.i("out of memory","$e")
                        }
                        Log.i("get media data","${result.data},******** ${result.data?.clipData?.getItemAt(0)?.uri}")
                    }

                }

            })

        chooseProfilePhoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//            Log.i("","in select images listener")
            //passing the intent for launch
            getProfilePhoto.launch(Intent.createChooser(intent,"picture"))
        }

        saveButton.setOnClickListener {
            loadingDailog.showDialog()
            if (!profilePhotoChanged){
                val data = hashMapOf<String,Any>(
                    "first_name" to firstName.text.toString(),
                    "last_name" to lastName.text.toString()
                )
                db.collection("users").document(docId).set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),"Updated successfully",Toast.LENGTH_LONG).show()
                        loadingDailog.dismissDialog()
                        requireActivity().onBackPressed()
                    }
            }else{
                if (profilePhotoExists){
                    //if profile photo already exists
                    storageRef.child(profilePhotoPath).delete()
                        .addOnSuccessListener {
                            updateProfilePhoto?.let { it1 ->
                                storageRef.child("users/$userId/"+"profile_photo"+".jpg").putBytes(
                                    it1
                                ).addOnSuccessListener {
                                    storageRef.child("users/$userId/"+"profile_photo"+".jpg").downloadUrl
                                        .addOnSuccessListener { url ->
                                            val data = hashMapOf<String,Any>(
                                                "first_name" to firstName.text.toString(),
                                                "last_name" to lastName.text.toString(),
                                                "profile_photo" to hashMapOf<String,Any>(
                                                    "url" to url.toString(),
                                                    "path" to "users/$userId/"+"profile_photo"+".jpg"
                                                )
                                            )
                                            db.collection("users").document(docId).set(data, SetOptions.merge())
                                                .addOnSuccessListener {
                                                    loadingDailog.dismissDialog()
                                                    Toast.makeText(requireContext(),"Updated successfully",Toast.LENGTH_LONG).show()
                                                    requireActivity().onBackPressed()

                                                }

                                        }

                                }
                            }

                        }
                }else{
                    // if profile photo does not alreay exist
                    updateProfilePhoto?.let { it1 ->
                        storageRef.child("users/$userId/"+"profile_photo"+".jpg").putBytes(
                            it1
                        ).addOnSuccessListener {
                            storageRef.child("users/$userId/"+"profile_photo"+".jpg").downloadUrl
                                .addOnSuccessListener { url ->
                                    val data = hashMapOf<String,Any>(
                                        "first_name" to firstName.text.toString(),
                                        "last_name" to lastName.text.toString(),
                                        "profile_photo" to hashMapOf<String,Any>(
                                            "url" to url.toString(),
                                            "path" to "users/$userId/"+"profile_photo"+".jpg"
                                        )
                                    )
                                    db.collection("users").document(docId).set(data, SetOptions.merge())
                                        .addOnSuccessListener {
                                            loadingDailog.dismissDialog()
                                            Toast.makeText(requireContext(),"Updated successfully",Toast.LENGTH_LONG).show()
                                            requireActivity().onBackPressed()
                                        }

                                }

                        }
                    }
                }


            }
        }
    }
}
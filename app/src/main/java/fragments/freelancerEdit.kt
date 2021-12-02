package fragments

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.algolia.search.model.places.Country
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import viewmodels.freelancerEditViewModel
import viewmodels.locationViewModel
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor


class freelancerEdit : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freelancer_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val loadingDailog : loadingDailog = loadingDailog(requireContext())
        var desPhotosChanged : Boolean = false
        var profilePhotoChanged : Boolean = false
        var dbexists : Boolean = false
        var updateProfilePhoto : ByteArray = byteArrayOf()
        var docId : String = ""
        val freelanceEditModel : freelancerEditViewModel by viewModels()
        val locationViewModel : locationViewModel by activityViewModels()
        var desPhotosList : MutableList<ByteArray> = arrayListOf()

        //location
        Log.i("location in freelancer edit","${locationViewModel.location.value}")
        //name
        var name = view.findViewById<EditText>(R.id.freelancerEditName)

        //buttons
        val locationButton = view.findViewById<Button>(R.id.freelancerEditUpdateLocation)
        val backButton = view.findViewById<ImageView>(R.id.freelancerEditBackButton)
        val save = view.findViewById<TextView>(R.id.freelancerEditSave)
        val uploadPhotos = view.findViewById<Button>(R.id.freelancerEditUploadButton)
        val profilePhotoButton = view.findViewById<Button>(R.id.freelancerEditProfilePhotoButton)

        //profile photo
        var profilePhoto = view.findViewById<ImageView>(R.id.freelancerEditProfilePhoto)
        //profile description
        var description = view.findViewById<EditText>(R.id.freelancerEditDescription)

        //jobtitles
        var jobTitle1 = view.findViewById<EditText>(R.id.freelancerEditJobTitle1)
        var jobTitle2 = view.findViewById<EditText>(R.id.freelancerEditJobTitle2)
        var jobTitle3 = view.findViewById<EditText>(R.id.freelancerEditJobTitle3)

        //uploaded images
        var uploadImage1 = view.findViewById<ImageView>(R.id.freelancerEditUploadImage1)
        var uploadImage2 = view.findViewById<ImageView>(R.id.freelancerEditUploadImage2)
        var uploadImage3 = view.findViewById<ImageView>(R.id.freelancerEditUploadImage3)
        var uploadImage4 = view.findViewById<ImageView>(R.id.freelancerEditUploadImage4)

        // Prices of services
        var price1 = view.findViewById<EditText>(R.id.freelancerEditPrice1)
        var price2 = view.findViewById<EditText>(R.id.freelancerEditPrice2)
        var price3 = view.findViewById<EditText>(R.id.freelancerEditPrice3)
        var price4 = view.findViewById<EditText>(R.id.freelancerEditPrice4)
        var price5 = view.findViewById<EditText>(R.id.freelancerEditPrice5)
        var price6 = view.findViewById<EditText>(R.id.freelancerEditPrice6)
        var price7 = view.findViewById<EditText>(R.id.freelancerEditPrice7)
        var price8 = view.findViewById<EditText>(R.id.freelancerEditPrice8)
        var price9 = view.findViewById<EditText>(R.id.freelancerEditPrice9)
        var price10 = view.findViewById<EditText>(R.id.freelancerEditPrice10)
        var price11 = view.findViewById<EditText>(R.id.freelancerEditPrice11)
        var price12 = view.findViewById<EditText>(R.id.freelancerEditPrice12)
        var price13 = view.findViewById<EditText>(R.id.freelancerEditPrice13)
        var price14 = view.findViewById<EditText>(R.id.freelancerEditPrice14)
        var price15 = view.findViewById<EditText>(R.id.freelancerEditPrice15)
        var listPrices: List<EditText> = listOf(price1,price2,price3,price4,price5,price6,
        price7,price8,price9,price10,price11,price12,price13,price14,price15)

        //services list
        var service1 = view.findViewById<EditText>(R.id.freelancerEditService1)
        var service2 = view.findViewById<EditText>(R.id.freelancerEditService2)
        var service3 = view.findViewById<EditText>(R.id.freelancerEditService3)
        var service4 = view.findViewById<EditText>(R.id.freelancerEditService4)
        var service5 = view.findViewById<EditText>(R.id.freelancerEditService5)
        var service6 = view.findViewById<EditText>(R.id.freelancerEditService6)
        var service7 = view.findViewById<EditText>(R.id.freelancerEditService7)
        var service8 = view.findViewById<EditText>(R.id.freelancerEditService8)
        var service9 = view.findViewById<EditText>(R.id.freelancerEditService9)
        var service10 = view.findViewById<EditText>(R.id.freelancerEditService10)
        var service11 = view.findViewById<EditText>(R.id.freelancerEditService11)
        var service12 = view.findViewById<EditText>(R.id.freelancerEditService12)
        var service13 = view.findViewById<EditText>(R.id.freelancerEditService13)
        var service14 = view.findViewById<EditText>(R.id.freelancerEditService14)
        var service15 = view.findViewById<EditText>(R.id.freelancerEditService15)
        var listServices = listOf<EditText>(service1,service2,service3,service4,service5,
            service6,service7,service8,service9,service10,
            service11,service12,service13,service14,service15)

        if (!freelanceEditModel.desPhotosList.isEmpty()){
            val img = freelanceEditModel.desPhotosList
            desPhotosChanged = true
            desPhotosList = freelanceEditModel.desPhotosList
            uploadImage1.setImageBitmap(BitmapFactory.decodeByteArray(img[0], 0, img[0].size))
            uploadImage2.setImageBitmap(BitmapFactory.decodeByteArray(img[1], 0, img[1].size))
            uploadImage3.setImageBitmap(BitmapFactory.decodeByteArray(img[2], 0, img[2].size))
            uploadImage4.setImageBitmap(BitmapFactory.decodeByteArray(img[3], 0, img[3].size))
        }
        if (freelanceEditModel.profilePhoto != null){
            profilePhotoChanged = true
            updateProfilePhoto = freelanceEditModel.profilePhoto!!
            val img = freelanceEditModel.profilePhoto!!
            profilePhoto.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.size))
        }

        //when edit is done
        freelanceEditModel.editDone.postValue(false)
        freelanceEditModel.editDone.observe(viewLifecycleOwner, Observer {
            value->
            if(value == true){
                loadingDailog.dismissDialog()
                navController.navigate(freelancerEditDirections.actionFreelancerEditToSettingsUi())
                Toast.makeText(requireContext(),"Update successful",Toast.LENGTH_LONG).show()
            }
        })

            loadingDailog.dismissDialog()
            loadingDailog.showDialog()
        db.collection("freelancers").whereEqualTo("uid",userId).get()
            .addOnCompleteListener {
                loadingDailog.dismissDialog()
            }
            .addOnSuccessListener {
                snapshot->
                if(snapshot.documents.size != 0){
                    val data = snapshot.documents.get(0)
                    docId = snapshot.documents.get(0).id
                    if (data != null){

                        dbexists = true

                        name.setText(data["name"].toString())
                        //loading images
                        Glide.with(requireContext()).load(data["description_photos.0.url"]).into(uploadImage1)
                        Glide.with(requireContext()).load(data["description_photos.1.url"]).into(uploadImage2)
                        Glide.with(requireContext()).load(data["description_photos.2.url"]).into(uploadImage3)
                        Glide.with(requireContext()).load(data["description_photos.3.url"]).into(uploadImage4)

                        //profile photo
                        Glide.with(requireContext()).load(data["profile_photo.url"]).into(profilePhoto)

                        //setting job titles
                        jobTitle1.setText(data["job_titles.0"].toString())
                        jobTitle2.setText(data["job_titles.1"].toString())
                        jobTitle3.setText(data["job_titles.2"].toString())

                        //setting services name
                        for (i in 0..14){
                            if (data["services.$i.name"] != null){
                                listPrices[i].setText(data["services.$i.price"].toString() )
                                listServices[i].setText(data["services.$i.name"].toString() )
                            }
                        }

                        //setting description
                        description.setText(data["description"].toString())

                    }
                }

            }.addOnFailureListener{
                Snackbar.make(view,"Load failed",2000).show()
            }


        // select four description images from gallery
        val getDescritionImages = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                    result->
                if(result.data?.clipData != null){
                    if (result.data!!.clipData!!.itemCount != 4){
                        Toast.makeText(context,"4 photos should be selected",Toast.LENGTH_LONG).show()
                    }else{
                        for(i in 0..(result.data!!.clipData!!.itemCount-1)){
                            var uri = result.data!!.clipData!!.getItemAt(i).uri
                            desPhotosChanged = true

                            try{
                                var parcelFD = context?.contentResolver?.openFileDescriptor(uri, "r")
                                val imageSource: FileDescriptor = parcelFD!!.getFileDescriptor()
                                val bitmap : Bitmap = BitmapFactory.decodeFileDescriptor(imageSource)
                                Log.i("bitmap decode", "$bitmap")
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byteArray: ByteArray = stream.toByteArray()
                                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                desPhotosList.add(byteArray)
                                if (i == 0){
                                    uploadImage1.setImageBitmap(bmp)
                                }else if (i == 1){
                                    uploadImage2.setImageBitmap(bmp)
                                }else if (i == 2){
                                    uploadImage3.setImageBitmap(bmp)
                                }else{
                                    uploadImage4.setImageBitmap(bmp)
                                    freelanceEditModel.desPhotosList = desPhotosList
                                }
//                            listModel.getMessages.uploadImage(byteArray,docId.toString(),receiverUid.toString(), this.requireContext())
                                Log.i("image uri :","$byteArray, $uri")

                            }catch (e : OutOfMemoryError){
                                Log.i("out of memory","$e")
                            }


                        }
                    }
                }

                Log.i("get media data","${result.data},******** ${result.data?.clipData?.getItemAt(0)?.uri}")

            })

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
                            profilePhoto.setImageBitmap(bmp)
                            freelanceEditModel.profilePhoto = byteArray
                            updateProfilePhoto = byteArray
                            Log.i("image uri :","$byteArray, $uri")

                        }catch (e : OutOfMemoryError){
                            Log.i("out of memory","$e")
                        }
                        Log.i("get media data","${result.data},******** ${result.data?.clipData?.getItemAt(0)?.uri}")
                    }else{
                        Toast.makeText(requireContext(),"error try again",Toast.LENGTH_LONG).show()
                    }

                }

            })


        //implementing all buttons
        locationButton.setOnClickListener {
            navController.navigate(freelancerEditDirections.actionFreelancerEditToLocationSelectUi())
        }
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
            freelanceEditModel.profilePhoto = null
            freelanceEditModel.desPhotosList = arrayListOf()
        }
        //        handling android back button
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().onBackPressed()
                freelanceEditModel.profilePhoto = null
                freelanceEditModel.desPhotosList = arrayListOf()
                true
            } else false
        }

        uploadPhotos.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            Log.i("","in select images listener")
            Toast.makeText(requireContext(),"choose from gallery",Toast.LENGTH_LONG)
            //passing the intent for launch
            getDescritionImages.launch(Intent.createChooser(intent,"select picture"))
        }

        profilePhotoButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//            Log.i("","in select images listener")
            Toast.makeText(requireContext(),"choose from gallery",Toast.LENGTH_LONG)
            //passing the intent for launch
            getProfilePhoto.launch(Intent.createChooser(intent,"picture"))
        }

        save.setOnClickListener {
            val data = hashMapOf<String,Any>(
                "name" to name.text.toString(),
                "job_titles" to hashMapOf<String, String>(
                    "0" to jobTitle1.text.toString(),
                    "1" to jobTitle2.text.toString(),
                    "2" to jobTitle3.text.toString(),
                ),
                "location" to locationViewModel.location,
                "description" to description.text.toString(),
                "services" to hashMapOf<String, Any>(
                    "0" to hashMapOf<String, Any>(
                        "name" to service1.text.toString(),
                        "price" to price1.text.toString()
                    ),
                    "1" to hashMapOf<String, Any>(
                        "name" to service2.text.toString(),
                        "price" to price2.text.toString()
                    ),
                    "2" to hashMapOf<String, Any>(
                        "name" to service3.text.toString(),
                        "price" to price3.text.toString()
                    ),
                    "3" to hashMapOf<String, Any>(
                        "name" to service4.text.toString(),
                        "price" to price4.text.toString()
                    ),
                    "4" to hashMapOf<String, Any>(
                        "name" to service5.text.toString(),
                        "price" to price5.text.toString()
                    ),
                    "5" to hashMapOf<String, Any>(
                        "name" to service6.text.toString(),
                        "price" to price6.text.toString()
                    ),
                    "6" to hashMapOf<String, Any>(
                        "name" to service7.text.toString(),
                        "price" to price7.text.toString()
                    ),
                    "7" to hashMapOf<String, Any>(
                        "name" to service8.text.toString(),
                        "price" to price8.text.toString()
                    ),
                    "8" to hashMapOf<String, Any>(
                        "name" to service9.text.toString(),
                        "price" to price9.text.toString()
                    ),
                    "9" to hashMapOf<String, Any>(
                        "name" to service10.text.toString(),
                        "price" to price10.text.toString()
                    ),
                    "10" to hashMapOf<String, Any>(
                        "name" to service11.text.toString(),
                        "price" to price11.text.toString()
                    ),
                    "11" to hashMapOf<String, Any>(
                        "name" to service12.text.toString(),
                        "price" to price12.text.toString()
                    ),
                    "12" to hashMapOf<String, Any>(
                        "name" to service13.text.toString(),
                        "price" to price13.text.toString()
                    ),
                    "13" to hashMapOf<String, Any>(
                        "name" to service14.text.toString(),
                        "price" to price14.text.toString()
                    ),
                    "14" to hashMapOf<String, Any>(
                        "name" to service15.text.toString(),
                        "price" to price15.text.toString()
                    )

                ),

                )

            Log.i("booleans","$dbexists , $desPhotosChanged, $profilePhotoChanged")

            //checnking all the if statements for save button
            if (!dbexists && (!desPhotosChanged || !profilePhotoChanged)){
                Toast.makeText(context,"photos cannot be empty",Toast.LENGTH_LONG).show()
            }else if( (jobTitle1.text.isEmpty() || jobTitle2.text.isEmpty() || jobTitle3.text.isEmpty())){
                Toast.makeText(context,"Job titles cannot be empty",Toast.LENGTH_LONG).show()
            }else if ( description.text.isEmpty()){
                Toast.makeText(context,"description cannot be empty",Toast.LENGTH_LONG).show()
            }else if((service1.text.isEmpty() || service2.text.isEmpty() || service3.text.isEmpty() || service4.text.isEmpty())){
                Toast.makeText(context,"service 1-4 cannot be empty",Toast.LENGTH_LONG).show()
            }else if((price1.text.isEmpty() || price2.text.isEmpty() || price3.text.isEmpty() || price4.text.isEmpty())){
                Toast.makeText(context,"prices 1-4 cannot be empty",Toast.LENGTH_LONG).show()
            }else if(name.text.isEmpty()) {
                Toast.makeText(context, "name cannot be empty", Toast.LENGTH_LONG).show()
            }else if(!dbexists && locationViewModel.location.value == null){
                Toast.makeText(context, "location cannot be empty", Toast.LENGTH_LONG).show()
            }else if (!dbexists){

                // calling viewmodel
                freelanceEditModel.firstTime(desPhotosList,updateProfilePhoto,data,locationViewModel.location.value,requireContext())
            }else if(dbexists && (desPhotosChanged || profilePhotoChanged) ){
                //delete previous des photos and update new
                if(locationViewModel.location.value == null){
                    Toast.makeText(context, "location cannot be empty", Toast.LENGTH_LONG).show()
                }else{
                    loadingDailog.showDialog()
                    if (profilePhotoChanged){
                        Log.i("profilephoto","changed")
                        freelanceEditModel.profilPhotoChanged(updateProfilePhoto,data, docId,locationViewModel.location.value,desPhotosChanged,requireContext())
                    }
                    if (desPhotosChanged){
                        Log.i("desphoto","changed")
                        freelanceEditModel.desPhotosChanged(desPhotosList,data,requireContext(),locationViewModel.location.value,docId)
                    }
                }

            }else if(dbexists){
                //no photos changes but the content might be changed
                if(locationViewModel.location.value == null){
                    Toast.makeText(context, "location cannot be empty", Toast.LENGTH_LONG).show()
                }else{
                    freelanceEditModel.noPhotosChanged(data,docId,locationViewModel.location.value,requireContext())
                }
            }

        }
    }
}
package fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.compose.animation.core.snap
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import viewmodels.freelancerPageViewModel
import java.util.*


class freelancerPage : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freelancer_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val chatButton = view.findViewById<Button>(R.id.freelancerPageChatButton)
        val docId = arguments?.get("docId")
        val loadingDialog : loadingDailog = loadingDailog(requireContext())
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val viewPager = view.findViewById<ViewPager2>(R.id.freelancerPageImageSlider)
        val freelancerPageViewModel : freelancerPageViewModel by activityViewModels()
        var backButton = view.findViewById<ImageView>(R.id.freelancerPageBackButton)
        var data : DocumentSnapshot
        loadingDialog.showDialog()

        var freelancerUid : String = ""

        Log.i("doc id in argument" ,"$docId")

        //display values
        val name = view.findViewById<TextView>(R.id.freelancerPageName)
        val jobTitles = view.findViewById<TextView>(R.id.freelancerPageJobTitles)
        val profilePhoto = view.findViewById<ImageView>(R.id.freelancerPageProfilePhoto)
        val description = view.findViewById<TextView>(R.id.freelancerPageDescription)
        val servicesTab = view.findViewById<CardView>(R.id.freelancerPageServicesTab)
        var servicesName: MutableList<String> = arrayListOf()
        var servicesPrice : MutableList<String> = arrayListOf()


        //adding ads to then adview banner
        val mAdView = view.findViewById<AdView>(R.id.adViewFreelancerPage)
        val adRequest = AdRequest.Builder()
            .build()

        mAdView.loadAd(adRequest)


        //freelancer data
        var freelancerName = ""
        var freelancerProfilePhoto = ""

        //back button implementation
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }


        //getting freelancer document from firestore
        var descriptionImages : MutableList<String> = arrayListOf()
        db.collection("freelancers").document(docId.toString()).get()
            .addOnSuccessListener { snapshot->
                loadingDialog.dismissDialog()
                data = snapshot
                Log.i("doc snapshot" ,"${snapshot.data}")

                freelancerUid = data?.get("uid").toString()
                if(data != null) {
                    freelancerUid = data?.get("uid").toString()
                    //setting description phots
                    descriptionImages.add(data?.get("description_photos.0.url").toString())
                    descriptionImages.add(data?.get("description_photos.1.url").toString())
                    descriptionImages.add(data?.get("description_photos.2.url").toString())
                    descriptionImages.add(data?.get("description_photos.3.url").toString())
                    Log.i("snapsot","$snapshot")
                    Log.i("snapsot data","${snapshot.data}")
                    Log.i("data job titles","${snapshot["job_titles.0"]} ${snapshot.data?.get("job_titles.0")}")
                    viewPager.adapter = freelancePageImageSlider(descriptionImages, requireContext())

                    freelancerName =data?.get("name").toString()
                    freelancerProfilePhoto = data?.get("profile_photo.url").toString()

                    name.text = data?.get("name").toString()
                    jobTitles.text = data?.get("job_titles.0").toString() + ", " + data?.get("job_titles.1").toString() + ", " +
                            data?.get("job_titles.2").toString()
                    Glide.with(requireContext()).load(data?.get("profile_photo.url").toString())
                        .placeholder(R.drawable.user)
                        .into(profilePhoto)
                    description.text = data?.get("description").toString()
                    for (i in 0..14){
                        if (data?.get("services.$i.name") != null && data?.get("services.$i.name") != ""){
                            servicesName.add(data?.get("services.$i.name").toString())
                            servicesPrice.add(data?.get("services.$i.price").toString())
                        }
                        if (i==14){
                            freelancerPageViewModel.servicesName = servicesName
                            freelancerPageViewModel.servicesPrice = servicesPrice
                            Log.i("freelancer page services","${freelancerPageViewModel.servicesName}")
//                            freelancerPageViewModel.setNameAndPrice(servicesPrice,servicesName)


                        }
                    }
                }
            }

        //services tab
        servicesTab.setOnClickListener {
            navController.navigate(freelancerPageDirections.actionFreelancerPageToFreelancerServicesList(
                docId.toString()
            ))
        }

        chatButton.setOnClickListener {
            loadingDialog.showDialog()
            if (userId == null){
                Toast.makeText(context,"Login to chat",Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }else if(userId == freelancerUid && userId != null){
                Toast.makeText(context,"Not possible",Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }else{
                db.collection("chats").whereEqualTo("users_map.$userId",true)
                    .whereEqualTo("users_map.$freelancerUid",true).get().addOnSuccessListener {
                        snapshot ->
                        Log.i("in chat query","snapshot ${snapshot.documents.size}")
                        if (snapshot.documents.size == 0){
                            //create new chat
                            db.collection("users").whereEqualTo("userId",userId).get()
                                .addOnSuccessListener {
                                    snapshot ->
                                    if (snapshot.documents[0].data != null){

                                        val userName = snapshot.documents[0].get("first_name").toString() + " " +
                                                snapshot.documents[0].get("last_name").toString()

                                        var newChatData = hashMapOf<String,Any>(
                                            "time_start" to Timestamp(Date()),
                                            "time_last" to Timestamp(Date()),
                                            "users_map" to hashMapOf<String,Boolean>(
                                                "$userId" to true,
                                                "$freelancerUid" to true
                                            ),
                                            "users" to listOf<String>(userId,freelancerUid),
                                            "user_1" to hashMapOf<String,Any>(
                                                "name" to freelancerName,
                                                "profile_photo" to freelancerProfilePhoto,
                                                "uid" to freelancerUid
                                            ),
                                            "user_2" to hashMapOf<String,Any>(
                                                "name" to userName,
                                                "profile_photo" to snapshot.documents[0].get("profile_photo.url").toString(),
                                                "uid" to userId
                                            ),

                                        )
                                        db.collection("chats").add(newChatData).addOnSuccessListener { newSnap->
                                            val docId = newSnap.id
                                            navController.navigate(freelancerPageDirections.actionFreelancerPageToChatUi(
                                                freelancerName,
                                                freelancerProfilePhoto,
                                                docId,
                                                freelancerUid
                                            ))
                                            loadingDialog.dismissDialog()
                                        }
                                    }
                                }
                        }else{
                            val name = freelancerName
                            loadingDialog.dismissDialog()
                            navController.navigate(freelancerPageDirections.actionFreelancerPageToChatUi(
                                name,
                                freelancerProfilePhoto,
                                snapshot.documents[0].id,
                                freelancerUid
                            ))
                        }
                    }
            }


        }
    }



}

class freelancePageImageSlider(val images: List<String>, val context: Context) : RecyclerView.Adapter<freelancePageImageSlider.ViewHolder> (){
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var sliderImageView : ImageView = itemView.findViewById(R.id.imageSlider)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): freelancePageImageSlider.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_slider,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curImage = images[position]
        Glide.with(context).load(curImage)
            .placeholder(R.drawable.loading)
            .into(holder.sliderImageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

}
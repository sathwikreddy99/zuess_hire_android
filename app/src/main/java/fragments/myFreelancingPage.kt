package fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.navigation.compose.navArgument
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.zuess.zuess_android.R
import viewmodels.freelancerPageViewModel


class myFreelancingPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_freelancing_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val chatButton = view.findViewById<Button>(R.id.freelancerPageChatButton)
        val docId = arguments?.get("docId")
        val loadingDialog : loadingDailog = loadingDailog(requireContext())
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val freelancerPageViewModel : freelancerPageViewModel by activityViewModels()

        //variables
        var servicesName: MutableList<String> = arrayListOf()
        var servicesPrice : MutableList<String> = arrayListOf()


        //components of my freelancing page
        val viewPager = view.findViewById<ViewPager2>(R.id.myFreelancerPageImageSlider)
        val editButton = view.findViewById<TextView>(R.id.myFreelancingPageEditButton)
        val backButton = view.findViewById<ImageView>(R.id.myfreelancerPageBackButton)
        val profilePhoto = view.findViewById<ImageView>(R.id.myFreelancerPageProfilePhoto)
        val name = view.findViewById<TextView>(R.id.myFreelancerPageName)
        val jobTitles = view.findViewById<TextView>(R.id.myFreelancerPageJobTitles)
        val description = view.findViewById<TextView>(R.id.myFreelancerPageDescription)
        val servicesTab = view.findViewById<CardView>(R.id.myFreelancerPageServicesTab)


        loadingDialog.showDialog()

        var freelancerUid : String = ""

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        editButton.setOnClickListener {
            loadingDialog.showDialog()
            navController.navigate(myFreelancingPageDirections.actionMyFreelancingPage2ToFreelancerEdit())
        }

        var descriptionImages : MutableList<String> = arrayListOf()
        db.collection("freelancers").document(docId.toString()).get()
            .addOnSuccessListener { data->
                loadingDialog.dismissDialog()

                freelancerUid = data?.get("uid").toString()
                if(data != null){
                    Log.i("my data",", ${data?.get("description_photos").toString()},${data["uid"]}")
                    freelancerUid = data?.get("uid").toString()
                    name.text = data["name"].toString()
                    jobTitles.text = data["job_titles.0"].toString() + ", " +
                            data["job_titles.1"].toString() + ", " +
                            data["job_titles.2"].toString()
                    Glide.with(requireContext()).load(data["profile_photo.url"].toString()).into(profilePhoto)
                    description.text = data["description"].toString()
                    descriptionImages.add(data?.get("description_photos.0.url").toString())
                    descriptionImages.add(data?.get("description_photos.1.url").toString())
                    descriptionImages.add(data?.get("description_photos.2.url").toString())
                    descriptionImages.add(data?.get("description_photos.3.url").toString())
                    Log.i("des images","$descriptionImages")
                    viewPager.adapter = myFreelancingPageImageSlider(descriptionImages, requireContext())
                    for (i in 0..14){
                        if (data?.get("services.$i.name") != ""){
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

        servicesTab.setOnClickListener {
            navController.navigate(myFreelancingPageDirections.actionMyFreelancingPage2ToFreelancerServicesList(docId.toString()))
        }

    }


}

class myFreelancingPageImageSlider(val images: List<String>, val context: Context) : RecyclerView.Adapter<myFreelancingPageImageSlider.ViewHolder> (){
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var sliderImageView : ImageView = itemView.findViewById(R.id.imageSlider)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myFreelancingPageImageSlider.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_slider,parent,false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curImage = images[position]
        Glide.with(context).load(curImage)
            .placeholder(R.drawable.loading)
            .into(holder.sliderImageView)

//        Picasso.get().load(curImage)
//            .placeholder(R.drawable.loading)
//            .into(holder.sliderImageView)
    }

}
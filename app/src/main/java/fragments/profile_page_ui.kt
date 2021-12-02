package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.compose.navArgument
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import org.w3c.dom.Text
import viewmodels.profilePageRecyclerAdapter
import viewmodels.profileViewModel


class profile_page_ui : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        var image = container?.findViewById<ImageView>(R.id.profile_page_profile_photo)
        return inflater.inflate(R.layout.fragment_profile_page_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingDailog = loadingDailog(requireContext())
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val navController = findNavController()

        loadingDailog.showDialog()
        //components
        var name = view.findViewById<TextView>(R.id.userProfilePageName)
        var profilePhoto = view.findViewById<ImageView>(R.id.profilePageProfilePhoto)
        var email = view.findViewById<TextView>(R.id.userProfilePageEmail)
        val backButton = view.findViewById<ImageView>(R.id.profilePageBackButton)
        val editButton = view.findViewById<TextView>(R.id.profilePageEditButton)
        val resetPassword = view.findViewById<Button>(R.id.profilePageResetPasswordButton)

        //profile model
        db.collection("users").whereEqualTo("userId",userId.toString()).get()
            .addOnSuccessListener {
                snapshot ->
                if(snapshot.documents.size != 0){
                    val data = snapshot.documents[0]
                    name.text = data["first_name"].toString() + " " + data["last_name"].toString()
                    email.text = data["email_id"].toString()
                    Glide.with(requireContext()).load(data["profile_photo.url"].toString())
                        .placeholder(R.drawable.user)
                        .into(profilePhoto)
                }
                loadingDailog.dismissDialog()
            }

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        editButton.setOnClickListener {
            navController.navigate(profile_page_uiDirections.actionProfilePageUi2ToProfilePageEdit())
        }

        resetPassword.setOnClickListener {
            navController.navigate(profile_page_uiDirections.actionProfilePageUi2ToPasswordReset())
        }
    }

}



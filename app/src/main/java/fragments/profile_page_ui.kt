package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zuess.zuess_android.R
import viewmodels.profilePageRecyclerAdapter
import viewmodels.profileViewModel
import viewmodels.userViewModel


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
        var recyclerView = view.findViewById<RecyclerView>(R.id.profile_page_recycler_view)

        var name = view.findViewById<TextView>(R.id.profile_page_name)
        var jobTitles = view.findViewById<TextView>(R.id.profile_page_job_titles)
        var profilePhoto = view.findViewById<ImageView>(R.id.profile_page_profile_photo)

        //profile model
        val profile : profileViewModel by viewModels()
        Log.d("", "onViewCreated: in profilepage ui")
        profile.validateUser()
        profile.print()
        profile.user.observe(viewLifecycleOwner, Observer { value ->
            val adapter1 : profilePageRecyclerAdapter = profilePageRecyclerAdapter(value.servicesOffered)
            recyclerView.adapter = adapter1
            name.text = value.firstName + " " + value.lastName
            Glide.with(this).load(value.profilePhoto).into(profilePhoto)
            Log.i("user info","${value.servicesOffered[0]["type"]}")
        })
    }

}



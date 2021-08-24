package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.zuess.zuess_android.*
import viewmodels.userViewModel



private var imageNo ="0"

class homeUi : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //initializing model, nav controller, location id
        val model: userViewModel by viewModels()
        val navController = findNavController()
        var locationId = view.findViewById<TextView>(R.id.home_page_location_id)

        super.onViewCreated(view, savedInstanceState)


        //location id on click listener
        locationId.setOnClickListener {
            navController.navigate(homeUiDirections.actionHomeUi2ToLocationSelectUi())
        }



        //sign out implementation
        val signOutButton = view.findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener{
            model.signOutUser()
        }

        //userViewmodel observer to logout
        model.userLiveData.observe(viewLifecycleOwner, Observer { user->

            Log.i("","in observer")
            if(user==null){
                var i = Intent(activity, MainApplication:: class.java)
                startActivity(i)
//                navController.navigate(homeUiDirections.actionHomeUiToLoginui())
            }else{
                Log.i("","error")
            }

        })

        //implementing viewpager(for image slider)
        var list = arrayListOf<Int>(R.drawable.architect,R.drawable.carpenter,
            R.drawable.teacher,R.drawable.trainer,R.drawable.driver)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerHome)
        viewPager.adapter = ImageSliderAdapter(list)


//bottom navbar implementation
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbar)
        bottomNavbar.selectedItemId = R.id.homeIcon
        bottomNavbar.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.homeIcon->{
                    startActivity(Intent(activity, HomeActivity::class.java))
                    true
                }
                R.id.searchIcon->{
                    startActivity(Intent(activity, SearchActivity::class.java))
                    true
                }
                R.id.settingsIcon->{
                    startActivity(Intent(activity, settingsActivity::class.java))
                    true
                }
                else -> false
            }
        }


    }

}

//image slider adapter
class ImageSliderAdapter(val images: List<Int>) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> (){
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
         var sliderImageView : ImageView = itemView.findViewById(R.id.imageSlider)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSliderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_slider,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageSliderAdapter.ViewHolder, position: Int) {
        val curImage = images[position]
        holder.sliderImageView.setImageResource(curImage)
    }

    override fun getItemCount(): Int {
        return images.size
    }

}
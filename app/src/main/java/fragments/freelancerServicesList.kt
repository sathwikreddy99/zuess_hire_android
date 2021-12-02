package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zuess.zuess_android.R
import viewmodels.freelancerPageViewModel
import viewmodels.searchListAdapter
import java.io.Serializable


class freelancerServicesList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freelancer_services_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val freelancerPageViewModel : freelancerPageViewModel by activityViewModels()
        val recyclerView = view.findViewById<RecyclerView>(R.id.freelancerPageServicesListRecyclerView)
        val servicesName = arguments?.get("servicesName")
        val backButton = view.findViewById<ImageView>(R.id.freelancerServicesListPageBackButton)

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }


        //setting layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

//        var servicesName : List<String>
        val bundle : Bundle = Bundle()
        servicesName.toString().toList()

        Log.i("services list","${freelancerPageViewModel.servicesName}")

        var adapter = servicesListAdapter(freelancerPageViewModel.servicesName,freelancerPageViewModel.servicesPrice)
        recyclerView.adapter = adapter

    }

}

class servicesListAdapter(
    val servicesName : List<String>,
    val servicesPrice : List<String>,
) : RecyclerView.Adapter<servicesListAdapter.viewHolder>() {
    class viewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        var name = itemView.findViewById<TextView>(R.id.profile_page_service_title)
        var price = itemView.findViewById<TextView>(R.id.profile_page_service_price)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): servicesListAdapter.viewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.profile_page_list_view_item,parent,false)
        return servicesListAdapter.viewHolder(view)
    }

    override fun onBindViewHolder(holder: servicesListAdapter.viewHolder, position: Int) {
        if (servicesName[position] != "" && !servicesName[position].isEmpty()){
            holder.name.text = servicesName[position]
            holder.price.text = servicesPrice[position]
        }
    }

    override fun getItemCount(): Int {
        return servicesName.size
    }

}
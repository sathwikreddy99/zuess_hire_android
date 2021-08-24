package viewmodels

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.zuess.zuess_android.R
import data_models.userData
import services.getUserProfile



class profileViewModel : ViewModel() {
    val userProfile : getUserProfile = getUserProfile()
    val user: MutableLiveData<userData> = userProfile.userLiveData()

    fun validateUser(){
        userProfile.getCurrentUser()
    }
    fun print(){
        userProfile.getUserProfile()
//        userProfile.addData()
        Log.i("","in profileViewModel : ${user.value}")
    }
}

class profilePageRecyclerAdapter(val servicesList : List<Map<String,Any>>) : RecyclerView.Adapter<profilePageRecyclerAdapter.viewHolder>(){
    class viewHolder(view : View): RecyclerView.ViewHolder(view) {
        var title = view.findViewById<TextView>(R.id.profile_page_service_title)
        var price = view.findViewById<TextView>(R.id.profile_page_service_price)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_page_list_view_item,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val title : String = servicesList[position]["type"].toString()
        holder.title.text = title
        holder.price.text = servicesList[position]["price"].toString()
    }

    override fun getItemCount(): Int {
        return  servicesList.size
    }


}
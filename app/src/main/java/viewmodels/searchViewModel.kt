package viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.layout.Layout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.query
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.search.AroundRadius
import com.algolia.search.model.search.Point
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import fragments.loadingDailog
import kotlinx.serialization.json.jsonObject
import org.w3c.dom.Text
//import services.appID
//import services.searchID

var appID : String = ""
var searchID :String = ""


class searchViewModel : ViewModel(){
    val searchList : MutableLiveData<List<ResponseSearch.Hit>> by lazy {
        MutableLiveData<List<ResponseSearch.Hit>>()
    }
    var searchText : String = ""

    suspend fun search(queryString: String, location : LatLng?, context: Context){
        Log.i("in searchfunc","in search func")
        if(appID.isEmpty() || searchID.isEmpty() || appID == "" || searchID == ""){
            Toast.makeText(context,"NO INTERNET or try again",Toast.LENGTH_LONG)
            Log.i("in searchif func","no network")
            getID()
            searchList.postValue(null)
        }else{
            val client = ClientSearch(ApplicationID(appID), APIKey(searchID))
            val indexName = IndexName("freelancers")
            val index = client.initIndex(indexName)
            val query = query (){
                query = queryString
                hitsPerPage = 100
                aroundLatLng = location?.latitude?.toFloat()?.let { Point(location.latitude.toFloat(), location.longitude.toFloat()) }
                aroundRadius = AroundRadius.InMeters(15000) // 15 km
            }
            val result = index.search(query)
            var t = result.hits
            searchList.postValue(result.hits)
            Log.i("search list","${searchList.value}, ${result.hits}")

            client.initIndex(indexName)
        }

    }

    fun getID(){
        val db = FirebaseFirestore.getInstance()
        Log.i("in get id","in getid")
        db.collection("algolia").document("keys").get()
            .addOnSuccessListener { result ->
                Log.i("algoliaresult","$result")
                if(result != null){
                    appID = ( result["app_id"].toString())
                    searchID = (result["search_api_key"].toString())
                }
                Log.i("algolia Ids","$appID,$searchID")
            }

    }

}

class searchListAdapter(val searchList : List<ResponseSearch.Hit>?,
                        val context : Context,
                        val onClick : searchListAdapter.searchListOnClick
) : RecyclerView.Adapter<searchListAdapter.viewHolder>(){

    interface searchListOnClick{
        fun getInfo(docId : String)
    }

    class viewHolder(itemView : View, click : searchListOnClick) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.searchItemName)
        var profilePhoto = itemView.findViewById<ImageView>(R.id.searchItemPicture)
        var avgPrice = itemView.findViewById<TextView>(R.id.searchItemAvgPrice)
        var jobTitles = itemView.findViewById<TextView>(R.id.searchItemJobTitles)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): searchListAdapter.viewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item,parent,false)
        return viewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: searchListAdapter.viewHolder, position: Int) {
        var currentItem = searchList?.get(position)
        val space = " "
        val name : String = currentItem?.get("name").toString()
        holder.name.text = removeQutotes(name)
        holder.jobTitles.text = removeQutotes(currentItem?.get("job_titles")?.jsonObject?.get("0").toString()) + ", " +
                removeQutotes(currentItem?.get("job_titles")?.jsonObject?.get("1").toString()) + ", " +
                removeQutotes(currentItem?.get("job_titles")?.jsonObject?.get("2").toString())
        Glide.with(context).load(removeQutotes(currentItem?.get("profile_photo")?.jsonObject?.get("url").toString()))
            .placeholder(R.drawable.user)
            .into(holder.profilePhoto)

        Log.i("profile photo","${currentItem?.get("profile_photo")?.jsonObject?.get("url")}" )
        var avgPrice = 0
        var divisor: Int = 0
        for (i in 0..14){
            if (currentItem?.get("services")?.jsonObject?.get("$i")?.jsonObject?.get(
                    "price"
                ) != null && removeQutotes(currentItem?.get("services")?.jsonObject?.get("$i")?.jsonObject?.get(
                    "price"
                ).toString()) != ""){
//                Log.i("objecid"," , ${currentItem?.get("services")?.jsonObject?.get("$i")?.jsonObject?.get(
//                    "price"
//                ).toString().isEmpty()}")
//                Log.i("avg price of services"," ,${currentItem?.get("services")?.jsonObject?.get("$i")?.jsonObject?.get(
//                    "price"
//                ).toString()}")
                var k = Integer.parseInt(
                    removeQutotes(currentItem?.get("services")?.jsonObject?.get("$i")?.jsonObject?.get(
                        "price"
                    ).toString())
                )
                avgPrice = (k + (avgPrice * (divisor)))/(divisor+1)
                divisor += 1
                Log.i("avgPrice","$k,$avgPrice,$divisor")
            }
        }

        holder.avgPrice.text = avgPrice.toString()

        holder.itemView.setOnClickListener {
            Log.i("","${currentItem?.get("objectID").toString()}")
            onClick.getInfo(removeQutotes(currentItem?.get("objectID").toString()))
        }
    }

    override fun getItemCount(): Int {
            if (searchList != null) {
                return searchList.size
            }else{
                return 0
            }
    }

    fun removeQutotes(string : String): String {
        var k : String = ""
        for (v in 0..(string.length-1)){
            if (v !=0 &&  v!= string.length-1){
                k += string[v]
            }
        }
        return k
    }


}
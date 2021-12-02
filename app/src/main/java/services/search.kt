//package services
//
//import android.location.Location
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import com.algolia.search.client.ClientSearch
//import com.algolia.search.dsl.attributesForFaceting
//import com.algolia.search.dsl.filters
//import com.algolia.search.dsl.query
//import com.algolia.search.dsl.settings
//import com.algolia.search.model.APIKey
//import com.algolia.search.model.ApplicationID
//import com.algolia.search.model.Attribute
//import com.algolia.search.model.IndexName
//import com.algolia.search.model.response.ResponseSearch
//import com.algolia.search.model.search.AroundRadius
//import com.algolia.search.model.search.Point
//import com.google.android.gms.common.api.Api
//import com.google.android.gms.maps.model.LatLng
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.serialization.json.*
//import org.json.JSONArray
//import org.json.JSONObject
//import viewmodels.searchViewModel
//
//
//var appID : String = ""
//var searchID : String = ""
//
//
//class Search {
//    val searchList : MutableLiveData<List<ResponseSearch.Hit>> by lazy {
//        MutableLiveData<List<ResponseSearch.Hit>>()
//    }
//
//
//    // searching algolia for the give queryString
//     suspend fun search(queryString: String, location : LatLng?){
//         val client = ClientSearch(ApplicationID(appID), APIKey(searchID))
//         val indexName = IndexName("freelancers")
//         val index = client.initIndex(indexName)
//         val query = query (){
//             query = queryString
//             hitsPerPage = 100
////             aroundLatLng = location?.latitude?.toFloat()?.let { Point(location.latitude.toFloat(), location.longitude.toFloat()) }
////             aroundRadius = AroundRadius.InMeters(15000) // 15 km
//         }
//         val result = index.search(query)
//         var t = result.hits
//         searchList.postValue(result.hits)
//        Log.i("search list","${searchList.value}, ${result.hits}")
//         val k = result.hits[0].get("services")?.jsonObject?.get("0")
//
//         client.initIndex(indexName)
//         Log.i("in search function","${result.hits}")
//         var karray: FloatArray = FloatArray(10)
//         Location.distanceBetween(10.00,0.00,0.00,0.00,karray)
//         Log.i("distance","$karray,${karray[0]},${karray[1]}")
//    }
//
//    // get the appID and search api key on app startup
//    fun getID(){
//        val db = FirebaseFirestore.getInstance()
//        db.collection("algolia").document("keys").get()
//            .addOnSuccessListener { result ->
//                if(result != null){
//                    appID = ( result["app_id"].toString())
//                    searchID = (result["search_api_key"].toString())
//                    Log.i("algolia Ids","$appID,$searchID")
//                }
//            }
//
//    }
//
//    //filtering on client side based to location to locate nearby freelancers
////    fun locationFilter(list : List<ResponseSearch.Hit>): JSONArray {
////        var result = JSONArray()
////        for(l in list){
////            var currentItem = JSONObject()
////            var location : JsonArray? = l.get("location")?.jsonArray
////            var distance: FloatArray = FloatArray(10)
////            Location.distanceBetween(location?.get(0) as Double,location?.get(1) as Double,0.00,0.00,distance)
////            if (distance.get(0)/1000 <= 10){
////                currentItem.put("distance",distance[0])
////                currentItem.put("data",l)
////                result.put(currentItem)
////            }
////        }
////        return result
////
////    }
//
//    fun returnSearchList(): MutableLiveData<List<ResponseSearch.Hit>> {
//        return searchList
//    }
//
//}
//

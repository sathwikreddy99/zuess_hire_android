package viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
var name : MutableList<String> = arrayListOf()
var price : MutableList<String> = arrayListOf()

class freelancerPageViewModel : ViewModel() {
    var servicesName: MutableList<String> = arrayListOf()
    var servicesPrice : MutableList<String> = arrayListOf()


    fun setNameAndPrice( servicesPriceList : MutableList<String>,
                         servicesnNameList : MutableList<String>){
        name = servicesnNameList
        price = servicesPriceList
    }

    fun returnName(): MutableList<String> {
        return name
    }

    fun returnPrice(): MutableList<String> {
        return price
    }
}
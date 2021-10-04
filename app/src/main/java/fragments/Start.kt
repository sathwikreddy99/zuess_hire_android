package fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.zuess.zuess_android.R
import viewmodels.userViewModel


class start : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
//        model
        val model: userViewModel by viewModels()

        val navController = view.findNavController()



        //on app start up navigation (whether to load login page or home page)
        model.userLiveData.observe(viewLifecycleOwner, Observer { user->
            Log.i("","in start observer")
            if(user==null){
                navController.navigate(startDirections.actionStart2ToLoginui())
            }else{
               navController.navigate(startDirections.actionStart2ToHomeUi())
            }

        })

    }

}
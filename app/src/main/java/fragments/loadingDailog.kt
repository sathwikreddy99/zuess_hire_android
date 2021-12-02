package fragments

import android.app.Dialog
import android.content.Context
import com.zuess.zuess_android.R

lateinit var dialog: Dialog


class loadingDailog(val context: Context) {


    fun showDialog(){
        dialog = Dialog(context)
        dialog.setContentView(R.layout.loading_dailog)
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

    fun isShowingDismiss(){
        if (dialog.isShowing){
            dialog.dismiss()
        }
    }
}
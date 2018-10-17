package friendroid.bustracking

import android.app.Service

interface ServiceStateListener {
    fun onStopService(service: Service){}
    fun onStartService(service: Service){}
}

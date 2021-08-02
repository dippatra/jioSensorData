package com.jiosensordata

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sensoraidlmodule.ISensorServer
import com.sensoraidlmodule.ISensorServerCallBack
import java.lang.ref.WeakReference
import java.util.*

class MainActivity : AppCompatActivity(),ServiceConnection {
    private lateinit var sensorServer:ISensorServer
    private lateinit var sensorIntent:Intent
    private lateinit var sensorReadingText:TextView
    private lateinit var callBackID:String
    private lateinit var handler:Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeService()
        handler= Handler()
        sensorReadingText=findViewById(R.id.sensor_reading)
        handler = InternalHandler(sensorReadingText);
    }
    private fun initializeService(){
        sensorIntent=Intent("android.intent.action.SENSOR_SERVICE")
        sensorIntent.setPackage("com.jiosensordata")
    }

    override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
        try {
            sensorServer = ISensorServer.Stub.asInterface(binder)
            callBackID = UUID.randomUUID().toString()
            sensorServer.setCallback(callback, callBackID)

        }catch (ex: Exception){
            Log.e("onServiceConnected", ex.toString())
        }

    }
    private fun removeCallBack(){
        sensorServer.removeCallback(callback, callBackID)
    }

    override fun onServiceDisconnected(componentName: ComponentName?) {
        removeCallBack()
    }

    override fun onStart() {
        super.onStart()
        bindService(sensorIntent, this, Context.BIND_AUTO_CREATE);
    }

    override fun onStop() {
        super.onStop()
        unbindService(this)

    }
    var callback: ISensorServerCallBack = object : ISensorServerCallBack.Stub() {
        @Throws(RemoteException::class)
        override fun onSensorReadingReceived(sensorReadings: FloatArray) {
            val msg = Message()
            msg.what = 1
            msg.obj = sensorReadings
            handler.sendMessage(msg)
        }
    }

    private class InternalHandler internal constructor(textView: TextView?) : Handler() {
        private val weakTextView: WeakReference<TextView> = WeakReference(textView)
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    val textView: TextView? = weakTextView.get()
                    val sensorReadings = msg.obj as FloatArray
                    if (textView != null) {
                        textView.text = String.format("x=%.3f, y=%.3f, z=%.3f",
                            sensorReadings[0], sensorReadings[1], sensorReadings[2]
                        )
                    }
                }
                else -> super.handleMessage(msg)
            }
        }

    }
}
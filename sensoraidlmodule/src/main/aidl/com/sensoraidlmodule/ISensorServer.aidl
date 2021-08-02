// ISensorServer.aidl
package com.sensoraidlmodule;

// Declare any non-default types here with import statements
import com.sensoraidlmodule.ISensorServerCallBack;
interface ISensorServer {
   void setCallback(ISensorServerCallBack callback, String uuid);
           void removeCallback(ISensorServerCallBack callback, String id);
}
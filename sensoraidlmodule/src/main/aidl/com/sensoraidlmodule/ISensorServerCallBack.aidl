// ISensorServerCallBack.aidl
package com.sensoraidlmodule;

// Declare any non-default types here with import statements

interface ISensorServerCallBack {
   void onSensorReadingReceived(in float[] sensorReadings);
}
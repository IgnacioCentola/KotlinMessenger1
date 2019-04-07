package com.nacho.kotlinmessenger.notificationServices

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        internal val TAG = "Notificaciones"
    }

    //Clase que manejara los servicios de notificaciones, además siendo éstos declarados
    //en el Manifest

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        //codigo del origen del msg recibido

        val from = remoteMessage!!.from

        Log.d(TAG, "Mensaje recibido de: " + from!!)

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Notificación: " + remoteMessage.notification!!.body!!)
        }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token
                Log.d("Token", "Token: $token")
                Toast.makeText(this@MyFirebaseMessagingService, token, Toast.LENGTH_SHORT)
            })
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)


        //        FirebaseInstanceId.getInstance().getInstanceId()
        //                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        //                    @Override
        //                    synchronized public void onComplete(@NonNull Task<InstanceIdResult> task) {
        //                        if (!task.isSuccessful()){
        //                            Log.w(TAG, "getInstanceId failed", task.getException());
        //                            return;
        //                        }
        //
        //                        // Get new Instance ID token
        //                        String token = task.getResult().getToken();
        //                        Log.d(TAG, "Token: " + token);
        //                        Toast.makeText(MyFirebaseMessagingService.this, token, Toast.LENGTH_SHORT);
        //                    }
        //                });

    }


}

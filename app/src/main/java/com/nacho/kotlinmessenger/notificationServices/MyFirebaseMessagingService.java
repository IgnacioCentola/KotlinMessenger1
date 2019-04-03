package com.nacho.kotlinmessenger.notificationServices;

import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    static final String TAG = "Notificaciones";

    //Clase que manejara los servicios de notificaciones, además siendo éstos declarados
    //en el Manifest

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //codigo del origen del msg recibido

        String from = remoteMessage.getFrom();

        Log.d(TAG , "Mensaje recibido de: " + from);

        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Notificación: " + remoteMessage.getNotification().getBody());
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                     public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()){
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("Token", "Token: " + token);
                        Toast.makeText(MyFirebaseMessagingService.this, token, Toast.LENGTH_SHORT);
                    }
                });
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

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

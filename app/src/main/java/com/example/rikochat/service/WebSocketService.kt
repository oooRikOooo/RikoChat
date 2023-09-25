//package com.example.rikochat.service
//
//import android.app.Service
//import android.content.Intent
//import android.os.IBinder
//import android.util.Log
//import androidx.lifecycle.LifecycleService
//import androidx.lifecycle.lifecycleScope
//import com.example.rikochat.data.remote.api.chatSocket.WebSocketManager
//import io.ktor.utils.io.CancellationException
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//
//class WebSocketService(private val webSocketManager: WebSocketManager) : LifecycleService() {
//
//    override fun onCreate() {
//        super.onCreate()
////        lifecycleScope.launch {
////            webSocketManager.initSession()
////        }
//        Log.d("riko", "WebSocketService: onCreate")
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
////        lifecycleScope.launch {
////            webSocketManager.closeSession()
////        }
//
//        Log.d("riko", "WebSocketService: onDestroy")
//
////        job.cancel(CancellationException("Service is destroyed"))
//    }
//
//    override fun onBind(intent: Intent): IBinder {
//        super.onBind(intent)
//
//    }
//}
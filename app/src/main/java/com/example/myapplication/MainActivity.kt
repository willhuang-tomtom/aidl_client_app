package com.example.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.tomtom.sdk.adas.adasis.ICanBusListener
import com.tomtom.sdk.adas.adasis.IAdasisV2HorizonProvider
import com.tomtom.sdk.adas.adasis.INavigation
import java.time.Instant

class MainActivity : AppCompatActivity() {
    private var aidlService: INavigation? = null
    private var horizonProvider: IAdasisV2HorizonProvider? = null

    private val canBusListener = object : ICanBusListener.Stub() {
        override fun onPacketReceived(canId: Int, data: ByteArray) {
            val timestampUs = getCurrentTimestampInMicroseconds()
            val canData = formatCanData(timestampUs, canId, data)
            Log.i(TAG, "Received CAN data: $canData")
        }

        private fun getCurrentTimestampInMicroseconds(): Long {
            val now = Instant.now()
            return now.epochSecond * 1_000_000L + now.nano / 1_000L
        }

        private fun formatCanData(timestampUs: Long, canId: Int, data: ByteArray): String {
            val timestamp = "%d.%06d".format(timestampUs / 1_000_000L, timestampUs % 1_000_000L)
            val hexCanId = Integer.toHexString(canId)
            val hexData = data.joinToString(" ") { "%02X".format(it) }

            return "$timestamp 1 $hexCanId Rx d 8 $hexData"
        }
    }

    private val aidlServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?, ) {
            try {
                aidlService = INavigation.Stub.asInterface(service)
                horizonProvider = aidlService?.start(canBusListener)

                val bindStatusTextView: TextView = findViewById(R.id.bindStatus)
                bindStatusTextView.apply {
                    text = "Connected"
                }
                // Handle service connection
            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect AIDL service", e)
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService?.stop(horizonProvider)

            val bindStatusTextView: TextView = findViewById(R.id.bindStatus)
            bindStatusTextView.apply {
                text = "Disconnected"
            }
            // Handle service disconnection
        }
    }

    private fun bindAidlService() {
        val intent = Intent("com.tomtom.sdk.adas.adasis.INavigation").apply {
            setPackage("com.tomtom.sdk.adas.adasis.demo.service.app")
        }

        try {
            val isBound = bindService(intent, aidlServiceConnection, 0)
            Log.i(TAG, "Binding to AIDL service: $isBound")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind AIDL service", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Try to bind to the AIDL service
        bindAidlService()
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(aidlServiceConnection)
    }

    private companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
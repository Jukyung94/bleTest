package com.example.bletest

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.bletest.ui.theme.BleTestTheme

class MainActivity : ComponentActivity() {
    var bluetoothManager: BluetoothManager? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    var bluetoothLeScanner: BluetoothLeScanner?  = null
    var REQUEST_ENABLE_BT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        checkIsAvailable()
        checkBTPermission()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        Log.d("start", "start")
//        registerReceiver(receiver, filter)
        Handler().postDelayed({
            scanLeDevice()
        }, 5000)


        Handler().postDelayed({
            stopScanLeDevice()
        }, 20000)
    }

//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d("callback??", "callback")
//            val action: String? = intent?.action
//            when(action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    val deviceName = device?.name
//                    val deviceHardWareAddress = device?.address
//                }
//            }
//        }
//    }

    private fun scanLeDevice() {
        Log.i("sstart", "scan")
        bluetoothLeScanner?.startScan(leScanCallback)
    }

    private fun stopScanLeDevice() {
        bluetoothLeScanner?.stopScan(leScanCallback)
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val scanRecord = result?.scanRecord
            val device = result?.device
            val iBeacon = scanRecord?.getManufacturerSpecificData(0X004c)
            if(iBeacon != null&& iBeacon.size >= 23) {
                Log.d("result", "result" + scanRecord)
                Log.d("result", "result" + device)
            }


        }
    }

    @SuppressLint("MissingPermission")
    fun checkIsAvailable() {
        if(bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        Log.d("check", "ehcck")
        bluetoothManager = applicationContext.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        Log.d("check", "ehcck" + bluetoothAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkBTPermission() {
        if(checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
          requestPermissionLauncher.launch(
              Manifest.permission.BLUETOOTH_SCAN
          )
        }
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            if(isGranted) {
                Log.i("Permission:", "Granted")
            } else {
                Log.i("Permission:", "Denied")
            }
        }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BleTestTheme {
        Greeting("Android")
    }
}
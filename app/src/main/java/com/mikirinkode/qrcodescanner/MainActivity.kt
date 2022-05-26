package com.mikirinkode.qrcodescanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.mikirinkode.qrcodescanner.databinding.ActivityMainBinding

private const val CAMERA_REQUEST_CODE = 101
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermission()
        setupCodeScanner()
    }

    private fun setupCodeScanner() {
        codeScanner = CodeScanner(this, binding.scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE // autofocus at fixed intervals
            scanMode = ScanMode.CONTINUOUS // continuously try to scan and find the barcode

            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.tvScanResult.text = it.text
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("MainActivity", "Camera initialization error: ${it.message}")
                }
            }
        }

        // start scanning on click
        // important if not using continuously scan mode
        binding.scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
         super.onPause()
    }

    private fun setupPermission(){
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission to be able to use this app!", Toast.LENGTH_SHORT).show()
                } else {
                    // successful
                }
            }
        }
    }
}
package com.example.google_wallet_flutter

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.google_wallet_flutter.viewmodel.CheckoutViewModel
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import androidx.activity.viewModels
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.pay.PayClient
import java.util.*

class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.example.google_wallet_flutter/google_wallet"
    private lateinit var channel: MethodChannel


    private val addToGoogleWalletRequestCode = 1000

    private val viewModel: CheckoutViewModel by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        CheckoutViewModel(application)
    }

    private lateinit var addToGoogleWalletButton: View

    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
       viewModel.canSavePasses.observe(this, androidx.lifecycle.Observer { canSavePasses ->
           //todo enable disable can save passes button
        })
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)

        channel.setMethodCallHandler { call, result ->
            if (call.method == "addToWallet") {
                addToWallet()
                val resultValue = if (message != null) message else "Success"
                result.success(resultValue)

            } else {
                result.notImplemented()
            }
        }
    }

    private fun addToWallet() {
        requestSavePass();
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
        this.message = message
    }

    private fun requestSavePass() {
        // todo Disables the button to prevent multiple clicks.
        viewModel.savePasses(newObjectJson, this, addToGoogleWalletRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == addToGoogleWalletRequestCode) {
            when (resultCode) {
                RESULT_OK -> Toast
                    .makeText(
                        this,
                        "Pass added to Google Pay",
                        Toast.LENGTH_LONG
                    )
                    .show()

                RESULT_CANCELED -> {
                    // Save canceled
                }

                PayClient.SavePassesResult.SAVE_ERROR -> data?.let { intentData ->
                    val apiErrorMessage =
                        intentData.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE)
                    handleError(resultCode, apiErrorMessage)
                }

                else -> handleError(
                    CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!"
                )
            }

            //todo Re-enables the Google Pay payment button.
        }
    }


    private val issuerEmail = "jishan010@gmail.com"
    private val issuerId = "3388000000022161310"
    private val passClass = "3388000000022161310.dfa2fd81-270b-4860-aec7-7fe1f8f89862"
    private val passId = UUID.randomUUID().toString()

    private val newObjectJson = """
    {
      "iss": "$issuerEmail",
      "aud": "google",
      "typ": "savetowallet",
      "iat": ${Date().time / 1000L},
      "origins": [],
      "payload": {
        "genericObjects": [
          {
            "id": "3388000000022161310.6bc0b3f4-4ae1-4fb0-b254-787dc374d0ee",
            "classId": "3388000000022161310.dfa2fd81-270b-4860-aec7-7fe1f8f89862",
            "genericType": "GENERIC_TYPE_UNSPECIFIED",
            "hexBackgroundColor": "#60f442",
            "logo": {
              "sourceUri": {
                "uri": "https://storage.googleapis.com/wallet-lab-tools-codelab-artifacts-public/pass_google_logo.jpg"
              }
            },
            "cardTitle": {
              "defaultValue": {
                "language": "en",
                "value": "Default Card Test"
              }
            },
            "subheader": {
              "defaultValue": {
                "language": "en",
                "value": "Attendee"
              }
            },
            "header": {
              "defaultValue": {
                "language": "en",
                "value": "Jishan Ansari"
              }
            },
            "barcode": {
              "type": "QR_CODE",
              "value": "6bc0b3f4-4ae1-4fb0-b254-787dc374d0ee"
            },
            "heroImage": {
              "sourceUri": {
                "uri": "https://codeskulptor-demos.commondatastorage.googleapis.com/GalaxyInvaders/back05.jpg"
              }
            },
            "textModulesData": [
              {
                "header": "POINTS",
                "body": "593",
                "id": "points"
              },
              {
                "header": "CONTACTS",
                "body": "593",
                "id": "contacts"
              }
            ]
          }
        ]
      }
    }
     """
}

package com.example.google_wallet_flutter.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.google_wallet_flutter.utils.PaymentsUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.pay.Pay
import com.google.android.gms.pay.PayApiAvailabilityStatus
import com.google.android.gms.pay.PayClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    // A client to interact with the Google Wallet API
    private val walletClient: PayClient = Pay.getClient(application)

    // LiveData with the result of whether the user can save passes to Google Wallet
    private val _canSavePasses: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            fetchCanAddPassesToGoogleWallet()
        }
    }
    val canSavePasses: LiveData<Boolean> = _canSavePasses

    /**
     * Determine whether the API to save passes to Google Pay is available on the device.
     */
    private fun fetchCanAddPassesToGoogleWallet() {
        walletClient
            .getPayApiAvailabilityStatus(PayClient.RequestType.SAVE_PASSES)
            .addOnSuccessListener { status ->
                _canSavePasses.value = status == PayApiAvailabilityStatus.AVAILABLE
                // } else {
                // We recommend to either:
                // 1) Hide the save button
                // 2) Fall back to a different Save Passes integration (e.g. JWT link)
                // Note that a user might become eligible in the future.
            }
            .addOnFailureListener {
                // Google Play Services is too old. API availability can't be verified.
                _canSavePasses.value = false
            }
    }

    /**
     * Exposes the `savePassesJwt` method in the wallet client
     */
    val savePassesJwt: (String, Activity, Int) -> Unit = walletClient::savePassesJwt

    // Test generic object used to be created against the API
    // See https://developers.google.com/wallet/tickets/boarding-passes/web#json_web_token_jwt for more details
    // val genericObjectJwt =
    //   "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJnb29nbGUiLCJwYXlsb2FkIjp7ImdlbmVyaWNPYmplY3RzIjpbeyJpZCI6IjMzODgwMDAwMDAwMjIwOTUxNzcuZjUyZDRhZjYtMjQxMS00ZDU5LWFlNDktNzg2ZDY3N2FkOTJiIn1dfSwiaXNzIjoid2FsbGV0LWxhYi10b29sc0BhcHBzcG90LmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ0eXAiOiJzYXZldG93YWxsZXQiLCJpYXQiOjE2NTA1MzI2MjN9.ZURFHaSiVe3DfgXghYKBrkPhnQy21wMR9vNp84azBSjJxENxbRBjqh3F1D9agKLOhrrflNtIicShLkH4LrFOYdnP6bvHm6IMFjqpUur0JK17ZQ3KUwQpejCgzuH4u7VJOP_LcBEnRtzZm0PyIvL3j5-eMRyRAo5Z3thGOsKjqCPotCAk4Z622XHPq5iMNVTvcQJaBVhmpmjRLGJs7qRp87sLIpYOYOkK8BD7OxLmBw9geqDJX-Y1zwxmQbzNjd9z2fuwXX66zMm7pn6GAEBmJiqollFBussu-QFEopml51_5nf4JQgSdXmlfPrVrwa6zjksctIXmJSiVpxL7awKN2w"
    val genericObjectJwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJnb29nbGUiLCJwYXlsb2FkIjp7ImdlbmVyaWNDbGFzc2VzIjpbeyJpZCI6IjMzODgwMDAwMDAwMjIxOTAxNjAuOTQ1ODdjYTAtYWQwNy00MWZhLWIyYjgtMGZiMzk3MzI5NGMzIn1dLCJnZW5lcmljT2JqZWN0cyI6W3siYmFyY29kZSI6eyJ0eXBlIjoiUVJfQ09ERSIsInZhbHVlIjoiUVIgY29kZSB2YWx1ZSJ9LCJjYXJkVGl0bGUiOnsiZGVmYXVsdFZhbHVlIjp7Imxhbmd1YWdlIjoiZW4tVVMiLCJ2YWx1ZSI6IkJyb2FkY29tIERpZ2l0YWwgSWRlbnRpdHkifX0sImNsYXNzSWQiOiIzMzg4MDAwMDAwMDIyMTkwMTYwLjk0NTg3Y2EwLWFkMDctNDFmYS1iMmI4LTBmYjM5NzMyOTRjMyIsImhlYWRlciI6eyJkZWZhdWx0VmFsdWUiOnsibGFuZ3VhZ2UiOiJlbi1VUyIsInZhbHVlIjoiU21yaXRoaSBTaXZhIn19LCJoZXJvSW1hZ2UiOnsiY29udGVudERlc2NyaXB0aW9uIjp7ImRlZmF1bHRWYWx1ZSI6eyJsYW5ndWFnZSI6ImVuLVVTIiwidmFsdWUiOiJIZXJvIGltYWdlIGRlc2NyaXB0aW9uIn19LCJzb3VyY2VVcmkiOnsidXJpIjoiaHR0cHM6Ly9mYXJtNC5zdGF0aWNmbGlja3IuY29tLzM3MjMvMTExNzcwNDExMTVfNmU2YTNiNmY0OV9vLmpwZyJ9fSwiaGV4QmFja2dyb3VuZENvbG9yIjoiIzQyODVmNCIsImlkIjoiMzM4ODAwMDAwMDAyMjE5MDE2MC43ZTQwNGE4Yi1kMGE0LTRiY2QtYmNjZS0wMTJmOWUzZjdlNTkiLCJpbWFnZU1vZHVsZXNEYXRhIjpbeyJpZCI6IklNQUdFX01PRFVMRV9JRCIsIm1haW5JbWFnZSI6eyJjb250ZW50RGVzY3JpcHRpb24iOnsiZGVmYXVsdFZhbHVlIjp7Imxhbmd1YWdlIjoiZW4tVVMiLCJ2YWx1ZSI6IkltYWdlIG1vZHVsZSBkZXNjcmlwdGlvbiJ9fSwic291cmNlVXJpIjp7InVyaSI6Imh0dHA6Ly9mYXJtNC5zdGF0aWNmbGlja3IuY29tLzM3MzgvMTI0NDA3OTk3ODNfM2RjM2MyMDYwNl9iLmpwZyJ9fX1dLCJsaW5rc01vZHVsZURhdGEiOnsidXJpcyI6W3siZGVzY3JpcHRpb24iOiJMaW5rIG1vZHVsZSBVUkkgZGVzY3JpcHRpb24iLCJpZCI6IkxJTktfTU9EVUxFX1VSSV9JRCIsInVyaSI6Imh0dHA6Ly9tYXBzLmdvb2dsZS5jb20vIn0seyJkZXNjcmlwdGlvbiI6IkxpbmsgbW9kdWxlIHRlbCBkZXNjcmlwdGlvbiIsImlkIjoiTElOS19NT0RVTEVfVEVMX0lEIiwidXJpIjoidGVsOjY1MDU1NTU1NTUifV19LCJsb2dvIjp7ImNvbnRlbnREZXNjcmlwdGlvbiI6eyJkZWZhdWx0VmFsdWUiOnsibGFuZ3VhZ2UiOiJlbi1VUyIsInZhbHVlIjoiR2VuZXJpYyBjYXJkIGxvZ28ifX0sInNvdXJjZVVyaSI6eyJ1cmkiOiJodHRwczovL3N0b3JhZ2UuZ29vZ2xlYXBpcy5jb20vd2FsbGV0LWxhYi10b29scy1jb2RlbGFiLWFydGlmYWN0cy1wdWJsaWMvcGFzc19nb29nbGVfbG9nby5qcGcifX0sInN0YXRlIjoiQUNUSVZFIiwidGV4dE1vZHVsZXNEYXRhIjpbeyJib2R5IjoiTjAzOTM4NyIsImhlYWRlciI6IkVtcGxveWVlIElEIiwiaWQiOiJFTVBfSUQifSx7ImJvZHkiOiJHVE8iLCJoZWFkZXIiOiJCdXNpbmVzcyBVbml0IiwiaWQiOiJCVVNJTkVTU19VTklUX0RBVEEifV19XX0sImlzcyI6Im15LXNlcnZpY2UtYWNjb3VudEBtYWNyby1ib25pdG8tMjU0OTA2LmlhbS5nc2VydmljZWFjY291bnQuY29tIiwib3JpZ2lucyI6WyJ3d3cuZXhhbXBsZS5jb20iXSwidHlwIjoic2F2ZXRvd2FsbGV0In0.cwyUtq25RmUgnGkQnDdmSDK8Byg4UYJ_XLurvlpQVn63vU__lphFkwbM83c2P7UPCtGog_5c3r0l2MWqsfyHN-cjw7z3GCWP_LTifKEvJcSrYYOWg3wQrG8H0KmqkoIdZMi1TAI-0K8fPFH6lAUITWGFqHsaQv22aiK3cIt_dzxIg3GFWRFaWmHcm4VgvPPfOhmF3OREU1_F_A5ncZQeglcSpJztR4X75tJmkdOSiwUrUh0m0VO8Ma4ITJd0xG-BCFqsHioidGiQpVwuH42qpZ7AOZ0b-0UYosPflDFzLr8ZWpT9xVMty36eBzPMS_IHtfwjRc6Ii7nHsnqKz49MKg";
}
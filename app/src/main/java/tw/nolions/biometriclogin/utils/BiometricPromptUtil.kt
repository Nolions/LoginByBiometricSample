package tw.nolions.biometriclogin.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import javax.crypto.Cipher

object BiometricPromptUtil {
    private const val TAG = "BiometricPromptUtil"

    /**
     * 裝置是否支援Biometric
     *
     * @param context
     * @return Boolean
     */
    fun isBiometricSupport(context: Context) =
        BiometricManager.from(context).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

    /**
     * 初始化 BiometricPrompt
     *
     * @param activity
     * @param successListener
     * @return BiometricPrompt
     */
    private fun initBiometricPrompt(
        activity: AppCompatActivity,
        successListener: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "errCode is $errorCode and errString is: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                successListener(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "User biometric rejected.")
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    /**
     * 顯示BiometricPrompt Dialog
     *
     * @param activity
     * @param successListener
     */
    fun showBiometricPrompt(
        activity: AppCompatActivity,
        successListener: (BiometricPrompt.AuthenticationResult) -> Unit,
        cryptoObject: Cipher? = null
    ) {
        val promptInfo = setBiometricPromptInfo()
        val biometricPrompt = initBiometricPrompt(activity, successListener)

        biometricPrompt.apply {
            if (cryptoObject == null) authenticate(promptInfo)
            else authenticate(promptInfo, BiometricPrompt.CryptoObject(cryptoObject))
        }
    }

    /**
     * 設定BiometricPrompt Dialog資訊
     *
     * @return BiometricPrompt.PromptInfo
     */
    private fun setBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sample App Authentication")
            .setSubtitle("Please login to get access")
            .setDescription("Sample App is using Android biometric authentication")
            .setConfirmationRequired(false)
            .setNegativeButtonText("Cancel")

        return builder.build()
    }
}

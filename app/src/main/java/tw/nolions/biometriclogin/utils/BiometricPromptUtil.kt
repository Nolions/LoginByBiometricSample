package tw.nolions.biometriclogin.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.core.content.ContextCompat

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
     * @param listener
     * @return BiometricPrompt
     */
    private fun initBiometricPrompt(
        activity: AppCompatActivity,
        listener: BiometricAuthListener
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "errCode is $errorCode and errString is: $errString")
                listener.onBiometricAuthenticationError(errorCode, errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                listener.onBiometricAuthenticationSuccess(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "User biometric rejected.")
                listener.onBiometricAuthenticationFail()
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    /**
     * 顯示BiometricPrompt Dialog
     *
     * @param activity
     * @param listener
     */
    fun showBiometricPrompt(
        activity: AppCompatActivity,
        listener: BiometricAuthListener,
        cryptoObject: CryptoObject? = null
    ) {
        val promptInfo = setBiometricPromptInfo()
        val biometricPrompt = initBiometricPrompt(activity, listener)

        biometricPrompt.apply {
            authenticate(promptInfo)
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

interface BiometricAuthListener {
    /**
     * 辨識成功
     *
     * @param result
     */
    fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult)

    /**
     * 辨識異常
     *
     * EX: 取消辨識
     *
     * @param errorCode
     * @param errorMessage
     */
    fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String)

    /**
     * 辨識失敗
     *
     */
    fun onBiometricAuthenticationFail()
}
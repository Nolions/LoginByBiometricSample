package tw.nolions.biometriclogin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import kotlinx.android.synthetic.main.activity_main.*
import tw.nolions.biometriclogin.models.Key
import tw.nolions.biometriclogin.models.User
import tw.nolions.biometriclogin.utils.BiometricPromptUtil
import tw.nolions.biometriclogin.utils.CryptographyUtil

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val fileName = "biometric_prefs"
    private val prefKey = "ciphertext_wrapper"

    private val cipherTextWrapper
        get() = CryptographyUtil.getCipherTextWrapperFromSharedPrefs(
            applicationContext,
            fileName,
            Context.MODE_PRIVATE,
            prefKey
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        username.text = null
        password.text = null
        showBiometricLoginOption()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnLogin -> onClickLogin()
            R.id.btnBiometricsLogin -> onClickBiometrics()
        }
    }

    private fun onClickLogin() {
        if (username.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            toastAlert("帳密不能為空")
            return
        }

        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val cipher = CryptographyUtil.getInitializedCipherForEncryption(Key().name)
            BiometricPromptUtil.showBiometricPrompt(this, ::encryptAndStoreServerToken, cipher)
        }
    }

    private fun onClickBiometrics() {
        cipherTextWrapper?.let {
            val cipher = CryptographyUtil.getInitializedCipherForDecryption(
                Key().name,
                it.initializationVector
            )
            BiometricPromptUtil.showBiometricPrompt(this, ::decryptServerTokenFromStorage, cipher)
        }
    }

    /**
     * 是否顯示指紋辨識按鈕
     *
     */
    private fun showBiometricLoginOption() {
        btnBiometricsLogin.visibility =
            if (BiometricPromptUtil.isBiometricSupport(this) && cipherTextWrapper != null) View.VISIBLE
            else View.GONE
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        Log.d(TAG, "decryptServerTokenFromStorage")
        authResult.cryptoObject?.cipher?.apply {
            cipherTextWrapper?.let {
                val decryptData = CryptographyUtil.decryptData(it.ciphertext, this)
                toastAlert(decryptData)
            }
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            val user = User(
                username = username.text.toString(),
                password = password.text.toString()
            )

            val encryptedServerTokenWrapper = CryptographyUtil.encryptData(user.json, this)
            CryptographyUtil.persistCiphertextWrapperToSharedPrefs(
                encryptedServerTokenWrapper,
                applicationContext,
                fileName,
                Context.MODE_PRIVATE,
                prefKey
            )
        }

        onResume()
    }

    private fun toastAlert(data: String) {
        Toast.makeText(this, "Alert: $data", Toast.LENGTH_SHORT).show()
    }
}
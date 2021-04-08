package tw.nolions.biometriclogin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import kotlinx.android.synthetic.main.activity_main.*
import tw.nolions.biometriclogin.models.User
import tw.nolions.biometriclogin.utils.BiometricAuthListener
import tw.nolions.biometriclogin.utils.BiometricPromptUtil

class MainActivity : AppCompatActivity(), BiometricAuthListener {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showBiometricLoginOption()
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {

    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {

    }

    override fun onBiometricAuthenticationFail() {
    }

    fun onClickLogin(view: View) {
        val user = User(username = username.text.toString(), password = password.text.toString())
    }

    fun onClickBiometrics(view: View) {
        BiometricPromptUtil.showBiometricPrompt(this, this)
    }

    /**
     * 是否顯示指紋辨識按鈕
     *
     */
    private fun showBiometricLoginOption() {
        btnBiometricsLogin.visibility =
            if (BiometricPromptUtil.isBiometricSupport(this)) View.VISIBLE
            else View.GONE
    }


}
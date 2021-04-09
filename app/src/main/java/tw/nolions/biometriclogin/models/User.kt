package tw.nolions.biometriclogin.models

import com.google.gson.Gson

data class User(
    val username: String,
    val password: String
) {
    val json = Gson().toJson(this)
}

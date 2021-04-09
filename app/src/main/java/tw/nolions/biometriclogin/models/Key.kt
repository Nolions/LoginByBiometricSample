package tw.nolions.biometriclogin.models

import android.security.keystore.KeyProperties

data class Key(
    val name: String = "123456",
    val size: Int = 256,
    val keystore: String = "AndroidKeyStore",
    val encryption_mode: String = KeyProperties.BLOCK_MODE_GCM,
    val encryption_padding: String = KeyProperties.ENCRYPTION_PADDING_NONE,
    val encryption_algorithm: String = KeyProperties.KEY_ALGORITHM_AES,
) {
    val transformation: String
        get() = "$encryption_algorithm/$encryption_mode/$encryption_padding"
}

package tw.nolions.biometriclogin.models

data class CiphertextWrapper(val ciphertext: ByteArray, val initializationVector: ByteArray)

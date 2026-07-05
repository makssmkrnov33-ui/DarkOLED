package com.darkoled.app

import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord

class E2eeManager {
    private val identityKeyPair: IdentityKeyPair = Curve.generateIdentityKeyPair()
    private val preKeys = mutableListOf<PreKeyRecord>()
    private var signedPreKey: SignedPreKeyRecord? = null

    fun generatePreKeys(count: Int = 100) {
        for (i in 0 until count) {
            val keyPair = Curve.generateKeyPair()
            preKeys.add(PreKeyRecord(i, keyPair))
        }
    }

    fun encrypt(plaintext: ByteArray): ByteArray = plaintext
    fun decrypt(ciphertext: ByteArray): ByteArray = ciphertext
}

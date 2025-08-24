package com.project.mukchoice.util

import com.project.mukchoice.config.CryptoConfig
import org.apache.commons.codec.binary.Hex
import org.slf4j.LoggerFactory
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoUtil {
    companion object {
        private val logger = LoggerFactory.getLogger(CryptoUtil::class.java)
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

        fun encrypt(plainText: String?): String? {
            if (plainText.isNullOrBlank()) {
                return null
            }

            return try {
                val privateKey = CryptoConfig.PRIVATE_KEY
                val secretKey = SecretKeySpec(privateKey.toByteArray(Charsets.UTF_8), ALGORITHM)
                val iv = IvParameterSpec(privateKey.substring(0, 16).toByteArray())

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

                val encryptionByte = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
                Hex.encodeHexString(encryptionByte)
            } catch (e: Exception) {
                logger.error("fail encrypt: plainText length=${plainText.length}, message=${e.message}", e)
                throw RuntimeException("암호화 처리 중 오류가 발생했습니다.", e)
            }
        }

        fun decrypt(encryptedText: String?): String? {
            if (encryptedText.isNullOrBlank()) {
                return null
            }

            return try {
                val privateKey = CryptoConfig.PRIVATE_KEY
                val secretKey = SecretKeySpec(privateKey.toByteArray(Charsets.UTF_8), ALGORITHM)
                val iv = IvParameterSpec(privateKey.substring(0, 16).toByteArray())

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)

                val decodeByte = Hex.decodeHex(encryptedText.toCharArray())
                String(cipher.doFinal(decodeByte), Charsets.UTF_8)
            } catch (e: Exception) {
                logger.error("fail decrypt: encryptedText ${encryptedText.length}, message=${e.message}", e)
                throw RuntimeException("복호화 처리 중 오류가 발생했습니다.", e)
            }
        }
    }
}

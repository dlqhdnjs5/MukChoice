package com.project.mukchoice.converter

import com.project.mukchoice.util.CryptoUtil
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class EncryptionConverter : AttributeConverter<String?, String?> {

    override fun convertToDatabaseColumn(attribute: String?): String? {
        return if (attribute.isNullOrBlank()) {
            attribute
        } else {
            CryptoUtil.encrypt(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return if (dbData.isNullOrBlank()) {
            dbData
        } else {
            CryptoUtil.decrypt(dbData)
        }
    }
}

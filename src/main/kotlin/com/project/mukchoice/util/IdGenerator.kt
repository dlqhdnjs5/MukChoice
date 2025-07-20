package com.project.mukchoice.util

import kotlin.random.Random

object IdGenerator {

    private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val DEFAULT_LENGTH = 10

    /**
     * VARCHAR(10)에 맞는 랜덤 ID 생성
     * 숫자와 대문자 영어 조합 (길이: 10자)
     */
    fun generateId(): String {
        return generateId(DEFAULT_LENGTH)
    }

    /**
     * 지정된 길이의 랜덤 ID 생성
     * @param length 생성할 ID의 길이
     */
    fun generateId(length: Int): String {
        return (1..length)
            .map { CHARACTERS.random() }
            .joinToString("")
    }

    /**
     * 숫자만으로 구성된 랜덤 ID 생성
     * @param length 생성할 ID의 길이 (기본값: 10)
     */
    fun generateNumericId(length: Int = DEFAULT_LENGTH): String {
        return (1..length)
            .map { Random.nextInt(0, 10) }
            .joinToString("")
    }

    /**
     * 영어만으로 구성된 랜덤 ID 생성
     * @param length 생성할 ID의 길이 (기본값: 10)
     */
    fun generateAlphaId(length: Int = DEFAULT_LENGTH): String {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return (1..length)
            .map { letters.random() }
            .joinToString("")
    }

    /**
     * 접두사가 있는 랜덤 ID 생성
     * @param prefix 접두사
     * @param remainingLength 접두사를 제외한 나머지 길이
     */
    fun generateIdWithPrefix(prefix: String, remainingLength: Int): String {
        val randomPart = generateId(remainingLength)
        return "$prefix$randomPart"
    }
}

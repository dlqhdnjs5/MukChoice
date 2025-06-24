package com.project.mukchoice.util

import kotlin.math.*

object GeoUtil {
    /**
     * 두 좌표(X, Y) 사이의 거리를 미터 단위로 반환 (Haversine 공식)
     * @param currentY 현재 위치 위도(Y)
     * @param currentX 현재 위치 경도(X)
     * @param targetY 목표 위치 위도(Y)
     * @param targetX 목표 위치 경도(X)
     * @return 거리(미터)
     */
    fun distanceMeter(
        currentY: Double, currentX: Double,
        targetY: Double, targetX: Double
    ): Double {
        val R = 6371000.0 // 지구 반지름 (미터)
        val dLat = Math.toRadians(targetY - currentY)
        val dLon = Math.toRadians(targetX - currentX)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(currentY)) * cos(Math.toRadians(targetY)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * 미터(m)를 킬로미터(km)로 변환
     * @param meter 변환할 미터 값
     * @return 킬로미터 값
     */
    fun meterToKm(meter: Double): Double {
        return meter / 1000.0
    }
}

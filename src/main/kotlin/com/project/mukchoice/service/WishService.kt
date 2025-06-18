package com.project.mukchoice.service

import com.project.mukchoice.model.wish.WishEntity
import com.project.mukchoice.repository.WishRepository
import org.springframework.stereotype.Service

@Service
class WishService(
    private val wishRepository: WishRepository,
) {
    fun saveWish(userNo: Int, placeId: Long) {
        val wish = WishEntity(userNo = userNo, placeId = placeId)
        wishRepository.save(wish)
    }

    fun existsWish(userNo: Int, placeId: Long): Boolean {
        return wishRepository.existsByUserNoAndPlaceId(userNo, placeId)
    }

    fun deleteWish(userNo: Int, placeId: Long) {
        wishRepository.deleteByUserNoAndPlaceId(userNo, placeId)
    }

    fun updateWish(userNo: Int, placeId: Long, isWish: Boolean) {
        val wishExists = existsWish(userNo, placeId)
        if (isWish && !wishExists) {
            saveWish(userNo, placeId)
        } else if (!isWish && wishExists) {
            deleteWish(userNo, placeId)
        }
    }
}
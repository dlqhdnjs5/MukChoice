package com.project.mukchoice.service

import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.wish.WishDongInfoDto
import com.project.mukchoice.model.wish.WishDto
import com.project.mukchoice.model.wish.WishEntity
import com.project.mukchoice.repository.WishRepository
import com.project.mukchoice.util.ContextHolder
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

    fun getWishListWithTotalCount(userNo: Int, offset: Int, limit: Int): Pair<List<WishEntity>, Long> {
        val wishEntities: List<WishEntity> = wishRepository.findByUserNoWithPagingWithPlace(userNo, offset, limit)
        val total = wishRepository.countByUserNo(userNo)

        return Pair(wishEntities, total)
    }

    fun getWishList(offset: Int, limit: Int): Pair<List<WishDto>, Long> {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val userNo = userInfo.userNo ?: throw IllegalArgumentException("User not found")

        val wishEntities: List<WishEntity> = wishRepository.findByUserNoWithPagingWithPlace(userNo, offset, limit)
        val total = wishRepository.countByUserNo(userNo)

        val dtoList = wishEntities.map {
            check(it.place != null) { "Place must not be null" }

            WishDto(
                userNo = it.userNo,
                placeId = it.placeId,
                regTime = it.regTime,
                place = PlaceDto.fromEntity(it.place!!).apply {
                    this.isWish = true
                }
            )
        }

        return Pair(dtoList, total)
    }

    /*fun getWishListWithPlace(userNo: Int, offset: Int, limit: Int): List<WishWithPlaceDto> {
        val result = wishRepository.findWishWithPlaceByUserNoWithPaging(userNo, offset, limit)
        return result.map {
            val arr = it as Array<*>
            WishWithPlaceDto(
                userNo = arr[0] as Int,
                place = arr[1] as com.project.mukchoice.model.place.PlaceEntity,
                regTime = arr[2] as java.time.LocalDateTime?
            )
        }
    }*/

    fun getWishListWithPlaceByRelation(userNo: Int, offset: Int, limit: Int): List<WishEntity> {
        return wishRepository.findByUserNoWithPagingWithPlace(userNo, offset, limit)
    }

    fun getWishDongList(userNo: Int): List<WishDongInfoDto> {
        return wishRepository.findDistinctDongsByUserNo(userNo)
    }

    fun getWishListByBcodeWithTotalCount(userNo: Int, bcode: String, offset: Int, limit: Int): Pair<List<WishEntity>, Long> {
        val wishEntities: List<WishEntity> = wishRepository.findByUserNoAndBcodeWithPaging(userNo, bcode, offset, limit)
        val total = wishRepository.countByUserNoAndBcode(userNo, bcode)

        return Pair(wishEntities, total)
    }
}
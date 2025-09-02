package com.project.mukchoice.repository

import com.project.mukchoice.model.wish.WishDongInfoDto
import com.project.mukchoice.model.wish.WishEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class WishRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun save(entity: WishEntity): WishEntity {
        entityManager.persist(entity)
        return entity
    }

    fun existsByUserNoAndPlaceId(userNo: Int, placeId: Long): Boolean {
        val query = "SELECT COUNT(w) FROM WishEntity w WHERE w.userNo = :userNo AND w.placeId = :placeId"
        val count = entityManager.createQuery(query, java.lang.Long::class.java)
            .setParameter("userNo", userNo)
            .setParameter("placeId", placeId)
            .singleResult
        return count > 0
    }

    fun deleteByUserNoAndPlaceId(userNo: Int, placeId: Long) {
        val query = "DELETE FROM WishEntity w WHERE w.userNo = :userNo AND w.placeId = :placeId"
        entityManager.createQuery(query)
            .setParameter("userNo", userNo)
            .setParameter("placeId", placeId)
            .executeUpdate()
    }

    fun findByUserNoWithPaging(userNo: Int, offset: Int, limit: Int): List<WishEntity> {
        val query = "SELECT w FROM WishEntity w WHERE w.userNo = :userNo ORDER BY w.regTime DESC"
        return entityManager.createQuery(query, WishEntity::class.java)
            .setParameter("userNo", userNo)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }

    fun findByUserNoWithPagingWithPlace(userNo: Int, offset: Int, limit: Int): List<WishEntity> {
        val query = "SELECT w FROM WishEntity w WHERE w.userNo = :userNo ORDER BY w.regTime DESC"
        return entityManager.createQuery(query, WishEntity::class.java)
            .setParameter("userNo", userNo)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }

    /**
     * 사용자의 찜 목록을 특정 법정동 코드(bcode)로 필터링하여 조회합니다.
     * @param userNo 사용자 번호
     * @param bcode 법정동 코드
     * @param offset 페이지 오프셋
     * @param limit 페이지 크기
     * @return 필터링된 찜 목록
     */
    fun findByUserNoAndBcodeWithPaging(userNo: Int, bcode: String, offset: Int, limit: Int): List<WishEntity> {
        val query = """
            SELECT w 
            FROM WishEntity w 
            INNER JOIN FETCH w.place p
            WHERE w.userNo = :userNo AND p.bcode = :bcode 
            ORDER BY w.regTime DESC
        """
        return entityManager.createQuery(query, WishEntity::class.java)
            .setParameter("userNo", userNo)
            .setParameter("bcode", bcode)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }

    /**
     * 특정 법정동 코드(bcode)에 해당하는 사용자의 찜 개수를 조회합니다.
     * @param userNo 사용자 번호
     * @param bcode 법정동 코드
     * @return 필터링된 찜 개수
     */
    fun countByUserNoAndBcode(userNo: Int, bcode: String): Long {
        val query = """
            SELECT COUNT(w) 
            FROM WishEntity w 
            INNER JOIN PlaceEntity p ON w.placeId = p.id
            WHERE w.userNo = :userNo AND p.bcode = :bcode
        """
        return entityManager.createQuery(query, java.lang.Long::class.java)
            .setParameter("userNo", userNo)
            .setParameter("bcode", bcode)
            .singleResult.toLong()
    }

    fun findDistinctDongsByUserNo(userNo: Int): List<WishDongInfoDto> {
        val query = """
            SELECT DISTINCT p.dong, p.bcode
            FROM WishEntity w 
            INNER JOIN PlaceEntity p ON w.placeId = p.id 
            WHERE w.userNo = :userNo AND p.dong IS NOT NULL
            ORDER BY p.dong
        """

        val results = entityManager.createQuery(query)
            .setParameter("userNo", userNo)
            .resultList

        return results.map { result ->
            val array = result as Array<*>
            WishDongInfoDto(
                dong = array[0] as String,
                bcode = array[1] as String
            )
        }
    }

    fun countByUserNo(userNo: Int): Long {
        val query = "SELECT COUNT(w) FROM WishEntity w WHERE w.userNo = :userNo"
        return entityManager.createQuery(query, java.lang.Long::class.java)
            .setParameter("userNo", userNo)
            .singleResult.toLong()
    }
}
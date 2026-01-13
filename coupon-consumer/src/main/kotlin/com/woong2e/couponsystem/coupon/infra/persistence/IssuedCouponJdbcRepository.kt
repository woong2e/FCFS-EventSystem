package main.kotlin.com.woong2e.couponsystem.coupon.infra.persistence

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponBatchRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.nio.ByteBuffer
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Repository
class IssuedCouponJdbcRepository(
    private val jdbcTemplate: JdbcTemplate
) : IssuedCouponBatchRepository {

    override fun batchInsert(coupons: List<IssuedCoupon>) {
        val sql = """
            INSERT INTO issued_coupons (id, coupon_id, user_id, status, issued_at)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        val now = Timestamp.valueOf(LocalDateTime.now())

        jdbcTemplate.batchUpdate(
            sql,
            coupons,
            coupons.size
        ) { ps: PreparedStatement, coupon: IssuedCoupon ->
            ps.setBytes(1, uuidToBytes(coupon.id))

            ps.setBytes(2, uuidToBytes(coupon.couponId))

            ps.setLong(3, coupon.userId)

            ps.setString(4, coupon.status)

            ps.setTimestamp(5, now)
        }
    }

    private fun uuidToBytes(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }
}
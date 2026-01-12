package main.kotlin.com.woong2e.couponsystem.coupon.infra.persistence

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponBatchRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
class IssuedCouponJdbcRepository(
    private val jdbcTemplate: JdbcTemplate
) : IssuedCouponBatchRepository {

    override fun batchInsert(coupons: List<IssuedCoupon>) {
        val sql = """
            INSERT INTO issued_coupon (coupon_id, user_id, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(
            sql,
            coupons,
            coupons.size,
            { ps: PreparedStatement, coupon: IssuedCoupon ->
                ps.setString(1, coupon.couponId.toString())
                ps.setLong(2, coupon.userId)
                ps.setString(3, "ISSUED")
                val now = Timestamp.valueOf(LocalDateTime.now())
                ps.setTimestamp(4, now)
                ps.setTimestamp(5, now)
            }
        )
    }
}
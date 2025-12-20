package com.woong2e.couponsystem.user.domin

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
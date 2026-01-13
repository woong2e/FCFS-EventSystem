package main.kotlin.com.woong2e.couponsystem.global.annotaion

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bulkhead(
    val permits: Int,
    val fair: Boolean = true,
    val waitTime: Long = 30,
    val key: String = ""
)
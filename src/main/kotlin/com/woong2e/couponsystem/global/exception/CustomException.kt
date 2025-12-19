package com.woong2e.couponsystem.global.exception

import com.woong2e.couponsystem.global.response.code.BaseErrorStatus

class CustomException(
    val errorCode: BaseErrorStatus
) : RuntimeException(errorCode.message)
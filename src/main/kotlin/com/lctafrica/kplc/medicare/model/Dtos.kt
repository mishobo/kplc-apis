package com.lctafrica.kplc.medicare.model

import java.math.BigDecimal
import java.time.LocalDate

data class ClaimDTO(
    val claimNumber: Long,
    val memberNumber: String,
    val memberName: String,
    val providerCode: String,
    val providerName: String,
    val benefitType: String,
    val claimDate: LocalDate,
    val invoiceNumber: String,
    val totalAmount: BigDecimal
)

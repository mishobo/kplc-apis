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

data class LctBeneficiaryDTO(
    val categoryId: Long,
    val name: String,
    val memberNumber: String,
    val nhifNumber: String?,
    val dob: String,
    val gender: String,
    val phoneNumber: String?,
    val email: String?,
    val beneficiaryType: String,
    val principalId: Long
)

data class BeneficiaryResponse(
    val success: Boolean,
    val msg: String,
    val data: Beneficiary

)

data class Beneficiary(
    val id: Long,
    val name: String,
    val memberNumber: String,
    val dob: String,
    val gender: String,
    val beneficiaryType: String,
)

package com.lctafrica.kplc.medicare.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate


data class ApiResponse<T>(
    @JsonProperty("data")
    val data: T?,
    @JsonProperty("msg")
    val msg: String?,
    @JsonProperty("success")
    val success: Boolean
)

data class Claims(
    val claims: List<ClaimDTO>
)

data class ClaimDTO(
    val claimNumber: Long,
    val invoiceId: Long,
    val memberNumber: String,
    val memberName: String,
    val providerCode: String,
    val providerName: String,
    val benefitType: String,
    val claimDate: LocalDate,
    val invoiceNumber: String,
    val totalAmount: BigDecimal,
    val batchId: Long?
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
    val status: String
)

data class BeneficiaryStatusDTO(
    val beneficiaryIds: List<Long>,
    val reason: String,
    val updateBy: String,
    val status: String,
    val updateType: String
)

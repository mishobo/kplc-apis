package com.lctafrica.kplc.medicare.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
    val joinDate: String,
    val gender: String,
    val phoneNumber: String?,
    val email: String?,
    @Enumerated(EnumType.STRING)
    val beneficiaryType: BeneficiaryType,
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
    val updateBy: String?,
    val status: String,
    val updateType: String
)

data class BeneficiaryUpdate(
    val name: String,
    val id: Long,
    val dob: String,
    val phoneNumber: String?,
    val email: String?,
    val reason: String,
    val canUseBiometrics: Boolean,
    val memberNumber: String,
    val user: String?
)

data class TopUp(
    val topUpReason: String,
    val topUpAmount: BigDecimal,
    val topUpBy: String,
    val id: Long
)

data class BenefitResponse(
    val success: Boolean,
    val msg: String,
    val data: List<BenefitBeneficiary>
)

data class BenefitBeneficiary (
    val benefitBeneficiaryId: Long,
    val aggregateId: String,
    val balance: BigDecimal,
    val benefitName: String,
    val catalogId: Long,
    val initialLimit: BigDecimal
)

data class TopUpResponse (
    val success: Boolean,
    val msg: String,
    val data: Boolean
)

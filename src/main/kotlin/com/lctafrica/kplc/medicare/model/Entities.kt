package com.lctafrica.kplc.medicare.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "claim")
data class Claim(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val claimNumber: Long,
    @Column(unique=true)
    val invoiceId: Long,
    val memberNumber: String,
    val memberName: String,
    val providerCode: String,
    val providerName: String,
    val beneficiaryCode: String,
    val benefitType: String,
    val year: String,
    val invoiceDate: LocalDate,
    val invoiceNumber: String,
    val totalAmount: BigDecimal,
    val dateReceived: LocalDate = LocalDate.now(),
    val timeStamp: LocalDateTime = LocalDateTime.now(),
    val batchId: Long?

)

@Entity
@Table(name = "beneficiaries")
data class Beneficiaries(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val createdBy: String?,
    val createdDate: String,
    val memberNumber: String,
    @Enumerated(EnumType.STRING)
    val beneficiaryType: BeneficiaryType,
    val nationalId: String?,
    val memberName: String,
    val gender: String,
    val dob: String,
    val phoneNo: String?,
    val status: String,
    val scale: String,
    val jobCategory: Int?,
    val newEntry: Boolean = false,
    val updatedEntry: Boolean,
    val company: String,
    @Column(name = "lct_principal_id", nullable = true)
    val lctPrincipalId: Long? = 0,
    val lctCategoryId: Long?,
    @Enumerated(EnumType.STRING)
    val transmission: MemberStatus = MemberStatus.PENDING,
    val transmissionComment: String?
)
@Entity
@Table(name = "jobScale")
data class JobScale(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val scale: String,
    val description: String,
    val outPatientLimit: BigDecimal,
    val inPatientLimit: BigDecimal,
    val jobCategory: Int,
    val lctCategoryId: Int,
    val company: String
)
@Entity
@Table(name = "table_maintenance")
data class TableMaintenance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "user1", nullable = false)
    val user1: String,
    @Column(name = "date_act", nullable = false)
    val dateAct: LocalDate,
    @Column(name = "time_act", nullable = false)
    val timeAct: LocalDate,
    @Column(name = "table_code", nullable = false)
    val tableCode: String,
    @Column(name = "main_code", nullable = false)
    val mainCode: String,
    @Column(name = "secondary_code", nullable = true)
    val secondaryCode: String? = null,
    @Column(name = "description", nullable = false)
    val description: String,
    @Column(name = "other_attributes", nullable = false)
    val otherAttributes: String
)
@Entity
@Table(name = "money_additions")
data class MoneyAdditions(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "staff_num", nullable = false)
    val staffNum: String,
    @Column(name = "amount", nullable = false)
    val amount: String,
    @Column(name = "benefits", nullable = false)
    val benefits: String,
    @Column(name = "user1", nullable = false)
    val user1: String,
    @Column(name = "date_done", nullable = false)
    val dateDone: LocalDate
)

@Entity
@Table(name = "stg_kplc_frint_rmvl ")
data class FingerPrints(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "STAFF_NR", nullable = false)
    val staffNr: String,
    @Column(name = "BEN_CODE", nullable = false)
    val benCode: String,
    @Column(name = "STATUS_DESC", nullable = false)
    val statusDesc: String,
    @Column(name = "STATUS_REASON", nullable = false)
    val reason: String,
    @Column(name = "USER_ID", nullable = false)
    val userId: String,
    @Column(name = "PICKED_STATUS", nullable = false)
    val pickedStatus: String,
    @Column(name = "STATUS_DATE", nullable = false)
    val statusDate: LocalDate,
    @Column(name = "INSERT_DATE", nullable = false)
    val insertDate: LocalDate
)

@Entity
@Table(name = "MONEY_MANAGEMENT ")
data class MoneyManagement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "auto_id", nullable = false)
    val autoId: Long?,
    @Column(name = "STAFF_NR", nullable = false)
    val staffNr: String,
    @Column(name = "SMART_BENEFIT", nullable = false)
    val smartBenefit: String,
    @Column(name = "AMOUNT", nullable = false)
    val amount: BigDecimal,
    @Column(name = "STATUS", nullable = false)
    val status: Boolean = false,
    @Column(name = "user_name", nullable = false)
    val userName: String,
    @Column(name = "done_date", nullable = false)
    val doneDate: String,
    @Column(name = "smart_picked_date", nullable = false)
    val smartPickedDate: Boolean = false,
    @Column(name = "reasons", nullable = false)
    val reason: String,
    @Column(name = "done_status", nullable = false)
    val doneStatus: String,
)



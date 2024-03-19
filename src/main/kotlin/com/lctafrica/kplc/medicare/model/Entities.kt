package com.lctafrica.kplc.medicare.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
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
    val createdBy: String,
    val createdDate: String,
    val memberNumber: String,
    val beneficiaryType: String,
    val nationalId: String?,
    val memberName: String,
    val gender: String,
    val dob: String,
    val phoneNo: String?,
    val status: String,
    val scale: String,
    val jobCategory: Int?,
    val newEntry: Boolean,
    val updatedEntry: Boolean,
    val company: String,
    @Column(name = "lct_principal_id", nullable = true)
    val lctPrincipalId: Long? = 0,
    val lctCategoryId: Long?
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


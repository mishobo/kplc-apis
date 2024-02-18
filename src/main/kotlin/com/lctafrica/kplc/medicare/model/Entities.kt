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
    var dateReceived: LocalDate = LocalDate.now(),
    val timeStamp: LocalDateTime = LocalDateTime.now()
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
    val lctPrincipalId: Long? = 0
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
    val lctCategoryId: Int

)


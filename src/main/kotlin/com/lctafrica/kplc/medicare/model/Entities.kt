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
    val benefit: String,
    val payerCode: String,
    val claimDate: LocalDate,
    val invoiceNumber: String,
    val totalAmount: BigDecimal,
    var dateReceived: LocalDateTime = LocalDateTime.now(),
)

@Entity
@Table(name = "invoice")
data class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val invoiceNumber: String,
    val invoiceUrl: String?=null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    val invoiceDate: LocalDate,
    val total: BigDecimal,
    @Enumerated(EnumType.STRING)
    val status: Status,
    @JsonIgnore
    @ManyToOne
    val claim: Claim,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice")
    val items: Set<InvoiceLine> = mutableSetOf()
)

@Entity
@Table(name = "invoice_line")
data class InvoiceLine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val lineItemId: Int = 0,
    @JsonIgnore
    @ManyToOne
    val invoice: Invoice,
    val unit: BigDecimal,
    val quantity: Int,
    val amount: BigDecimal,
    val description: String,
    val itemName: String,
    val serviceGroup: String? = "General"
)

@Entity
@Table(name = "diagnosis")
data class Diagnosis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @JsonIgnore
    @ManyToOne
    val claim: Claim,
    val icd10Code: String? = null,
    val description: String? = null
)


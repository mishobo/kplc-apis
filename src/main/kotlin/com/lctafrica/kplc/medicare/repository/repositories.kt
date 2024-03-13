package com.lctafrica.kplc.medicare.repository

import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.JobScale
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ClaimRepo: JpaRepository<Claim, Int> {

    fun findByInvoiceId(invoiceId: Long): Optional<Claim>

}

interface BeneficiaryRepo: JpaRepository<Beneficiaries, Long> {

    fun findByNewEntryAndScaleIsNotNull(newEntry: Boolean): List<Beneficiaries>?

    @Modifying
    @Query(value = "update Beneficiaries b set b.newEntry = false, b.lctCategoryId = :categoryId where b.memberNumber = :memberNo")
    fun updateNewEntry(@Param("memberNo") memberNo: String, @Param("categoryId") categoryId: Long)
    @Modifying
    @Query(value = "update Beneficiaries b set b.updatedEntry = false where b.memberNumber = :memberNo")
    fun updateMemberStatus(@Param("memberNo") memberNo: String)

    fun findByUpdatedEntryAndScaleIsNotNull(newEntry: Boolean): List<Beneficiaries>?

}

interface JobScaleRepo: JpaRepository<JobScale, Long>{

    fun findByScaleAndCompany(scale: String, company: String): JobScale
}
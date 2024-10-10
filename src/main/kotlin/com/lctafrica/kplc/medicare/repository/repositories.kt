package com.lctafrica.kplc.medicare.repository

import com.lctafrica.kplc.medicare.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

interface ClaimRepo: JpaRepository<Claim, Int> {

    fun findByInvoiceId(invoiceId: Long): Optional<Claim>

    @Query(value = "select distinct invoice_number from claim where invoice_number = :invoiceNumber and provider_code = :providerCode", nativeQuery = true)
    fun findByInvoiceNumberAndProviderCode(invoiceNumber: String, providerCode: String): String?

}

@Repository
interface BeneficiaryRepo: JpaRepository<Beneficiaries, Long> {

    fun findTop20ByNewEntryAndScaleIsNotNull(newEntry: Boolean): List<Beneficiaries>?

    @Modifying
    @Query(value = "update Beneficiaries b set b.newEntry = false, b.lctCategoryId = :categoryId where b.memberNumber = :memberNo")
    fun updateNewEntry(
        @Param("memberNo") memberNo: String,
        @Param("categoryId") categoryId: Long)

    @Modifying
    @Query(value = "update Beneficiaries b set b.newEntry = :newEntry, b.updatedEntry = :updatedEntry where b.memberNumber = :memberNo")
    fun updateNewEntryAndUpdateStatus(
        @Param("newEntry") newEntry: Boolean,
        @Param("updatedEntry") updatedEntry: Boolean,
        @Param("memberNo") memberNo: String,

    )

    @Modifying
    @Query(value = "update Beneficiaries b set b.updatedEntry = false, transmission = :transmission where b.memberNumber = :memberNo")
    fun updateMemberStatus(
        @Param("transmission") transmission: MemberStatus,
        @Param("memberNo") memberNo: String)

    @Modifying
    @Query(value = "update Beneficiaries b set b.lctCategoryId = :category where b.memberNumber = :memberNo")
    fun updateMemberCategory(
        @Param("category") category: Long,
        @Param("memberNo") memberNo: String
    )

    @Modifying
    @Query(value = "update Beneficiaries b set b.newEntry = false,  b.updatedEntry = false, b.transmission = :transmission where b.memberNumber = :memberNo")
    fun updateMemberTransmissionStatus(
        @Param("transmission") transmission: MemberStatus,
        @Param("memberNo") memberNo: String
    )

    @Modifying
    @Query(value = "update Beneficiaries b set b.newEntry = false,  b.updatedEntry = false, b.transmission = :transmission, transmissionComment = :transmissionComment where b.memberNumber = :memberNo")
    fun commentsForNewBeneficiaryTransmission(
        @Param("transmission") transmission: MemberStatus,
        @Param("transmissionComment") transmissionComment: String,
        @Param("memberNo") memberNo: String
    )

    fun findTop20ByUpdatedEntryAndScaleIsNotNull(newEntry: Boolean): List<Beneficiaries>?


    @Query(value = "select b from Beneficiaries b where b.memberNumber like concat('%',:familyNo,'%') and b.memberName = :memberName and b.beneficiaryType in (:beneficiaryType , :beneficiaryType1) and b.memberNumber != :memberNumber")
    fun findDuplicateDependant(
        @Param("familyNo") familyNo: String,
        @Param("memberName") memberName: String,
        @Param("beneficiaryType") beneficiaryType: BeneficiaryType,
        @Param("beneficiaryType1") beneficiaryType1: BeneficiaryType,
        @Param("memberNumber") memberNumber: String
    ): List<Beneficiaries>

}

interface JobScaleRepo: JpaRepository<JobScale, Long>{

    fun findByScaleAndCompany(scale: String, company: String): Optional<JobScale>
}

interface MoneyManagementRepo: JpaRepository<MoneyManagement, Long>{


    fun findByDoneStatus(doneStatus: String): Optional<List<MoneyManagement>>

    @Modifying
    @Query(value = "update MoneyManagement m set m.doneStatus = :doneStatus where m.staffNr = :memberNo")
    fun updateTopUpStatus(
        @Param("doneStatus") doneStatus: String,
        @Param("memberNo") memberNo: String
    )
}

interface FingerPrintRepo: JpaRepository<FingerPrints, Long>{

    fun findByPickedStatus(pickedStatus: Int): Optional<List<FingerPrints>>

    @Modifying
    @Query(value = "update FingerPrints f set f.pickedStatus = :pickedStatus where f.staffNr = :staffNr and f.benCode = :benCode")
    fun updateFingerPrintStatus(
        @Param("pickedStatus") pickedStatus: Int,
        @Param("staffNr") staffNr: String,
        @Param("benCode") benCode: String
    )



}
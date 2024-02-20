package com.lctafrica.kplc.medicare.repository

import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.JobScale
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ClaimRepo: JpaRepository<Claim, Int> {}

interface BeneficiaryRepo: JpaRepository<Beneficiaries, Long> {

    fun findByNewEntryAndScaleIsNotNull(newEntry: Boolean): List<Beneficiaries>?

    @Modifying
    @Query(value = "update Beneficiaries b set b.newEntry = false where b.memberNumber = :memberNo")
    fun updateNewEntry(@Param("memberNo") memberNo: String)

}

interface JobScaleRepo: JpaRepository<JobScale, Long>{

    fun findByScale(scale: String): JobScale
}
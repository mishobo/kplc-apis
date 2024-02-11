package com.lctafrica.kplc.medicare.repository

import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.model.Claim
import org.springframework.data.jpa.repository.JpaRepository

interface ClaimRepo : JpaRepository<Claim, Int> {}

interface BeneficiaryRepo : JpaRepository<Beneficiaries, Long> {

    fun findByNewEntry(newEntry: Boolean): List<Beneficiaries>?

}
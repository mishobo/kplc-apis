package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.ClaimDTO
import com.lctafrica.kplc.medicare.model.JobScale
import org.springframework.http.ResponseEntity

interface IClaimService {
    fun createClaim(dto: ClaimDTO): ResponseEntity<Claim>
    fun findAllClaims(): ResponseEntity<List<Claim>>
}

interface IBeneficiary{

    fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>>
}

interface IJobScales {

    fun getAllJobScaleMappings(): ResponseEntity<List<JobScale>>
}
package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.ClaimDTO
import org.springframework.http.ResponseEntity

interface IClaimService {
    fun createClaim(dto: ClaimDTO): ResponseEntity<Claim>
    fun findAllClaims(): ResponseEntity<List<Claim>>
}

interface IBeneficiary{

    fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>>
}
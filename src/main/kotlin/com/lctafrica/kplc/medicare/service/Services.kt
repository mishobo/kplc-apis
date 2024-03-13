package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.*
import org.springframework.http.ResponseEntity

interface IClaimService {
    fun createClaim(dto: Claims): ApiResponse<List<Claim>>
    fun findAllClaims(): ResponseEntity<List<Claim>>
}

interface IBeneficiary{

    fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>>

    fun uploadNewMembersToLCT()

    fun pickUpdatedRecords()
}

interface IJobScales {

    fun getAllJobScaleMappings(): ResponseEntity<List<JobScale>>
}
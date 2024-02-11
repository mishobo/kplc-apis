package com.lctafrica.kplc.medicare.controller

import com.lctafrica.kplc.medicare.service.BeneficiaryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/medicare")
@RestController
class BeneficiaryController (
    val beneficiaries: BeneficiaryService
){

    @GetMapping(value = ["/new/beneficiaries"])
    fun findAllClaims() = beneficiaries.getNewBeneficiaries()
}
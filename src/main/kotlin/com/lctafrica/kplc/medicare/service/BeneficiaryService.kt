package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.repository.BeneficiaryRepo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BeneficiaryService(
    val beneficiaryRepo: BeneficiaryRepo
): IBeneficiary {
    override fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>> {
        val beneficiaries = beneficiaryRepo.findByNewEntry(true)
        println("beneficiaries: $beneficiaries")
        return ResponseEntity(beneficiaries, HttpStatus.OK)
    }


    @Scheduled(cron = "* * * * * ?")
    override fun uploadnewMembersToLCT(){
        val beneficiaries = beneficiaryRepo.findByNewEntry(true)
        println("beneficiaries: $beneficiaries")
        beneficiaries?.forEach {
            println(it)
        }

    }

}
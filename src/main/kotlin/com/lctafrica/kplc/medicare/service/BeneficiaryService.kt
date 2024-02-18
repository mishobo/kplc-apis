package com.lctafrica.kplc.medicare.service

import com.google.gson.Gson
import com.lctafrica.kplc.medicare.model.Beneficiaries
import com.lctafrica.kplc.medicare.model.BeneficiaryResponse
import com.lctafrica.kplc.medicare.model.LctBeneficiaryDTO
import com.lctafrica.kplc.medicare.repository.BeneficiaryRepo
import com.lctafrica.kplc.medicare.repository.JobScaleRepo
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Transactional
class BeneficiaryService(
    val beneficiaryRepo: BeneficiaryRepo,
    val jobScaleRepo: JobScaleRepo
): IBeneficiary {

    @Value("\${lct.africa.membership}")
    lateinit var memberShipUrl: String

    override fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>> {
        val beneficiaries = beneficiaryRepo.findByNewEntry(true)
        println("beneficiaries: $beneficiaries")
        return ResponseEntity(beneficiaries, HttpStatus.OK)
    }


    @Scheduled(cron = "* * * * * ?")
    override fun uploadnewMembersToLCT(){
        val beneficiaries = beneficiaryRepo.findByNewEntry(true)
        beneficiaries?.forEach {
            val jobScale = jobScaleRepo.findByScale(it.scale)
            val staff = LctBeneficiaryDTO(
                categoryId = jobScale.lctCategoryId.toLong(),
                name = it.memberName,
                memberNumber = it.memberNumber,
                dob = it.dob,
                email = null,
                nhifNumber = null,
                gender = it.gender,
                phoneNumber = it.phoneNo,
                beneficiaryType = it.beneficiaryType,
                principalId = 0
            )
            apiCallForNewStaff(staff)
        }
    }

    fun apiCallForNewStaff(dto: LctBeneficiaryDTO){
        val gson = Gson()
        var staffJson = gson.toJson(dto)
        println("staff json payload :$staffJson" )

        val kplcClient = WebClient.builder().baseUrl(memberShipUrl).build()
        val remoteResponse = kplcClient.post()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(staffJson), String::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println(remoteResponse)
        val memberShipResponse = gson.fromJson(remoteResponse.toString(), BeneficiaryResponse::class.java)

        if (memberShipResponse.success){
            println("principle id: " + memberShipResponse.data.id)
        }



    }

}
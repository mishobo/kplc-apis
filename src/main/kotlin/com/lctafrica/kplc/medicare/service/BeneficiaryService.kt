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

    @Value("\${lct.africa.membershipByCategoryId}")
    lateinit var memberByCategoryIdAndMbrNo: String

    val gson = Gson()

    override fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>> {
        val beneficiaries = beneficiaryRepo.findByNewEntryAndScaleIsNotNull(true)
        println("beneficiaries: $beneficiaries")
        return ResponseEntity(beneficiaries, HttpStatus.OK)
    }

    fun getLCTPrincipleId(memberNo: String, categoryId: Long): Long {
        val familyNumber = memberNo.split("-")
        val principalNumber = familyNumber[0] + "-00"
        println("principalNumber: $principalNumber")
        val membershipClient = WebClient.builder().baseUrl(memberByCategoryIdAndMbrNo).build()
        val membershipResponse1 = membershipClient
            .get()
            .uri { u ->
                u
                    .queryParam("categoryId", categoryId)
                    .queryParam("memberNumber", principalNumber)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println("response: $membershipResponse1")
        val response = gson.fromJson(membershipResponse1.toString(), BeneficiaryResponse::class.java)

        if (!response.success){
            return 0
        }

        return response.data.id
    }


    @Scheduled(cron = "* * * * * ?")
    override fun uploadnewMembersToLCT(){
        val beneficiaries = beneficiaryRepo.findByNewEntryAndScaleIsNotNull(true)

        beneficiaries?.forEach {
            val jobScale = jobScaleRepo.findByScale(it.scale)
            val principalId = getLCTPrincipleId(it.memberNumber, jobScale.lctCategoryId.toLong())
            println("principalId: $principalId")
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
                principalId = principalId
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
            beneficiaryRepo.updateNewEntry(dto.memberNumber)

        } else {
            val membershipClient = WebClient.builder().baseUrl(memberByCategoryIdAndMbrNo).build()
            val membershipResponse1 = membershipClient
                .get()
                .uri { u ->
                    u
                        .queryParam("categoryId", dto.categoryId)
                        .queryParam("memberNumber", dto.memberNumber)
                        .build()
                }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            val response = gson.fromJson(membershipResponse1.toString(), BeneficiaryResponse::class.java)
            if (response.success){
                beneficiaryRepo.updateNewEntry(dto.memberNumber)
            }
        }
    }

}
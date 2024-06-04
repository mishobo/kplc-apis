package com.lctafrica.kplc.medicare.service

import com.google.gson.Gson
import com.lctafrica.kplc.medicare.model.*
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

    @Value("\${lct.africa.membershipStatusUpdate}")
    lateinit var membershipStatusUpdate: String

    val gson = Gson()

    override fun getNewBeneficiaries(): ResponseEntity<List<Beneficiaries>> {
        val beneficiaries = beneficiaryRepo.findTop20ByNewEntryAndScaleIsNotNull(true)
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
    override fun uploadNewMembersToLCT(){
        val beneficiaries = beneficiaryRepo.findTop20ByNewEntryAndScaleIsNotNull(true)

        beneficiaries?.forEach {
            try {
                println("member: ${it.memberNumber}")
                val jobScale = jobScaleRepo.findByScaleAndCompany(it.scale, it.company)
                if (jobScale.isPresent){
                    val scale = jobScale.get()
                    beneficiaryRepo.updateMemberCategory(scale.lctCategoryId.toLong(), it.memberNumber)
                    val principalId = getLCTPrincipleId(it.memberNumber, scale.lctCategoryId.toLong())
                    println("principalId: $principalId")
                    val staff = LctBeneficiaryDTO(
                        categoryId = scale.lctCategoryId.toLong(),
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
                }else {
                    beneficiaryRepo.updateMemberTransmissionStatus(MemberStatus.FAILED, it.memberNumber)
                }


            }catch (ex: Exception){
                println(MemberStatus.FAILED)
                println(it.memberNumber)
                beneficiaryRepo.updateMemberTransmissionStatus(MemberStatus.FAILED, it.memberNumber)
                println(ex.printStackTrace())
            }

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
            beneficiaryRepo.updateNewEntry(dto.memberNumber, dto.categoryId)
            beneficiaryRepo.updateMemberTransmissionStatus(MemberStatus.PICKED, dto.memberNumber)

        } else {
            println("check if member exists")
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
            println(response)
            if (response.success){
                println("member exists in LCT, mark as picked and make new entry false")
                beneficiaryRepo.updateMemberTransmissionStatus(MemberStatus.PICKED, dto.memberNumber)
            }else {
                println("member doest not exists in LCT, mark as FAILED and make new entry false")
                beneficiaryRepo.updateMemberTransmissionStatus(MemberStatus.FAILED, dto.memberNumber)
            }
        }
    }

    fun apiCallForNewStaff1(dto: LctBeneficiaryDTO){
        val members = mutableListOf<LctBeneficiaryDTO>()
        members.add(dto)
        val gson = Gson()
        var staffJson = gson.toJson(members)
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
            beneficiaryRepo.updateNewEntry(dto.memberNumber, dto.categoryId)

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
                beneficiaryRepo.updateNewEntry(dto.memberNumber, dto.categoryId)
            }
        }
    }

    fun getLCTMemberDetails(memberNo: String, categoryId: Long): BeneficiaryResponse {
        val membershipClient = WebClient.builder().baseUrl(memberByCategoryIdAndMbrNo).build()
        val membershipResponse1 = membershipClient
            .get()
            .uri { u ->
                u
                    .queryParam("categoryId", categoryId)
                    .queryParam("memberNumber", memberNo)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println("membershipResponse1: $membershipResponse1")
        return gson.fromJson(membershipResponse1.toString(), BeneficiaryResponse::class.java)
    }

    fun updateStaffStatusAPICall(dto: BeneficiaryStatusDTO): Boolean{
        val gson = Gson()
        var staffJson = gson.toJson(dto)
        println("staff json payload :$staffJson" )

        val kplcClient = WebClient.builder().baseUrl(membershipStatusUpdate).build()
        val remoteResponse = kplcClient.post()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(staffJson), String::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println(remoteResponse)
        val memberShipResponse = gson.fromJson(remoteResponse.toString(), BeneficiaryResponse::class.java)

        return memberShipResponse.success
    }
//    @Scheduled(cron = "* * * * * ?")
    override fun pickUpdatedRecords() {
//        val updatedStaff = beneficiaryRepo.findByUpdatedEntryAndScaleIsNotNull(true)
//
//        updatedStaff?.forEach {
//            println("updated: $it")
//            val categoryId = jobScaleRepo.findByScaleAndCompany(it.scale, it.company)
//            val member = getLCTMemberDetails(it.memberNumber, categoryId.lctCategoryId.toLong())
//
//            if(!member.success){
//                beneficiaryRepo.updateNewEntryAndUpdateStatus(newEntry = true, updatedEntry = false, it.memberNumber)
//            } else {
//                val statusDTO = BeneficiaryStatusDTO(
//                    beneficiaryIds = arrayListOf(member.data.id),
//                    reason = if (it.status == "DEACTIVATED") "FORMER" else "ACTIVATE",
//                    updateBy = it.createdBy,
//                    status = it.status,
//                    updateType = if (it.beneficiaryType == "PRINCIPAL") "FAMILY" else "INDIVIDUAL"
//                )
//
//                if(updateStaffStatusAPICall(statusDTO)){
//                    beneficiaryRepo.updateMemberStatus(it.memberNumber)
//                }
//            }
//
//        }

    }

}
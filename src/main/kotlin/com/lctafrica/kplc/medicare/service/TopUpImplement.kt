package com.lctafrica.kplc.medicare.service

import com.google.gson.Gson
import com.lctafrica.kplc.medicare.model.*
import com.lctafrica.kplc.medicare.repository.MoneyManagementRepo
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Transactional
class TopUpImplement(
    private val moneyManagementRepo: MoneyManagementRepo
) : TopUpService {

    private val gson = Gson()

    @Scheduled(cron = "* * * * * ?")
    override fun sendToLCT() {
        println("money management job")
        val topUps = moneyManagementRepo.findByDoneStatus("0")
        if (topUps.isPresent) {
            val money = topUps.get()
            money.forEach { moneyManagement ->
                try {
                    val memberNumber = moneyManagement.staffNr.plus("-00")
                    val benefitId = getBenefitId(memberNumber)

                    if (benefitId.data.isEmpty()){
                        println("There is no benefit")
                        moneyManagementRepo.updateTopUpStatus("2", moneyManagement.staffNr)
                    } else {
                        benefitId.data.forEach { benefitBeneficiary ->
                            if (moneyManagement.smartBenefit.contentEquals("10")){
                                if (benefitBeneficiary.benefitName.contentEquals("OUTPATIENT OVERALL")){
                                    println("OUTPATIENT OVERALL ${benefitBeneficiary.benefitBeneficiaryId}")
                                    val data = TopUp(topUpReason = moneyManagement.reason, topUpAmount = moneyManagement.amount, topUpBy = moneyManagement.userName, id = benefitBeneficiary.benefitBeneficiaryId)
                                    if(apiToLCT(data)){
                                        moneyManagementRepo.updateTopUpStatus("1", moneyManagement.staffNr)
                                    } else {
                                        moneyManagementRepo.updateTopUpStatus("2", moneyManagement.staffNr)
                                    }
                                }
                            } else {
                                if (benefitBeneficiary.benefitName.contentEquals("IN PATIENT OVERALL")){
                                    println("IN PATIENT OVERALL ${benefitBeneficiary.benefitBeneficiaryId}")
                                    val data = TopUp(topUpReason = moneyManagement.reason, topUpAmount = moneyManagement.amount, topUpBy = moneyManagement.userName, id = benefitBeneficiary.benefitBeneficiaryId)
                                    if(apiToLCT(data)){
                                        moneyManagementRepo.updateTopUpStatus("1", moneyManagement.staffNr)
                                    } else {
                                        println("There is no benefit 2")
                                        moneyManagementRepo.updateTopUpStatus("2", moneyManagement.staffNr)
                                    }
                                }
                            }

                        }
                    }
                }catch (e: Exception) {
                    println("Error: ${e.message}")
                    println("There is no benefit 3")
                    moneyManagementRepo.updateTopUpStatus("2", moneyManagement.staffNr)
                }
            }
        }
    }

    private fun apiToLCT(data: TopUp): Boolean {
        println("top up payload $data")
        val dataJson = gson.toJson(data)
        println("staff json payload :$dataJson" )
//        val kplcClient = WebClient.builder().baseUrl("http://localhost:8088/api/v1/benefit/topUpBenefit").build()
        val kplcClient = WebClient.builder().baseUrl("http://api.lctafrica.net/api/v1/benefit/topUpBenefit").build()
        val remoteResponse = kplcClient.post()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(dataJson), String::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val topUpResponse = gson.fromJson(remoteResponse.toString(), TopUpResponse::class.java)
        println(topUpResponse.success)

        return topUpResponse.success
    }

    private fun getBenefitId(memberNumber: String): BenefitResponse {
        val claimsClient = WebClient.builder().baseUrl("http://localhost:8088/api/v1/payer/getBenefitBeneficiary").build()
        val benefitResponse = claimsClient
            .get()
            .uri { u ->
                u
                    .queryParam("memberNumber", memberNumber)
                    .queryParam("payerId", 11)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println("benefitResponse: $benefitResponse")
        return gson.fromJson(benefitResponse.toString(), BenefitResponse::class.java)
    }
}
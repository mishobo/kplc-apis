package com.lctafrica.kplc.medicare.service

import com.google.gson.Gson
import com.lctafrica.kplc.medicare.model.BeneficiaryResponse
import com.lctafrica.kplc.medicare.model.BenefitResponse
import com.lctafrica.kplc.medicare.model.TopUp
import com.lctafrica.kplc.medicare.repository.MoneyManagementRepo
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


class TopUpImplement(
    private val moneyManagementRepo: MoneyManagementRepo
) : TopUpService {

    private val gson = Gson()

    @Scheduled(cron = "* * * * * ?")
    override fun sendToLCT() {
        val topUps = moneyManagementRepo.findByStatus(false)
        if (topUps.isPresent) {
            val money = topUps.get()

            money.forEach { moneyManagement ->
                Thread{
                    val benefitId = getBenefitId(moneyManagement.staffNr)
                    benefitId.data.forEach { benefitBeneficiary ->
                        if (moneyManagement.smartBenefit.contentEquals("OP")){
                            if (benefitBeneficiary.benefitName.contentEquals("OUTPATIENT OVERALL")){
                                println(benefitBeneficiary.benefitBeneficiaryId)
                                val data = TopUp(topUpReason = moneyManagement.reason, topUpAmount = moneyManagement.amount, topUpBy = moneyManagement.userName, id = benefitBeneficiary.benefitBeneficiaryId)
                                apiToLCT(data)
                            }
                        } else {
                            if (benefitBeneficiary.benefitName.contentEquals("IN PATIENT OVERALL")){
                                println(benefitBeneficiary.benefitBeneficiaryId)
                                val data = TopUp(topUpReason = moneyManagement.reason, topUpAmount = moneyManagement.amount, topUpBy = moneyManagement.userName, id = benefitBeneficiary.benefitBeneficiaryId)
                                apiToLCT(data)
                            }
                        }

                    }
                }.start()
            }
        }
    }

    private fun apiToLCT(data: TopUp){
        val kplcClient = WebClient.builder().baseUrl("http://localhost:8088/api/v1/benefit/topUpBenefit").build()
        val remoteResponse = kplcClient.post()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(data), String::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
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
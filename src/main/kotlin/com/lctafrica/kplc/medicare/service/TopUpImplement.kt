package com.lctafrica.kplc.medicare.service

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

    @Scheduled(cron = "* * * * * ?")
    override fun sendToLCT() {
        val topUps = moneyManagementRepo.findByStatus(false)
        if (topUps.isPresent) {
            val money = topUps.get()
            money.forEach {
                Thread{
                    val data = TopUp(topUpReason = it.reason, topUpAmount = it.amount, topUpBy = it.userName, id = it.id)
                    apiToLCT(data)
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
}
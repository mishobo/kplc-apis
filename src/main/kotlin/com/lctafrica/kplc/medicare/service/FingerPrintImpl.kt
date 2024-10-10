package com.lctafrica.kplc.medicare.service

import com.google.gson.Gson
import com.lctafrica.kplc.medicare.model.TopUpResponse
import com.lctafrica.kplc.medicare.repository.FingerPrintRepo
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient

@Service
@Transactional
class FingerPrintServiceImpl
    (private val fingerPrintRepo: FingerPrintRepo)
    : FingerPrintService  {

    private val gson = Gson()


    @Scheduled(cron = "* * * * * ?")
    override fun detachFingerPrint() {
//        println("detachFingerPrint")
        val prints = fingerPrintRepo.findByPickedStatus(0)
        if(prints.isPresent){
            val fingerPrints = prints.get()
            fingerPrints.forEach {
                println(it.staffNr)
                println(it.benCode)
                val memberNumber = it.staffNr.plus("-${it.benCode}")
                println(memberNumber)
                if(detachPrintAPI(memberNumber)){
                    fingerPrintRepo.updateFingerPrintStatus( pickedStatus = 1, staffNr = it.staffNr, benCode =  it.benCode)
                } else {
                    fingerPrintRepo.updateFingerPrintStatus( pickedStatus = 2, staffNr = it.staffNr, benCode =  it.benCode)
                }
            }
        }
    }

    fun detachPrintAPI(memberNumber: String): Boolean{
        val fingerPrintsClient = WebClient.builder().baseUrl("http://api.lctafrica.net/api/v1/biometric/update").build()
        val fingerPrintResponse = fingerPrintsClient
            .put()
            .uri { u ->
                u
                    .queryParam("memberNumber", memberNumber)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println("benefitResponse: $fingerPrintResponse")
        val prints =  gson.fromJson(fingerPrintResponse.toString(), TopUpResponse::class.java)

        return prints.success
    }

}
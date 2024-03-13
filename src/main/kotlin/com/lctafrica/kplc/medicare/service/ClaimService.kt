package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.ApiResponse
import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.ClaimDTO
import com.lctafrica.kplc.medicare.model.Claims
import com.lctafrica.kplc.medicare.repository.ClaimRepo
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ClaimService(
    val claimRepo: ClaimRepo,
): IClaimService {

    override fun createClaim(dto: Claims): ApiResponse<List<Claim>> {
        try {
            val claims = mutableListOf<Claim>()
            dto.claims.forEach { it ->
                println("claim payload: $dto");
                val invoice = claimRepo.findByInvoiceId(it.invoiceId)
                if(invoice.isPresent){
                    return ApiResponse(success = true, data = null, msg = "${it.invoiceId} invoice already received")
                }

                val familyNumber = it.memberNumber.split("-")
                val benefitType = if(it.benefitType.contains("INPATIENT"))  "IP" else "OP"
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)

                println(currentYear)

                val claim = Claim(
                    claimNumber = it.claimNumber,
                    invoiceId = it.invoiceId,
                    memberNumber = familyNumber[0],
                    memberName = it.memberName,
                    providerCode = it.providerCode,
                    providerName = it.providerName,
                    beneficiaryCode = familyNumber[1],
                    benefitType = benefitType,
                    invoiceDate = it.claimDate,
                    invoiceNumber = it.invoiceNumber,
                    totalAmount = it.totalAmount,
                    year = currentYear.toString(),
                    batchId = it.batchId
                )
                claimRepo.save(claim)
                claims.add(claim)
            }


            return ApiResponse(success = true, data = claims, msg = "claim saved successfully")
        }catch (ex: Exception){
            return ApiResponse(success = true, data = null, msg = "failed to save claim")
        }
    }
    override fun findAllClaims(): ResponseEntity<List<Claim>> {
        val claims = claimRepo.findAll()

        if (claims.isNotEmpty()){
            return ResponseEntity(claims, HttpStatus.OK)
        } else {
            return ResponseEntity(null, HttpStatus.NOT_FOUND)
        }
    }


}
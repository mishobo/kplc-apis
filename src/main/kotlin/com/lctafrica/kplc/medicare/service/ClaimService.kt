package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.ApiResponse
import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.ClaimDTO
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

    override fun createClaim(dto: ClaimDTO): ApiResponse<Claim> {
        try {
            println("claim payload: $dto");
            val familyNumber = dto.memberNumber.split("-")
            val benefitType = if(dto.benefitType.contains("INPATIENT"))  "IP" else "OP"
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

            println(currentYear)

            var claim = Claim(
                claimNumber = dto.claimNumber,
                memberNumber = familyNumber[0],
                memberName = dto.memberName,
                providerCode = dto.providerCode,
                providerName = dto.providerName,
                beneficiaryCode = familyNumber[1],
                benefitType = benefitType,
                invoiceDate = dto.claimDate,
                invoiceNumber = dto.invoiceNumber,
                totalAmount = dto.totalAmount,
                year = currentYear.toString(),
                batchId = dto.batchId
            )

            claimRepo.save(claim)
            return ApiResponse(success = true, data = claim, msg = "claim saved successfully")
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
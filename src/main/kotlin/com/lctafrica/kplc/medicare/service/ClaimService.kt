package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.Claim
import com.lctafrica.kplc.medicare.model.ClaimDTO
import com.lctafrica.kplc.medicare.repository.ClaimRepo
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ClaimService(
    val claimRepo: ClaimRepo,
): IClaimService {

    override fun createClaim(dto: ClaimDTO): ResponseEntity<Claim> {
        println("claim payload: $dto");

        println("member no: ${dto.memberNumber}");

        val familyNumber = dto.memberNumber.split("-")

        println(familyNumber[0])
        println(familyNumber[1])

        var claim = Claim(
            claimNumber = dto.claimNumber,
            memberNumber = familyNumber[0],
            memberName = dto.memberName,
            providerCode = dto.providerCode,
            providerName = dto.providerName,
            benefit = familyNumber[1],
            payerCode = dto.payerCode,
            claimDate = dto.claimDate,
            invoiceNumber = dto.invoiceNumber,
            totalAmount = dto.totalAmount
        )

        claimRepo.save(claim)

        return ResponseEntity(claim, HttpStatus.OK)
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
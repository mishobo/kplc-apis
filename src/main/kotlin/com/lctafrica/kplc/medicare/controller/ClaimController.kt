package com.lctafrica.kplc.medicare.controller

import com.lctafrica.kplc.medicare.model.ClaimDTO
import com.lctafrica.kplc.medicare.service.IClaimService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/api/v1/medicare")
@RestController
class ClaimController(
    val claimService: IClaimService
) {

    @Operation(summary = "receive claim from lct")
    @PostMapping(value = ["/claim"])
    fun createClaim(@RequestBody dto: ClaimDTO) = claimService.createClaim(dto)

    @GetMapping(value = ["/claims"])
    fun findAllClaims() = claimService.findAllClaims()


}
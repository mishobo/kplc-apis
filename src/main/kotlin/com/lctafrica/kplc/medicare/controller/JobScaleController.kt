package com.lctafrica.kplc.medicare.controller

import com.lctafrica.kplc.medicare.service.IJobScales
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/medicare")
@RestController
class JobScaleController(
    val iJobScales: IJobScales
) {

    @GetMapping(value = ["/job-scale"], produces = ["application/json"])
    fun getAllJobScales() = iJobScales.getAllJobScaleMappings()

}
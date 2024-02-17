package com.lctafrica.kplc.medicare.service

import com.lctafrica.kplc.medicare.model.JobScale
import com.lctafrica.kplc.medicare.repository.JobScaleRepo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class JobScaleService (
    val jobScaleRepo: JobScaleRepo
): IJobScales {
    override fun getAllJobScaleMappings(): ResponseEntity<List<JobScale>> {
        val mappedJobScale = jobScaleRepo.findAll()
        return ResponseEntity(mappedJobScale, HttpStatus.OK)
    }
}
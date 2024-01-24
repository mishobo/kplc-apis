package com.lctafrica.kplc.medicare.repository

import com.lctafrica.kplc.medicare.model.Claim
import org.springframework.data.jpa.repository.JpaRepository

interface ClaimRepo : JpaRepository<Claim, Int> {}
package ru.example.demo.dto.request

import org.springframework.web.multipart.MultipartFile
import ru.example.demo.dto.model.CarrierRepresentative

data class CreateCarrierCompanyRequest(
    val name: String,
    val comment: String?,
    val contract: MultipartFile? = null,
    val application: MultipartFile? = null,
    val representatives: List<CarrierRepresentative>?,
)
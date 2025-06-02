package ru.example.demo.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.example.demo.dto.model.CarrierCompany
import ru.example.demo.dto.model.CarrierRepresentative
import ru.example.demo.dto.request.CreateCarrierCompanyRequest
import ru.example.demo.dto.request.UpdateCarrierCompanyRequest
import ru.example.demo.dto.response.SuccessResponse
import ru.example.demo.service.CarrierService
import org.springframework.http.ResponseEntity
import org.springframework.core.io.Resource

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/v1/carriers")
class CarrierController(
    val carrierService: CarrierService,
    val objectMapper: ObjectMapper
) {
    @PostMapping("/create", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createCarrierCompany(
        @RequestPart("companyName") companyName: String,
        @RequestPart("comment") comment: String?,
        @RequestPart("contacts") contacts: String,
        @RequestPart("application") application: MultipartFile?,
        @RequestPart("contract") contract: MultipartFile?,
        @RequestHeader("Authorization") token: String
    ): SuccessResponse {
        val contactsList: List<CarrierRepresentative> = objectMapper.readValue(
            contacts,
            object : TypeReference<List<CarrierRepresentative>>() {}
        )

        val request = CreateCarrierCompanyRequest(
            name = companyName,
            comment = comment,
            representatives = contactsList,
            application = application,
            contract = contract
        )

        val company = carrierService.createCarrierCompany(request, token)
        return SuccessResponse("Успешное создание компании перевозчиков с id = ${company.id}")
    }

    @GetMapping
    fun getCarriers(
        @RequestHeader("Authorization") token: String
    ): List<CarrierCompany> {
        return carrierService.getCarriers(token)
    }

    @PutMapping("/update-field")
    fun updateCarrierCompany(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: UpdateCarrierCompanyRequest
    ): SuccessResponse {
        when (request.type) {
            "company" -> {
                if (request.name == null) throw IllegalArgumentException("Поле name обязательно для carrier")
                carrierService.updateCompanyField(request.name, request.field, request.value, token)
            }
            "representative" -> {
                if (request.id == null) throw IllegalArgumentException("Поле id обязательно для representative")
                carrierService.updateRepresentativeField(request.id, request.field, request.value, token)
            }
            else -> throw IllegalArgumentException("Неизвестный тип: ${request.type}")
        }

        return SuccessResponse("Поле ${request.field} успешно обновлено")
    }
    @GetMapping("/file")
    fun downloadFile(
        @RequestParam name: String,
        @RequestParam type: String,
        @RequestParam fileName: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Resource> {
        val resource = carrierService.getCarrierFile(name, type, fileName, token)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header("Content-Disposition", "attachment; filename=\"$fileName\"")
            .body(resource)
    }

    @PostMapping("/file")
    fun uploadFile(
        @RequestParam name: String,
        @RequestParam type: String,
        @RequestPart file: MultipartFile,
        @RequestHeader("Authorization") token: String
    ): SuccessResponse {
        println("spdhvsdpsvydpsv")
        val fileName = carrierService.uploadCarrierFile(name, file, type, token)
        return SuccessResponse("Файл $fileName успешно загружен")
    }

    @DeleteMapping("/file")
    fun deleteFile(
        @RequestParam name: String,
        @RequestParam type: String,
        @RequestParam fileName: String,
        @RequestHeader("Authorization") token: String
    ): SuccessResponse {
        carrierService.deleteCarrierFile(name, type, fileName, token)
        return SuccessResponse("Файл $fileName успешно удалён")
    }

}
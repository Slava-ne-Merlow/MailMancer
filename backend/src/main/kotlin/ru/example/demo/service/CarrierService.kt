package ru.example.demo.service

import jakarta.transaction.Transactional
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.example.demo.dto.model.CarrierCompany
import ru.example.demo.dto.request.CreateCarrierCompanyRequest
import ru.example.demo.entity.CarrierCompanyEntity
import ru.example.demo.entity.CarrierRepresentativeEntity
import ru.example.demo.exception.type.ForbiddenException
import ru.example.demo.repository.CarrierCompanyRepository
import ru.example.demo.repository.CarrierRepresentativeRepository
import ru.example.demo.util.Loggable


@Service
class CarrierService(
    val carrierCompanyRepository: CarrierCompanyRepository,
    val carrierRepresentativeRepository: CarrierRepresentativeRepository,
    val tokenService: TokenService,
    val fileStorageService: FileStorageService
) : Loggable() {
    @Transactional
    fun createCarrierCompany(request: CreateCarrierCompanyRequest, token: String): CarrierCompanyEntity {
        val currentUser = tokenService.getUserFromToken(token)
        val carrierCompany = CarrierCompanyEntity(
            name = request.name,
            comment = request.comment,
            contract = request.contract?.originalFilename.toString(),
            application = request.application?.originalFilename.toString(),
            userCompany = currentUser.company
        )
        val savedCarrierCompany = carrierCompanyRepository.save(carrierCompany)

        request.contract?.let {
            fileStorageService.storeFile(request.contract, carrierCompany.id, "contract")
        }

        request.application?.let {
            fileStorageService.storeFile(request.application, carrierCompany.id, "application")
        }


        request.representatives?.map {
            carrierRepresentativeRepository.save(
                CarrierRepresentativeEntity(
                    name = it.name,
                    email = it.email,
                    phoneNumber = it.phoneNumber,
                    carrierCompany = savedCarrierCompany
                )
            )
        }

        return savedCarrierCompany
    }

    fun getCarriers(token: String): List<CarrierCompany> {
        val currentUser = tokenService.getUserFromToken(token)

        val carrierCompany = carrierCompanyRepository.findByUserCompany(currentUser.company)

        return carrierCompany.map {
            CarrierCompany(
                name = it.name,
                comment = it.comment,
                contract = it.contract,
                application =  it.application,
                representatives =  carrierRepresentativeRepository.findByCarrierCompany(it).map { contact ->
                    contact.toModel()
                }
            )
        }
    }

    @Transactional
    fun updateCompanyField(name: String, field: String, value: String, token: String) {

        tokenService.getUserFromToken(token)

        val company = carrierCompanyRepository.findByName(name)
            ?: throw IllegalArgumentException("Компания не найдена")

        when (field) {
            "comment" -> company.comment = value
            else -> throw IllegalArgumentException("Неподдерживаемое поле для carrier: $field")
        }

        carrierCompanyRepository.save(company)
    }

    @Transactional
    fun updateRepresentativeField(id: Long, field: String, value: String, token: String) {
        tokenService.getUserFromToken(token)


        val rep = carrierRepresentativeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Контакт не найден") }

        when (field) {
            "name" -> rep.name = value
            "email" -> rep.email = value
            "phoneNumber" -> rep.phoneNumber = value
            else -> throw IllegalArgumentException("Неподдерживаемое поле для representative: $field")
        }

        carrierRepresentativeRepository.save(rep)
    }

    fun uploadCarrierFile(name: String, file: MultipartFile, type: String, token: String): String {
        tokenService.getUserFromToken(token)

        val company = carrierCompanyRepository.findByName(name)
            ?: throw IllegalArgumentException("Компания не найдена")

        val fileName = fileStorageService.storeFile(file, company.id, type)

        when (type) {
            "contract" -> company.contract = file.originalFilename.toString()
            "application" -> company.application = file.originalFilename.toString()
        }

        carrierCompanyRepository.save(company)


        return fileName
    }

    fun deleteCarrierFile(name: String, type: String, fileName: String, token: String) {

        val currentUser = tokenService.getUserFromToken(token)

        val company = carrierCompanyRepository.findByName(name)
            ?: throw IllegalArgumentException("Компания не найдена")

        if (currentUser.company != company.userCompany)
            throw ForbiddenException("Нет доступа для редактирования данной компании")

        fileStorageService.deleteFile(company.id, type, fileName)

        when (type) {
            "contract" -> company.contract = null
            "application" -> company.application = null
        }

        carrierCompanyRepository.save(company)
    }

    fun getCarrierFile(name: String, type: String, fileName: String, token: String): Resource {
        val currentUser = tokenService.getUserFromToken(token)

        val company = carrierCompanyRepository.findByName(name)
            ?: throw IllegalArgumentException("Компания не найдена")

        if (currentUser.company != company.userCompany)
            throw ForbiddenException("Нет доступа для редактирования данной компании")


        return fileStorageService.loadFile(company.id, type, fileName)
    }


}
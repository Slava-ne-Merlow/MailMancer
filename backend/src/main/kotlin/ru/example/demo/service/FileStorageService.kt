package ru.example.demo.service

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import ru.example.demo.configuration.FileStorageProperties
import ru.example.demo.exception.type.StorageException

@Service
class FileStorageService(
    fileStorageProperties: FileStorageProperties
) {
    private val root: Path

    init {
        root = Paths.get(fileStorageProperties.uploadDir)
            .toAbsolutePath()
            .normalize()

        Files.createDirectories(root)
    }

    fun storeFile(file: MultipartFile, companyId: Long, type: String): String {
         val fileName = StringUtils.cleanPath(file.originalFilename ?: throw IllegalArgumentException("Original filename must not be null"))

        try {
             if (fileName.contains("..")) {
                throw StorageException("Sorry! Filename contains invalid path sequence $fileName")
            }

             val targetLocation = root.resolve(companyId.toString()).resolve(type).resolve(fileName).normalize()
            println(targetLocation)
            Files.createDirectories(targetLocation)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

            return fileName
        } catch (ex: IOException) {
            throw StorageException("Could not store file $fileName. Please try again!")
        }
    }

    fun loadFile(companyId: Long, type: String, fileName: String): Resource {
        try {
            val filePath = root.resolve(companyId.toString()).resolve(type).resolve(fileName).normalize()
            val resource = UrlResource(filePath.toUri())

            if (resource.exists()) {
                return resource
            } else {
                throw FileNotFoundException("Файл не найден $fileName")
            }
        } catch (ex: MalformedURLException) {
            throw FileNotFoundException("Файл не найден $fileName")
        }
    }

    fun deleteFile(companyId: Long, type: String, fileName: String) {
        val filePath = root.resolve(companyId.toString()).resolve(type).resolve(fileName).normalize()
        try {
            Files.deleteIfExists(filePath)
        } catch (ex: IOException) {
            throw StorageException("Не удалось удалить файл $fileName")
        }
    }

}

package ru.example.demo.unit.team

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.example.demo.dto.enums.UserRoles
import ru.example.demo.dto.model.Order
import ru.example.demo.dto.model.User
import ru.example.demo.dto.model.UserCompany
import ru.example.demo.exception.type.BadRequestException
import ru.example.demo.exception.type.ForbiddenException
import ru.example.demo.exception.type.UnauthorizedException
import ru.example.demo.unit.AbstractUnitTest
import java.time.LocalDateTime


class DeleteMemberTest : AbstractUnitTest() {
    @Test
    fun `успешное удаление участника команды`() {
        val userToken = "token1"

        val company = UserCompany(
            name = "name"
        )

        val head = User(
            name = "name1",
            login = "login1",
            email = "email@example.com1",
            password = "123456",
            role = UserRoles.HEAD,
            company = company,
            token = "token1"
        )

        val userToDelete = User(
            name = "name2",
            login = "login2",
            email = "email@example.com2",
            password = "123456",
            role = UserRoles.MANAGER,
            company = company,
            token = "token1"
        )

        val date = LocalDateTime.now()
        val order = Order(
            name = "String",
            downloadAddress = "String",
            deliveryAddress = "String",
            weight = 0.0,
            length = 0.0,
            width = 0.0,
            height = 0.0,
            additionalRequirements = "String",
            user = userToDelete,
            kind = "авто",
            createdDate = date
        )

        val orderUpdated = Order(
            name = "String",
            downloadAddress = "String",
            deliveryAddress = "String",
            weight = 0.0,
            length = 0.0,
            width = 0.0,
            height = 0.0,
            additionalRequirements = "String",
            user = head,
            kind = "авто",
            createdDate = date
        )

        every { userRepository.findByToken(userToken) } returns head.toEntity()
        every { userRepository.findByLogin(userToDelete.login) } returns userToDelete.toEntity()
        every { orderRepository.findAllByUser(userToDelete.toEntity()) } returns listOf(order.toEntity())
        every { orderRepository.saveAll(listOf(orderUpdated.toEntity())) } returns listOf(orderUpdated.toEntity())
        every { userRepository.deleteByLogin(userToDelete.login) } just Runs

        val message = teamService.deleteMember(token = userToken, login = userToDelete.login)

        // Проверки
        verify(exactly = 1) { userRepository.findByToken(userToken) }
        verify(exactly = 1) { orderRepository.findAllByUser(userToDelete.toEntity()) }
        verify(exactly = 1) { userRepository.findByLogin(userToDelete.login) }
        verify(exactly = 1) { orderRepository.saveAll(listOf(orderUpdated.toEntity())) }
        verify(exactly = 1) { userRepository.deleteByLogin(userToDelete.login) }

        message shouldBe "Пользователь был успешно удалён"
    }

    @Test
    fun `ошибка если токен авторизации не существет`() {
        val userToken = "token"

        every { userRepository.findByToken(userToken) } answers { null }

        val exception = shouldThrow<UnauthorizedException> {
            teamService.deleteMember(token = userToken, login = "")
        }

        exception.message should startWith("Недействителен токен авторизации")
    }

    @Test
    fun `ошибка если роль не HEAD`() {
        val userToken = "token"

        val company = UserCompany(
            name = "name",
        )

        val head = User(
            login = "login",
            name = "name1",
            password = "123456",
            email = "email@example.com",
            role = UserRoles.MANAGER,
            company = company,
            token = "token1"
        )
        every { userRepository.findByToken(userToken) } answers { head.toEntity() }

        val exception = shouldThrow<ForbiddenException> {
            teamService.deleteMember(token = userToken, login = "")
        }

        exception.message should startWith("Недостаточно прав")
    }

    @Test
    fun `ошибка если попытка удалить самого себя`() {
        val userToken = "token"

        val company = UserCompany(
            name = "name"
        )

        val head = User(
            name = "name1",
            login = "login",
            email = "email@example.com",
            password = "123456",
            role = UserRoles.HEAD,
            company = company,
            token = "token1"
        )


        every { userRepository.findByToken(userToken) } answers { head.toEntity() }

        val exception = shouldThrow<BadRequestException> {
            teamService.deleteMember(token = userToken, login = head.login)
        }

        exception.message should startWith("Вы не можете удалить сами себя")
    }

    @Test
    fun `ошибка если пользователь для удаления не существет`() {
        val userToken = "token"

        val company = UserCompany(
            name = "name"
        )

        val head = User(
            name = "name1",
            login = "login",
            email = "email@example.com",
            password = "123456",
            role = UserRoles.HEAD,
            company = company,
            token = "token1"
        )

        val login = "non-existent login"


        every { userRepository.findByToken(userToken) } answers { head.toEntity() }
        every { userRepository.findByLogin(login) } answers { null }

        val exception = shouldThrow<BadRequestException> {
            teamService.deleteMember(token = userToken, login = login)
        }

        exception.message should startWith("Такого логина не существует")
    }
}

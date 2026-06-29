package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.AgeRange
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.UserProfileRepository
import javax.inject.Inject

/**
 * Caso de uso: Criar perfil do usuário e conta corrente inicial.
 *
 * RN implícita: Um perfil começa no mês 1, com saldo zero e sem reserva.
 * O usuário aprende desde o início que precisa construir seu patrimônio
 * a partir das decisões que toma ao longo dos meses simulados.
 *
 * Não armazena dados pessoais sensíveis: o [name] é apenas um apelido livre.
 */
class CreateProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository
) {

    /**
     * @param name          Apelido do usuário (não pode ser vazio).
     * @param ageRange      Faixa etária para calibrar conteúdo educativo.
     * @param monthlyIncome Renda mensal simulada em centavos (deve ser positiva).
     * @return [UseCaseResult.Success] com o id do perfil criado, ou
     *         [UseCaseResult.Failure] com mensagem educativa.
     */
    suspend operator fun invoke(
        name: String,
        ageRange: AgeRange,
        monthlyIncome: Long
    ): UseCaseResult<Long> {
        if (name.isBlank()) {
            return UseCaseResult.Failure(
                "Escolha um nome ou apelido para o seu perfil. Pode ser qualquer coisa!"
            )
        }
        if (monthlyIncome <= 0) {
            return UseCaseResult.Failure(
                "Informe uma renda mensal maior que zero para começar a simulação."
            )
        }

        val profile = UserProfile(
            name = name.trim(),
            ageRange = ageRange,
            monthlyIncome = monthlyIncome,
            currentMonth = 1,
            createdAt = System.currentTimeMillis()
        )
        val profileId = userProfileRepository.save(profile)

        val account = Account(
            profileId = profileId,
            balance = 0L,
            emergencyReserveBalance = 0L,
            updatedAt = System.currentTimeMillis()
        )
        accountRepository.save(account)

        return UseCaseResult.Success(profileId)
    }
}

package com.finsim.app.data.local

import androidx.room.TypeConverter

/**
 * Conversores de tipo para o Room.
 *
 * Room suporta nativamente: Long, Int, String, Boolean, Double, Float, ByteArray.
 * Todos os enums do projeto são armazenados como String diretamente nos campos
 * das entidades, portanto nenhum conversor de enum é necessário aqui.
 *
 * Esta classe existe como ponto de extensão para tipos futuros
 * (ex.: List, BigDecimal se adotado) sem quebrar o esquema atual.
 *
 * Se BigDecimal for adotado na domain layer, adicionar aqui:
 *   @TypeConverter fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()
 *   @TypeConverter fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }
 */
class Converters {
    // Nenhum conversor necessário no MVP 1.
    // Todos os tipos usados (Long, Int, String, Boolean) são suportados nativamente.
}

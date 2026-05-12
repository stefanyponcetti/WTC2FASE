package com.example.wtcapp.ui.components

import java.util.Calendar

fun formatarDataParaExibicao(data: String?): String {
    val valor = data?.trim().orEmpty()
    if (valor.isBlank()) return ""

    val dataBrasileira = Regex("""^(\d{1,2})/(\d{1,2})/(\d{4})$""").matchEntire(valor)
    if (dataBrasileira != null) {
        val (dia, mes, ano) = dataBrasileira.destructured
        return if (isDataValida(dia, mes, ano)) {
            "%02d/%02d/%04d".format(dia.toInt(), mes.toInt(), ano.toInt())
        } else {
            valor
        }
    }

    val dataIso = Regex("""^(\d{4})-(\d{1,2})-(\d{1,2})""").find(valor)
    if (dataIso != null) {
        val (ano, mes, dia) = dataIso.destructured
        return if (isDataValida(dia, mes, ano)) {
            "%02d/%02d/%04d".format(dia.toInt(), mes.toInt(), ano.toInt())
        } else {
            valor
        }
    }

    return valor
}

fun converterDataParaApi(data: String): String? {
    val dataFormatada = formatarDataParaExibicao(data)
    val partes = dataFormatada.split("/")
    if (partes.size != 3) return null

    val (dia, mes, ano) = partes
    if (!isDataValida(dia, mes, ano)) return null

    return "%04d-%02d-%02dT00:00:00".format(ano.toInt(), mes.toInt(), dia.toInt())
}

fun converterDataParaApiDateOnly(data: String): String? {
    val dataFormatada = formatarDataParaExibicao(data)
    val partes = dataFormatada.split("/")
    if (partes.size != 3) return null

    val (dia, mes, ano) = partes
    if (!isDataValida(dia, mes, ano)) return null

    return "%04d-%02d-%02d".format(ano.toInt(), mes.toInt(), dia.toInt())
}

private fun isDataValida(dia: String, mes: String, ano: String): Boolean {
    return try {
        val diaInt = dia.toInt()
        val mesInt = mes.toInt()
        val anoInt = ano.toInt()

        Calendar.getInstance().apply {
            isLenient = false
            set(Calendar.YEAR, anoInt)
            set(Calendar.MONTH, mesInt - 1)
            set(Calendar.DAY_OF_MONTH, diaInt)
            time
        }
        true
    } catch (_: Exception) {
        false
    }
}

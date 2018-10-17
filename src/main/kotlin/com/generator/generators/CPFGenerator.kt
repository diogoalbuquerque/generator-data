package com.generator.generators

import org.springframework.stereotype.Component
import javax.swing.text.MaskFormatter

@Component
class CPFGenerator {

    fun generateWithoutMask(): String {
        val cpf = StringBuilder()
        for (i in 0..8) {
            cpf.append((Math.random() * 10).toInt())
        }
        return cpf.append(findVerifiedChar(cpf)).toString()
    }

    fun generateWithMask(): String {
        val mf = MaskFormatter("###.###.###-##")
        mf.valueContainsLiteralCharacters = false
        return mf.valueToString(generateWithoutMask())
    }

    fun validate(cpf: String): Boolean {
        val cpfWithoutMask = cpf.replace("-", "").replace(".", "")
        if (cpfWithoutMask.length != 11)
            return false

        return findVerifiedChar(StringBuilder(cpfWithoutMask.substring(0, 9))) == cpfWithoutMask.substring(9, 11)
    }

    private fun findVerifiedChar(value: StringBuilder): String {
        var valueAdd = 0
        var valuePercent = 10
        value.forEachIndexed { i, _ -> valueAdd += value.substring(i, i + 1).toInt() * valuePercent-- }
        val digOne = if ((valueAdd % 11 == 0) or (valueAdd % 11 == 1)) 0 else 11 - valueAdd % 11
        valueAdd = 0
        valuePercent = 11

        value.forEachIndexed { i, _ -> valueAdd += value.substring(i, i + 1).toInt() * valuePercent-- }
        valueAdd += digOne * 2
        val digTwo = if ((valueAdd % 11 == 0) or (valueAdd % 11 == 1)) 0 else 11 - valueAdd % 11
        return digOne.toString() + digTwo.toString()
    }
}

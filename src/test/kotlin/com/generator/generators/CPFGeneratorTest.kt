package com.generator.generators

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
class CPFGeneratorTest : GeneralUtilTest() {

    @Test
    fun should_verify_correct_cpf_with_mask() {
        assertThat(cpfGenerator.validate("908.555.237-00")).isEqualTo(true)
    }

    @Test
    fun should_verify_correct_cpf_without_mask() {
        assertThat(cpfGenerator.validate("20193719010")).isEqualTo(true)
    }

    @Test
    fun should_verify_incorrect_cpf_with_mask() {
        assertThat(cpfGenerator.validate("111.222.333-48")).isEqualTo(false)
    }

    @Test
    fun should_verify_incorrect_cpf_with_incorrect_length() {
        assertThat(cpfGenerator.validate("111.222.333-481")).isEqualTo(false)
    }

    @Test
    fun should_verify_incorrect_cpf_without_mask() {
        assertThat(cpfGenerator.validate("11111111111")).isEqualTo(true)
    }

    @Test
    fun should_verify_generated_cpf_with_mask() {
        assertThat(cpfGenerator.validate(cpfGenerator.generateWithMask())).isEqualTo(true)
    }

    @Test
    fun should_verify_generated_cpf_without_mask() {
        assertThat(cpfGenerator.validate(cpfGenerator.generateWithoutMask())).isEqualTo(true)
    }
}
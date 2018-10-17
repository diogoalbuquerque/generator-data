package com.generator.models

import com.fasterxml.jackson.annotation.JsonProperty

class LanguageTO(
    @JsonProperty("idiom") val idiom: String
)

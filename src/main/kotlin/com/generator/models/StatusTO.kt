package com.generator.models

import com.fasterxml.jackson.annotation.JsonProperty

class StatusTO(
    @JsonProperty("databaseStatus") val databaseStatus: String,
    @JsonProperty("databaseRows") val databaseRows: List<String>?
) {
    constructor(databaseStatus: String) :
            this(databaseStatus, null)
}
package com.mayte.huellitas_callejeras.models

data class Patient(val id: Int,
                   val name: String,
                   val breed: String,
                   val imageUrl: String,
                   var isAdopted: Boolean = false)

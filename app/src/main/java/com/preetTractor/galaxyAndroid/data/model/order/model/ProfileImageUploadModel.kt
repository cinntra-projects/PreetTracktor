package com.product.connect.models.loginProcessModels

class ProfileImageUploadModel(
    val message: String,
    val status: Int,
    val data: List<Data>,
    val errors: String,
) {
    data class Data(
        val createdAt: String,
        val id: Int,
        val distributor: Long,
        val updatedAt: String,
        val profileImage: String,
    )
}
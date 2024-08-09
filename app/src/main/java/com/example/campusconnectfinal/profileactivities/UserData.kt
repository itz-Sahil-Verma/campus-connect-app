package com.example.campusconnectfinal.profileactivities

data class UserData(
    val name: String = "",
    val phoneNumber: String = "",
    val profileImgUrl: String = "",
    val rollNo: String = "",
    val phonecode: String = "",
    val branch: String = "",
    val course: String = "",
    val onlineStatus: Boolean = true,
    val email: String = "",
    val uid: String = "",
    val password: String = "",
    val userType: String = "Email",
    val joinedCommunities: Map<String, Boolean> = emptyMap(),
    val ads: List<String> = emptyList(),
    val chatIds: List<String> = emptyList()
)


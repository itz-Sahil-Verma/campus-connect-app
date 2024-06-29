package com.example.campusconnectfinal

data class community_data(
    val communityId: String = "",
    val name: String = "",
    val description: String = "",
    val numberOfMembers: Int = 0,
    val userIds: List<String> = emptyList()
){
    // No-argument constructor
    constructor() : this("", "", "", 0, emptyList())
}

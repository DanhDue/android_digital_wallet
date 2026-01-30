package com.danhdue.authentication.domain.entities

data class Login(val accessToken: String, val refreshToken: String, val user: User)

package user.api

data class UserRegisterMapper(
    val username: String? = null,
    val password: String? = null,
    val displayName: String? = null
)

data class UserUpdateMapper(val displayName: String? = null)

data class UserViewMapper(val id: String, val username: String, val displayName: String)

package user.model

data class User(
    val id: String,
    val username: String,
    val password: String,
    var displayName: String
)
package user.model

import java.time.LocalDateTime

data class Post(val id: String, val date: LocalDateTime, val text: String)

data class User(
    val id: String,
    val username: String,
    val password: String,
    var displayName: String,
    val posts: MutableList<Post>,
    val friends: MutableList<User>
)
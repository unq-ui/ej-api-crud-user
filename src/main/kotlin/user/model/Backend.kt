package user.model

class Backend {
    private var userId = 0
    val users = mutableListOf<User>()

    fun register(username: String, password: String, displayName: String) {
        if(existUsername(username)) {
            throw UsernameExistException(username)
        }
        users.add(User("${++userId}", username, password, displayName))
    }

    fun getUser(id: String): User {
        return users.firstOrNull { it.id == id }
            ?: throw UserNotFoundException("Not found user with id $id")
    }

    fun updateWith(userId: String, updatedUser: User) {
        remove(userId)
        users.add(updatedUser)
    }

    fun remove(userId: String) = users.removeIf { it.id == userId }

    private fun existUsername(username: String): Boolean = users.any { it.username == username }
}

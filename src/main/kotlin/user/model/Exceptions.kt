package user.model

class UserNotFoundException(message: String): Exception(message)

class UsernameExistException(username: String) : Exception("Username $username is used")

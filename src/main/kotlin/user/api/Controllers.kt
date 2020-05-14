package user.api

import io.javalin.http.Context
import user.model.Backend

class UserController {
    private val backend = Backend()

    fun getAll(ctx: Context) {
        val users = backend.users.map { UserViewMapper(it.id, it.username, it.displayName) }
        ctx.json(users)
    }

    fun createUser(ctx: Context) {
        val newUser = ctx.bodyValidator<UserRegisterMapper>()
            .check({
                it.username != null && it.password != null && it.displayName != null
            }, "Invalid body: username, password and displayName should not be null")
            .get()

        backend.register(newUser.username!!, newUser.password!!, newUser.displayName!!)
        ctx.status(201)
        ctx.json(mapOf("message" to "ok"))
    }

    fun getUser(ctx: Context) {
        val user = backend.getUser(ctx.pathParam("id"))
        ctx.json(UserViewMapper(user.id, user.username, user.displayName))
    }

    fun updateUser(ctx: Context) {
        val userId = ctx.pathParam("id")
        val newProps = ctx.bodyValidator<UserUpdateMapper>()
            .check(
                { it.displayName != null },
                "Invalid body: username, password and displayName should not be null")
            .get()
        val currentUser = backend.getUser(userId)
        currentUser.displayName = newProps.displayName!!
        backend.updateWith(userId, currentUser)
        ctx.json(UserViewMapper(currentUser.id, currentUser.username, currentUser.displayName))
    }

    fun deleteUser(ctx: Context) {
        val userId = ctx.pathParam("id")
        backend.remove(userId)
        ctx.status(204)
    }
}

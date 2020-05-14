package user.api

import io.javalin.http.Context
import user.model.Backend
import user.model.User
import user.model.UsernameExist

class UserController {

    val backend = Backend(mutableListOf<User>())

    fun getAll(ctx: Context) {
        ctx.json(backend.users)
    }

    fun createUser(ctx: Context) {
        val newUser = ctx.bodyAsClass(UserRegister::class.java)
        try {
            backend.register(newUser.username, newUser.password, newUser.displayName)
            ctx.json("ok")
        } catch (exception: UsernameExist) {
            ctx.status(400)
            ctx.json(Handler(400, "Bad request", "Username is token"))
        }
    }

    fun getUser(ctx: Context) {
        val userId = ctx.pathParam("id")
        ctx.json(backend.getUser(userId))
    }

    fun updateUser(ctx: Context) {
        val userId = ctx.pathParam("id")
        val updateUser = ctx.bodyAsClass(UserUpdate::class.java)
        backend.getUser(userId).displayName = updateUser.displayName
        ctx.json("updated")
    }
}

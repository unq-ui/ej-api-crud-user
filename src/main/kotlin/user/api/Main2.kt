package user.api

import io.javalin.Javalin
import user.model.Backend
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.RouteOverviewPlugin
import user.model.UserNotFoundException
import user.model.UsernameExistException

fun main() {
    val app = Javalin.create {
        it.defaultContentType = "application/json"
        it.registerPlugin(RouteOverviewPlugin("/routes"))
        it.enableCorsForAllOrigins()
    }
    app.start(7000)

    val backend = Backend()

    app.routes {
        path("users") {
            get { ctx ->
                val users = backend.users.map { UserViewMapper(it.id, it.username, it.displayName) }
                ctx.json(users)
            }
            post { ctx ->
                val newUser = ctx.bodyValidator<UserRegisterMapper>()
                    .check({
                        it.username != null && it.password != null && it.displayName != null
                    }, "Invalid body: username, password and displayName should not be null")
                    .get()

                backend.register(newUser.username!!, newUser.password!!, newUser.displayName!!)
                ctx.status(201)
                ctx.json(mapOf("message" to "ok"))
            }
            path(":id") {
                get { ctx ->
                    val user = backend.getUser(ctx.pathParam("id"))
                    ctx.json(UserViewMapper(user.id, user.username, user.displayName))
                }
                put { ctx ->
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
                delete { ctx ->
                    val userId = ctx.pathParam("id")
                    backend.remove(userId)
                    ctx.status(204)
                }
            }
        }
    }

    app.exception(UserNotFoundException::class.java) { e, ctx ->
        ctx.status(404)
        ctx.json(mapOf(
            "message" to e.toString()
        ))
    }

    app.exception(UsernameExistException::class.java) { e, ctx ->
        ctx.status(400)
        ctx.json(mapOf(
            "message" to e.toString()
        ))
    }
}

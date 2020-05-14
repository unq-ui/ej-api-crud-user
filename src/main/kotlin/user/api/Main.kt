package user.api

import io.javalin.Javalin
import user.model.Backend
import user.model.UserNotFoundException
import user.model.UsernameExistException
import io.javalin.core.util.RouteOverviewPlugin

fun main() {
    val app = Javalin.create {
        it.defaultContentType = "application/json"
        it.registerPlugin(RouteOverviewPlugin("/routes"))
        it.enableCorsForAllOrigins()
    }
    app.start(7000)

    // ctx = Context
    // ctx.req -> Request
    // ctx.res -> Response
    app.get("/") { ctx -> ctx.result("Hello World") }

    // Init Backend
    val backend = Backend()

    // CRUD

    // GET /users
    app.get("/users") { ctx ->
        val users = backend.users.map { UserViewMapper(it.id, it.username, it.displayName) }
        ctx.json(users)
    }

    // GET /users/:id
    app.get("/users/:id") { ctx ->
        try {
            val user = backend.getUser(ctx.pathParam("id"))
            ctx.json(UserViewMapper(user.id, user.username, user.displayName))
        } catch (e: UserNotFoundException) {
            ctx.status(404)
            ctx.json(mapOf(
                "message" to e.message.toString()
            ))
        }
    }

    // POST /users
    app.post("/users") { ctx ->
        try {
            val newUser = ctx.bodyValidator<UserRegisterMapper>()
                .check({
                    it.username != null && it.password != null && it.displayName != null
                }, "Invalid body: username, password and displayName should not be null")
                .get()

            backend.register(newUser.username!!, newUser.password!!, newUser.displayName!!)
            ctx.status(201)
            ctx.json(mapOf("message" to "ok"))
        } catch (e: UsernameExistException) {
            ctx.status(400)
            ctx.json(mapOf(
                "message" to e.message.toString()
            ))
        }
    }

    // PUT /users/:id
    app.put("/users/:id") { ctx ->
        try {
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
        } catch (e: UserNotFoundException) {
            ctx.status(404)
            ctx.json(mapOf(
                "message" to e.message.toString()
            ))
        }
    }

    // DELETE /users/:id
    app.delete("/users/:id") { ctx ->
        try {
            val userId = ctx.pathParam("id")
            backend.remove(userId)
            ctx.status(204)
        } catch (e: UserNotFoundException) {
            ctx.status(404)
            ctx.json(mapOf(
                "message" to e.message.toString()
            ))
        }
    }
}

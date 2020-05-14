package user.api

import user.model.UserNotFoundException
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.http.BadRequestResponse
import io.javalin.http.NotFoundResponse
import user.model.UsernameExistException

fun main() {
    val app = Javalin.create {
        it.defaultContentType = "application/json"
        it.registerPlugin(RouteOverviewPlugin("/routes"))
        it.enableCorsForAllOrigins()
    }
    app.start(7000)

    val userController = UserController()
    app.routes {
        path("users") {
            get(userController::getAll)
            post(userController::createUser)
            path(":id") {
                get(userController::getUser)
                put(userController::updateUser)
                delete(userController::deleteUser)
            }
        }
    }

    app.exception(UserNotFoundException::class.java) { e, _ ->
        throw NotFoundResponse(e.toString())
    }

    app.exception(UsernameExistException::class.java) { e, _ ->
        throw BadRequestResponse(e.toString())
    }
}

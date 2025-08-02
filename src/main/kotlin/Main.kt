import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

@Serializable
data class User(val id: Int, var name: String, var title: String)

val users = mutableListOf(
    User(1, "Luffy", "Pirate King"),
    User(2, "Zoro", "Swordsman")
)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        route("/users") {

            get {
                call.respond(users)
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val user = users.find { it.id == id }
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            post {
                val newUser = call.receive<User>()
                if (users.any { it.id == newUser.id }) {
                    call.respond(HttpStatusCode.Conflict, "User with ID ${newUser.id} already exists")
                } else {
                    users.add(newUser)
                    call.respond(HttpStatusCode.Created, newUser)
                }
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val updatedUser = call.receive<User>()
                val index = users.indexOfFirst { it.id == id }
                if (index != -1) {
                    users[index] = updatedUser
                    call.respond(HttpStatusCode.OK, updatedUser)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            patch("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val patchData = call.receive<Map<String, String>>()
                val user = users.find { it.id == id }

                if (user != null) {
                    patchData["name"]?.let { user.name = it }
                    patchData["title"]?.let { user.title = it }
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val removed = users.removeIf { it.id == id }
                if (removed) {
                    call.respond(HttpStatusCode.OK, "User deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }
        }
    }
}

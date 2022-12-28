import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceOrFileSource
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.create.embed
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class Config(val bot: Bot, val alcohols: Alcohols)

data class Bot(val token: String)

data class Alcohols(val good: String, val bad: String)

val logger: Logger = LoggerFactory.getLogger("Main")

suspend fun main() {
    val config = ConfigLoaderBuilder.default()
        .addEnvironmentSource()
        .addResourceOrFileSource("/application.yaml")
        .build()
        .loadConfigOrThrow<Config>()
    val badAlcohols = config.alcohols.bad.splitToList()
    val goodAlcohols = config.alcohols.good.splitToList()

    val kord = Kord(config.bot.token) {
        defaultStrategy = EntitySupplyStrategy.rest
    }

    logger.info("Logging in...")
    kord.login {
        kord.on<ReadyEvent> {
            logger.info("Logged in!")

            kord.editPresence {
                status = PresenceStatus.Online
                watching("L'Oréal TV")
            }
        }
        kord.on<MessageCreateEvent> {
            if (message.channel.asChannel() !is DmChannel) return@on
            if (message.type != MessageType.Default) return@on

            val receivedIngredients = message.content

            val badMatch = receivedIngredients.findAllIn(badAlcohols)
            val goodMatch = receivedIngredients.findAllIn(goodAlcohols)

            logger.debug("Received: $receivedIngredients")

            message.reply {
                embed {
                    title = "Your product details"

                    description = when {
                        badMatch.isEmpty() && goodMatch.isNotEmpty() -> "It is very good!! :heart_eyes:"
                        badMatch.isNotEmpty() && goodMatch.isEmpty() -> "NO NO NO! :hot_face:"
                        badMatch.isNotEmpty() -> "It has some bad stuff! :confused:"
                        else -> "It is neutral. ¯\\_(ツ)_/¯"
                    }

                    if (badMatch.isNotEmpty()) {
                        field {
                            name = "Bad alcohols"
                            value = badMatch.joinToString()
                        }
                    }

                    if (goodMatch.isNotEmpty()) {
                        field {
                            name = "Good alcohols"
                            value = goodMatch.joinToString()
                        }
                    }
                }
            }
        }
    }
}

fun String.splitToList() = split(",").map(String::removeSymbols).map(String::trim)

fun String.findAllIn(values: List<String>): List<String> {
    val target = this.splitToList()
    return values.filter { value -> target.any { value.contentEquals(it.removeSymbols(), true) } }
}

fun String.removeSymbols() = filter { it.isLetterOrDigit() || it.isWhitespace() }

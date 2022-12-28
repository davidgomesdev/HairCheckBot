package me.davidgomes.haircheckbot

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceOrFileSource
import dev.kord.common.Color
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
import me.davidgomes.haircheckbot.model.Config
import me.davidgomes.haircheckbot.model.ResultType
import mu.KotlinLogging

val logger = KotlinLogging.logger { }

suspend fun main() {
    val config =
        ConfigLoaderBuilder.default()
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

            logger.debug { "Received: $receivedIngredients" }

            message.reply {
                embed {
                    title = "Your product details"

                    val type = when {
                        badMatch.isEmpty() && goodMatch.isNotEmpty() -> ResultType.Good
                        badMatch.isNotEmpty() && goodMatch.isEmpty() -> ResultType.Bad
                        badMatch.isNotEmpty() -> ResultType.NotSoGood
                        else -> ResultType.Neutral
                    }

                    logger.info { "The provided ingredients are '$type'" }

                    color = Color(type.color)
                    description = type.message

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

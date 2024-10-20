package com.example.homebookshelfapi

import com.example.homebookshelfapi.config.AppConfig
import me.paulschwarz.springdotenv.DotenvPropertySource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(AppConfig::class)
class Application

fun main(args: Array<String>) {
    val applicationContext = AnnotationConfigApplicationContext()
    DotenvPropertySource.addToEnvironment(applicationContext.getEnvironment())

    runApplication<Application>(*args)
}

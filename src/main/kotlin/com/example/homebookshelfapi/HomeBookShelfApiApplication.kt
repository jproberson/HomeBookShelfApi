package com.example.homebookshelfapi

import com.example.homebookshelfapi.config.AppConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(AppConfig::class)
class HomeBookShelfApiApplication

fun main(args: Array<String>) {
	runApplication<HomeBookShelfApiApplication>(*args)
}

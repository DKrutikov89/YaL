package com.yandex.lavka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// SpringBootApplication — специальная аннотация, которая включает все настройки Spring Boot.
// @SpringBootApplication =
//    @Configuration +        // 1. Говорит: "здесь будут настройки"
// Здесь можно объявлять бины (объекты, которыми управляет Spring)

//    @EnableAutoConfiguration + // 2. Говорит: "настрой себя сам"
//Включает автоматическую настройку!
//Говорит Spring: "Включи все автоматические настройки на основе зависимостей в classpath"
//Например: Если есть spring-boot-starter-web → автоматически настрой веб-сервер
//Если есть spring-boot-starter-data-jpa → настрой базу данных
//Если есть spring-boot-starter-thymeleaf → настрой шаблонизатор

//    @ComponentScan          // 3. Говорит: "найди все компоненты"
// // Ищет компоненты в пакете com.yandex.lavka и подпакетах!
//Говорит Spring: "Обыщи все пакеты, начиная с этого, и найди классы с аннотациями:"
//@RestController (находит CourierController)
//@Service (находит CourierService)
//@Component
//@Repository
//И другие
@SpringBootApplication
public class YandexLavkaApplication {

	public static void main(String[] args) {

		SpringApplication.run(YandexLavkaApplication.class, args);
	}

}
// Это запускает все приложение. Давай посмотрим, что происходит внутри:
//
//Пошагово:
//text
//1. SpringApplication.run() запускается
//   ↓
//2. Создается контекст приложения (ApplicationContext)
//   ↓
//3. Включается @ComponentScan
//   → Находит CourierController (@RestController)
//   → Находит CourierService (@Service)
//   → Находит GlobalExceptionHandler (@RestControllerAdvice)
//   ↓
//4. Включается @EnableAutoConfiguration
//   → Настраивает веб-сервер (Tomcat)
//   → Настраивает Jackson (для JSON)
//   → Настраивает все остальное автоматически
//   ↓
//5. Создаются бины (объекты)
//   → CourierService
//   → CourierController (в него внедряется CourierService)
//   → GlobalExceptionHandler
//   ↓
//6. Запускается встроенный Tomcat
//   → Открывает порт 8080
//   → Приложение готово принимать запросы
//   ↓
//7. В консоли видим:
//   Started YandexLavkaApplication in 2.5 seconds


// Это сердце твоего приложения. Когда ты его запускаешь:
//Spring поднимает веб-сервер
//Сканирует все компоненты
//Настраивает всё автоматически
//Твое приложение готово принимать запросы!
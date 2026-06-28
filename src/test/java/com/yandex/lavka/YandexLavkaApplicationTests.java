package com.yandex.lavka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Базовый тест для проверки, что Spring контекст запускается
 */
@SpringBootTest //
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class YandexLavkaApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
		// Тест проходит, если Spring контекст успешно загружается
		// Проверяет корректность конфигурации и зависимостей
	}

	@Test
	void createCouriersReturnsCreatedCourier() throws Exception {
		String requestBody = """
				{
				  "couriers": [
				    {
				      "courier_type": "AUTO",
				      "regions": [1, 2],
				      "working_hours": ["09:00-18:00"]
				    }
				  ]
				}
				""";

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.couriers[0].courier_id").value(1))
				.andExpect(jsonPath("$.couriers[0].courier_type").value("AUTO"))
				.andExpect(jsonPath("$.couriers[0].regions[0]").value(1))
				.andExpect(jsonPath("$.couriers[0].working_hours[0]").value("09:00-18:00"));
	}

	@Test
	void getAllCouriersReturnsCreatedCourier() throws Exception {
		String requestBody = """
				{
				  "couriers": [
				    {
				      "courier_type": "FOOT",
				      "regions": [12],
				      "working_hours": ["10:00-19:00"]
				    }
				  ]
				}
				""";

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk());

		mockMvc.perform(get("/couriers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].courier_id").value(1))
				.andExpect(jsonPath("$[0].courier_type").value("FOOT"))
				.andExpect(jsonPath("$[0].regions[0]").value(12))
				.andExpect(jsonPath("$[0].working_hours[0]").value("10:00-19:00"));
	}

	@Test
	void getCourierByIdReturnsNotFoundForUnknownCourier() throws Exception {
		mockMvc.perform(get("/couriers/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").value("Courier not found with id: 999"));
	}
	@Test
	void createCourierWithValidWorkingHours_ShouldSucceed() throws Exception {
		String requestBody = """
                {
                  "couriers": [
                    {
                      "courier_type": "AUTO",
                      "regions": [1, 2],
                      "working_hours": ["09:00-18:00"]
                    }
                  ]
                }
                """;

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.couriers[0].courier_id").value(1));
	}

	@Test
	void createCourierWithInvalidWorkingHours_StartAfterEnd_ShouldFail() throws Exception {
		String requestBody = """
                {
                  "couriers": [
                    {
                      "courier_type": "AUTO",
                      "regions": [1, 2],
                      "working_hours": ["18:00-09:00"]
                    }
                  ]
                }
                """;

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Failed"));
	}

	@Test
	void createCourierWithInvalidWorkingHours_StartEqualsEnd_ShouldFail() throws Exception {
		String requestBody = """
                {
                  "couriers": [
                    {
                      "courier_type": "AUTO",
                      "regions": [1, 2],
                      "working_hours": ["09:00-09:00"]
                    }
                  ]
                }
                """;

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Failed"));
	}

	@Test
	void createCourierWithMultipleWorkingHours_OneInvalid_ShouldFail() throws Exception {
		String requestBody = """
                {
                  "couriers": [
                    {
                      "courier_type": "AUTO",
                      "regions": [1, 2],
                      "working_hours": [
                        "09:00-12:00",
                        "13:00-18:00",
                        "20:00-19:00"
                      ]
                    }
                  ]
                }
                """;

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Failed"));
	}

	@Test
	void createCourierWithValidMultipleWorkingHours_ShouldSucceed() throws Exception {
		String requestBody = """
                {
                  "couriers": [
                    {
                      "courier_type": "BIKE",
                      "regions": [5],
                      "working_hours": [
                        "09:00-12:00",
                        "13:00-18:00"
                      ]
                    }
                  ]
                }
                """;

		mockMvc.perform(post("/couriers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.couriers[0].courier_id").value(1));
	}
}


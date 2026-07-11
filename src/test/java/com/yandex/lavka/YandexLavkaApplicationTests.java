package com.yandex.lavka;

import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.enums.CourierType;
import com.yandex.lavka.repository.CourierRepository;
import com.yandex.lavka.service.CourierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class YandexLavkaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private CourierService courierService;

    @BeforeEach
    void clearDatabase() {
        courierRepository.deleteAll();
    }

    @Test
    void contextLoads() {
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
                .andExpect(jsonPath("$.couriers[0].courier_id").isNumber())
                .andExpect(jsonPath("$.couriers[0].courier_type").value("AUTO"))
                .andExpect(jsonPath("$.couriers[0].regions[0]").value(1))
                .andExpect(jsonPath("$.couriers[0].working_hours[0]").value("09:00-18:00"));
    }

    @Test
    void getAllCouriersReturnsCreatedCourier() throws Exception {
        createCourier("FOOT", 12, "10:00-19:00");

        mockMvc.perform(get("/couriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courier_type").value("FOOT"))
                .andExpect(jsonPath("$[0].regions[0]").value(12))
                .andExpect(jsonPath("$[0].working_hours[0]").value("10:00-19:00"));
    }

    @Test
    void getCouriersSupportsLimitAndOffset() throws Exception {
        createCourier("FOOT", 1, "09:00-12:00");
        createCourier("BIKE", 2, "10:00-13:00");
        createCourier("AUTO", 3, "11:00-14:00");

        mockMvc.perform(get("/couriers?limit=1&offset=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courier_type").value("BIKE"))
                .andExpect(jsonPath("$[0].regions[0]").value(2));
    }

    @Test
    void getCouriersRejectsNegativeOffset() throws Exception {
        mockMvc.perform(get("/couriers?limit=5&offset=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void getCourierByIdReturnsNotFoundForUnknownCourier() throws Exception {
        mockMvc.perform(get("/couriers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Courier not found with id: 999"));
    }

    @Test
    void findByCourierTypeReturnsOnlyMatchingCouriers() throws Exception {
        createCourier("FOOT", 1, "09:00-12:00");
        createCourier("BIKE", 2, "10:00-13:00");
        createCourier("FOOT", 3, "14:00-18:00");

        List<CourierDto> footCouriers = courierService.findByCourierType(CourierType.FOOT);

        assertThat(footCouriers).hasSize(2);
        assertThat(footCouriers).allMatch(courier -> courier.getCourierType() == CourierType.FOOT);
    }

    @Test
    void findByCourierTypeWithPaginationUsesLimitAndOffset() throws Exception {
        createCourier("FOOT", 1, "09:00-12:00");
        createCourier("FOOT", 2, "10:00-13:00");
        createCourier("FOOT", 3, "14:00-18:00");

        List<CourierDto> pagedCouriers = courierService.findByCourierType(CourierType.FOOT, 1, 1);

        assertThat(pagedCouriers).hasSize(1);
        assertThat(pagedCouriers.get(0).getRegions()).containsExactly(2);
    }

    @Test
    void findByRegionReturnsOnlyMatchingCouriers() throws Exception {
        createCourier("AUTO", 5, "09:00-18:00");
        createCourierWithRegions("BIKE", new int[]{5, 7}, "10:00-12:00");
        createCourier("FOOT", 9, "12:00-15:00");

        List<CourierDto> couriers = courierService.findByRegion(5);

        assertThat(couriers).hasSize(2);
        assertThat(couriers).allMatch(courier -> courier.getRegions().contains(5));
    }

    @Test
    void findByRegionWithPaginationUsesLimitAndOffset() throws Exception {
        createCourierWithRegions("AUTO", new int[]{4, 8}, "09:00-18:00");
        createCourierWithRegions("BIKE", new int[]{4, 9}, "10:00-12:00");
        createCourierWithRegions("FOOT", new int[]{4, 10}, "12:00-15:00");

        List<CourierDto> couriers = courierService.findByRegion(4, 1, 1);

        assertThat(couriers).hasSize(1);
        assertThat(couriers.get(0).getCourierType()).isEqualTo(CourierType.BIKE);
    }

    @Test
    void countCouriersReturnsPersistedAmount() throws Exception {
        createCourier("AUTO", 1, "09:00-18:00");
        createCourier("BIKE", 2, "10:00-18:00");

        assertThat(courierService.countCouriers()).isEqualTo(2);
        assertThat(courierRepository.countByCourierType(CourierType.AUTO)).isEqualTo(1);
    }

    @Test
    void createCourierWithValidWorkingHoursShouldSucceed() throws Exception {
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
                .andExpect(jsonPath("$.couriers[0].courier_id").isNumber());
    }

    @Test
    void createCourierWithInvalidWorkingHoursStartAfterEndShouldFail() throws Exception {
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
    void createCourierWithInvalidWorkingHoursStartEqualsEndShouldFail() throws Exception {
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
    void createCourierWithMultipleWorkingHoursOneInvalidShouldFail() throws Exception {
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
    void createCourierWithValidMultipleWorkingHoursShouldSucceed() throws Exception {
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
                .andExpect(jsonPath("$.couriers[0].courier_id").isNumber());
    }

    private void createCourier(String courierType, int region, String workingHours) throws Exception {
        createCourierWithRegions(courierType, new int[]{region}, workingHours);
    }

    private void createCourierWithRegions(String courierType, int[] regions, String workingHours) throws Exception {
        String regionsJson = java.util.Arrays.stream(regions)
                .mapToObj(String::valueOf)
                .collect(java.util.stream.Collectors.joining(", "));

        String requestBody = """
                {
                  "couriers": [
                    {
                      "courier_type": "%s",
                      "regions": [%s],
                      "working_hours": ["%s"]
                    }
                  ]
                }
                """.formatted(courierType, regionsJson, workingHours);

        mockMvc.perform(post("/couriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}

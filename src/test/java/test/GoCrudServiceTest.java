package test;

import controller.GoCrudClient;
import dto.EntityGetAllRequest;
import dto.EntityRequest;
import dto.EntityResponse;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static config.AppConfig.CONNECTION_TIMEOUT;
import static config.AppConfig.SOCKET_TIMEOUT;
import static config.AppConfig.URL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static util.JsonParser.loadObject;

public class GoCrudServiceTest {

    private static GoCrudClient client;
    private static List<Integer> createdEntityIds;

    @BeforeAll
    static void setup() {
        client = new GoCrudClient(URL, CONNECTION_TIMEOUT, SOCKET_TIMEOUT);
        createdEntityIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        createdEntityIds.forEach(
                id -> client.deleteEntity(id)
        );
        createdEntityIds.clear();
    }


    @Test
    @Feature("Создание сущности")
    @DisplayName("Тест создания сущности")
    void testCreateEntity() {
        EntityRequest createEntityRequest = loadObject("request/req_create.json", EntityRequest.class);

        Integer createdId = createAndTrackEntity(createEntityRequest);
        Response response = client.getEntityById(createdId);
        EntityResponse entityResponse = response.getBody().as(EntityResponse.class);

        assertThat(response.getStatusCode())
                .as("Entity сохранилась в сервисом")
                .isEqualTo(200);
        assertEntityMatches(entityResponse, createEntityRequest);
    }

    @Test
    @Feature("Чтение по Id")
    @DisplayName("Тест получения сущности по ID")
    void testGetEntityById() {
        EntityRequest createEntityRequest = loadObject("request/req_create.json", EntityRequest.class);
        Integer createdId = createAndTrackEntity(createEntityRequest);

        Response response = client.getEntityById(createdId);
        EntityResponse entityResponse = response.getBody().as(EntityResponse.class);

        assertThat(response.getStatusCode())
                .as("Ответ сервиса 200")
                .isEqualTo(200);
        assertEntityMatches(entityResponse, createEntityRequest);
    }

    @Test
    @Feature("Update")
    @DisplayName("Тест обновления сущности")
    void testUpdateEntity() {
        EntityRequest createEntityRequest = loadObject("request/req_create.json", EntityRequest.class);
        EntityRequest updateEntityRequest = loadObject("request/req_update.json", EntityRequest.class);

        Integer createdId = createAndTrackEntity(createEntityRequest);
        Response updateResponse = client.updateEntity(createdId, updateEntityRequest);
        Response updatedEntityResponse = client.getEntityById(createdId);
        EntityResponse entityResponse = updatedEntityResponse.getBody().as(EntityResponse.class);

        assertThat(updateResponse.getStatusCode())
                .as("Ответ сервиса 204")
                .isEqualTo(204);
        assertEntityMatches(entityResponse, updateEntityRequest);
    }

    @Test
    @Feature("Чтение всех сущностей")
    @DisplayName("Тест получения всех сущностей")
    void testGetAllEntities() {
        EntityRequest createRequest = loadObject("request/req_create.json", EntityRequest.class);
        EntityGetAllRequest getAllRequest = loadObject("request/req_get_all.json", EntityGetAllRequest.class);

        Integer id1 = createAndTrackEntity(createRequest);
        Integer id2 = createAndTrackEntity(createRequest);

        Response getAllResponse = client.getAllEntities(getAllRequest);
        List<EntityResponse> entityResponseList = getAllResponse.getBody()
                .jsonPath()
                .getList("entity", EntityResponse.class);

        List<Integer> createdIds = entityResponseList.stream()
                .map(EntityResponse::getId)
                .collect(Collectors.toList());

        assertThat(getAllResponse.getStatusCode())
                .as("Ответ сервиса 200")
                .isEqualTo(200);
        assertThat(entityResponseList.size())
                .as("Проверка, на то, что объекты попали в приложение")
                .isEqualTo(2);
        AssertionsForInterfaceTypes
                .assertThat(createdIds)
                .containsExactlyInAnyOrder(id1, id2);

    }

    @Test
    @Feature("Удаление сущности")
    @DisplayName("Тест удаления сущности")
    void testDeleteEntity() {
        EntityRequest createRequest = loadObject("request/req_create.json", EntityRequest.class);

        Integer createdId = createAndTrackEntity(createRequest);
        Response deleteResponse = client.deleteEntity(createdId);
        Response entityResponse = client.getEntityById(createdId);

        assertThat(deleteResponse.getStatusCode())
                .as("Ответ сервиса 204")
                .isEqualTo(204);
        assertThat(entityResponse.getStatusCode())
                .as("Сущность не найдена")
                .isEqualTo(500);
    }

    private Integer createAndTrackEntity(EntityRequest request) {
        Response response = client.createEntity(request);
        Integer id = Integer.valueOf(response.getBody().asString());
        createdEntityIds.add(id);
        return id;
    }

    private void assertEntityMatches(EntityResponse actualEntityResponse, EntityRequest expectedEntityRequest) {
        assertThat(actualEntityResponse)
                .as("Проверка соответствия полей объекта")
                .usingRecursiveComparison()
                .ignoringFields("id", "addition.id")
                .isEqualTo(expectedEntityRequest);
    }

}

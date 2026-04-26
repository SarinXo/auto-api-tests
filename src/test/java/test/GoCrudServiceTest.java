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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static util.JsonParser.loadObject;

public class GoCrudServiceTest {

    private static GoCrudClient client;
    private static List<Integer> createdEntityIds;

    @BeforeAll
    static void setup() {
        client = new GoCrudClient("http://localhost:8080");
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
    @DisplayName("1. Тест создания сущности")
    void testCreateEntity() {
        EntityRequest request = loadObject("request/req_create.json", EntityRequest.class);

        Response created = client.createEntity(request);
        Integer createdId = Integer.valueOf(created.getBody().asString());
        createdEntityIds.add(createdId);
        Response getAfterCreate = client.getEntityById(createdId);
        EntityResponse response = getAfterCreate.getBody().as(EntityResponse.class);

        assertThat(created.getStatusCode())
                .as("Ответ сервиса 200")
                .isEqualTo(200);
        assertThat(getAfterCreate.getStatusCode())
                .as("Entity сохранилась в сервисом")
                .isEqualTo(200);
        assertThat(response)
                .as("Проверка, на то, что объект записался верно")
                .usingRecursiveComparison()
                .ignoringFields("id", "addition.id")
                .isEqualTo(request);
    }

    @Test
    @Feature("Чтение по Id")
    @DisplayName("2. Тест получения сущности по ID")
    void testGetEntityById() {
        EntityRequest request = loadObject("request/req_create.json", EntityRequest.class);

        Response created = client.createEntity(request);
        Integer createdId = Integer.valueOf(created.getBody().asString());
        createdEntityIds.add(createdId);
        Response getAfterCreate = client.getEntityById(createdId);
        EntityResponse response = getAfterCreate.getBody().as(EntityResponse.class);

        assertThat(getAfterCreate.getStatusCode())
                .as("Ответ сервиса 200")
                .isEqualTo(200);
        assertThat(response)
                .as("Проверка, на то, что объект записался верно")
                .usingRecursiveComparison()
                .ignoringFields("id", "addition.id")
                .isEqualTo(request);
    }

    @Test
    @Feature("Update")
    @DisplayName("3. Тест обновления сущности")
    void testUpdateEntity() {
        EntityRequest request = loadObject("request/req_create.json", EntityRequest.class);
        EntityRequest update = loadObject("request/req_update.json", EntityRequest.class);

        Response created = client.createEntity(request);
        Integer createdId = Integer.valueOf(created.getBody().asString());
        createdEntityIds.add(createdId);
        Response updated = client.updateEntity(createdId, update);
        Response getAfterCreate = client.getEntityById(createdId);
        EntityResponse response = getAfterCreate.getBody().as(EntityResponse.class);

        assertThat(updated.getStatusCode())
                .as("Ответ сервиса 204")
                .isEqualTo(204);
        assertThat(response)
                .as("Проверка, на то, что объект обновился")
                .usingRecursiveComparison()
                .ignoringFields("id", "addition.id")
                .isEqualTo(update);
    }

    @Test
    @Feature("Чтение всех сущностей")
    @DisplayName("4. Тест получения всех сущностей")
    void testGetAllEntities() {
        EntityRequest createRequest = loadObject("request/req_create.json", EntityRequest.class);
        EntityGetAllRequest getAllRequest = loadObject("request/req_get_all.json", EntityGetAllRequest.class);

        Response created = client.createEntity(createRequest);
        Integer createdId = Integer.valueOf(created.getBody().asString());
        createdEntityIds.add(createdId);
        Response created2 = client.createEntity(createRequest);
        Integer createdId2 = Integer.valueOf(created2.getBody().asString());
        createdEntityIds.add(createdId2);

        Response getAllResponse = client.getAllEntities(getAllRequest);
        List<EntityResponse> response = getAllResponse.getBody()
                .jsonPath()
                .getList("entity", EntityResponse.class);

        List<Integer> createdIds = response.stream()
                .map(EntityResponse::getId)
                .collect(Collectors.toList());

        assertThat(getAllResponse.getStatusCode())
                .as("Ответ сервиса 200")
                .isEqualTo(200);
        assertThat(response.size())
                .as("Проверка, на то, что объекты попали в приложение")
                .isEqualTo(2);
        AssertionsForInterfaceTypes
                .assertThat(createdIds)
                .containsExactlyInAnyOrderElementsOf(createdEntityIds);

    }

    @Test
    @Feature("Удаление сущности")
    @DisplayName("5. Тест удаления сущности")
    void testDeleteEntity() {
        EntityRequest request = loadObject("request/req_create.json", EntityRequest.class);

        Response created = client.createEntity(request);
        Integer createdId = Integer.valueOf(created.getBody().asString());
        createdEntityIds.add(createdId);
        Response getAfterUpdate = client.deleteEntity(createdId);
        Response response = client.getEntityById(createdId);

        assertThat(getAfterUpdate.getStatusCode())
                .as("Ответ сервиса 204")
                .isEqualTo(204);
        assertThat(response.getStatusCode())
                .as("Сущность не найдена")
                .isEqualTo(500);
    }

}

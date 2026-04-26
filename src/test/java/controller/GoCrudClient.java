package controller;

import dto.EntityGetAllRequest;
import dto.EntityRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GoCrudClient {

    private final RequestSpecification spec;

    public GoCrudClient(String baseUrl) {
        this.spec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Step("Создать сущность: {entity}")
    public Response createEntity(EntityRequest entity) {
        return RestAssured.given()
                .spec(spec)
                .body(entity)
                .post("/api/create");
    }

    @Step("Удалить сущность с Id: {entityId}")
    public Response deleteEntity(Integer entityId) {
        return RestAssured.given()
                .spec(spec)
                .delete("/api/delete/" + entityId);
    }

    @Step("Получить сущность с Id: {entityId}")
    public Response getEntityById(Integer entityId) {
        return RestAssured.given()
                .spec(spec)
                .get("/api/get/" + entityId);
    }

    @Step("Получить сущность с Id: {entityId}")
    public Response getAllEntities(EntityGetAllRequest request) {
        RequestSpecification req = RestAssured.given().spec(spec);

        if (request.getPage() != null) {
            req.queryParam("page", request.getPage());
        }
        if (request.getTitle() != null) {
            req.queryParam("title", request.getTitle());
        }
        if (request.getVerified() != null) {
            req.queryParam("verified", request.getVerified());
        }
        if (request.getPerPage() != null) {
            req.queryParam("perPage", request.getPerPage());
        }

        return req.get("/api/getAll");
    }

    @Step("Обновить сущность с Id: {entityId}")
    public Response updateEntity(Integer entityId, EntityRequest entity) {
        return RestAssured.given()
                .spec(spec)
                .body(entity)
                .patch("/api/patch/" + entityId);
    }

}

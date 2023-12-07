package ru.tinkoff.qa.apitests;

import apiModels.petDeleteResponse.PetDeleteResponse;
import apiModels.petRequest.Category;
import apiModels.petRequest.PetRequest;
import apiModels.petRequest.TagsItem;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetsApiTests {
    static PetRequest pet;
    static Response responseInitial;

    @BeforeAll
    public static void setUp() {
        Category category = new Category();
        category.setId(189);
        category.setName("Animal");

        TagsItem tag = new TagsItem();
        tag.setId(1);
        tag.setName("Friendly");

        pet = new PetRequest();
        pet.setId(123);
        pet.setCategory(category);
        pet.setName("Buddy22");
        pet.setPhotoUrls(Arrays.asList("https://cdn.pixabay.com/photo/2023/10/01/12/56/shih-tzu-8287355_1280.jpg"));
        pet.setTags(Arrays.asList(tag));
        pet.setStatus("available");

        responseInitial = given()
                .contentType(ContentType.JSON)
                .body(pet)
                .post("https://petstore.swagger.io/v2/pet");
    }

    @Test
    @Order(1)
    public void addPet() {
        PetRequest responseBody = responseInitial.as(PetRequest.class);
        assertEquals(200, responseInitial.getStatusCode(), "Status code check");
        System.out.println(responseBody.toString());
        assertTrue(pet.equals(responseBody), "Check pet was created");
    }

    @Test
    @Order(2)
    public void getPetById() {
        String url = "https://petstore.swagger.io/v2/pet/" + pet.getId();
        PetRequest petRequest = given()
                .contentType(ContentType.JSON)
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .as(PetRequest.class);

        assertEquals(pet, petRequest, "Check response body is the same as the pet created");
    }

    @Test
    @Order(3)
    public void modifyPet() {
        pet.setName("Edited name");
        PetRequest editPetName = given()
                .contentType(ContentType.JSON)
                .body(pet)
                .put("https://petstore.swagger.io/v2/pet")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(PetRequest.class);
        assertEquals(pet, editPetName, "Check response body is the same as the pet created");
    }

    @Test
    @Order(4)
    public void deletePet() {
        String urlDelete = "https://petstore.swagger.io/v2/pet/" + pet.getId();
        PetDeleteResponse petDeleteResponse = given()
                .contentType(ContentType.JSON)
                .delete(urlDelete)
                .then()
                .statusCode(200)
                .extract()
                .as(PetDeleteResponse.class);
        assertEquals(petDeleteResponse.getMessage(), String.valueOf(pet.getId()));
    }

    @Test
    @Order(5)
    public void getPetByIdInexistentId() {
        String url = "https://petstore.swagger.io/v2/pet/ewg# wegop[ w e#&*gweg";
        Response response = given()
                .contentType(ContentType.JSON)
                .get(url);
        assertEquals(404, response.getStatusCode(), "Status code check");
    }
}

package sn.ept.git.seminaire.cicd.cucumber.definitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sn.ept.git.seminaire.cicd.data.TestData;
import sn.ept.git.seminaire.cicd.entities.Tag;
import sn.ept.git.seminaire.cicd.exceptions.models.TagDTO;
import sn.ept.git.seminaire.cicd.repositories.TagRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagStepIT {

    private final static String BASE_URI = "http://localhost";
    public static final String API_PATH = "/cicd/api/tags";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    @LocalServerPort
    private int port;

    @Autowired
    private TagRepository tagRepository;

    private Response response;
    private String name;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.findAndRegisterModules();
    }

    @Before
    public void init() {
        tagRepository.deleteAll();
        this.response = null;
    }

    protected RequestSpecification request() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
        log.info("Testing on port {}", port);
        return given()
                .contentType(ContentType.JSON)
                .log().all();
    }

    // =================== GIVEN ===================
    @Given("table tag contains data:")
    public void tableTagContainsData(DataTable dataTable) {
        List<Tag> tagsList = dataTable
                .asMaps(String.class, String.class)
                .stream()
                .map(line -> Tag.builder()
                        .id(line.get(KEY_ID))
                        .name(line.get(KEY_NAME))
                        .version(0)
                        .createdDate(TestData.Default.createdDate)
                        .lastModifiedDate(TestData.Default.lastModifiedDate)
                        .build())
                .collect(Collectors.toUnmodifiableList());
        tagRepository.saveAllAndFlush(tagsList);
    }

    // =================== WHEN ===================
    @When("tag call delete all tags")
    public void tagCallDeleteAllTags() {
        response = request().when().delete(API_PATH);
        log.info("Delete all tags response: {}", response.asString());
    }

    @When("tag call find all tags with page={int}, size={int} and sort={string}")
    public void tagCallFindAllTagsWithPageSizeAndSort(Integer page, Integer size, String sort) {
        response = request()
                .when()
                .get(API_PATH + "?page=%d&size=%d&sort=%s".formatted(page, size, sort));
        log.info("Find all tags response: {}", response.asString());
    }

    @When("tag call find tag by id with id={string}")
    public void tagCallFindByIdWithId(String id) {
        response = request().when().get(API_PATH + "/" + id);
        log.info("Find tag by ID response: {}", response.asString());
    }

    @When("tag call delete tag with id={string}")
    public void tagCallDeleteWithId(String id) {
        response = request().when().delete(API_PATH + "/" + id);
        log.info("Delete tag by ID response: {}", response.asString());
    }

    @When("tag call add tag")
    public void tagCallAddTag() {
        TagDTO requestBody = TagDTO.builder().name(this.name).build();
        response = request().body(requestBody).when().post(API_PATH);
        log.info("Add tag response: {}", response.asString());
    }

    @When("tag call update tag with id={string}")
    public void tagCallUpdateTagWithIdAndName(String id) {
        TagDTO requestBody = TagDTO.builder().name(this.name).build();
        response = request().body(requestBody).when().put(API_PATH + "/" + id);
        log.info("Update tag response: {}", response.asString());
    }

    // =================== AND ===================
    @And("the following tag to add:")
    public void theFollowingTagToAdd(DataTable dataTable) {
        this.theFollowingTagToUpdate(dataTable);
    }

    @And("the following tag to update:")
    public void theFollowingTagToUpdate(DataTable dataTable) {
        Optional<Map<String, String>> optional = dataTable.asMaps(String.class, String.class).stream().findFirst();
        Assertions.assertThat(optional).isPresent();
        Map<String, String> line = optional.get();
        this.name = formatNullable(line.get(KEY_NAME));
    }

    // =================== THEN ===================
    @Then("the tag response status is {int}")
    public void theTagResponseStatusIs(Integer expectedStatus) {
        Assertions.assertThat(response)
                .as("Response should not be null")
                .isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    }

    @Then("the tag response contains {int} tags")
    public void theTagResponseContainsTags(Integer expectedCount) {
        Assertions.assertThat(response)
                .as("Response should not be null")
                .isNotNull();
        Assertions.assertThat(response.jsonPath().getList("content").size())
                .isEqualTo(expectedCount);
    }

    @Then("the returned error body tag looks like:")
    public void theReturnedErrorBodyLooksLike(DataTable dataTable) {
        Assertions.assertThat(response)
                .as("Response should not be null before checking error body")
                .isNotNull();

        Map<String, String> expected = dataTable.asMaps().get(0);

        response.then().log().all()
                .statusCode(Integer.parseInt(expected.get("status")))
                .body("system_id", Matchers.equalTo(expected.get("system_id")))
                .body("system_name", Matchers.equalTo(expected.get("system_name")))
                .body("type", Matchers.equalTo(expected.get("type")))
                .body("status", Matchers.equalTo(expected.get("status")))
                .body("message", Matchers.equalTo(expected.get("message")));
    }

    // =================== UTILS ===================
    private String formatNullable(String value) {
        if (value == null) return null;
        return "null".equalsIgnoreCase(value.trim()) ? null : value;
    }
}

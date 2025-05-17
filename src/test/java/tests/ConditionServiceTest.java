package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import service.ConditionService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 ConditionServiceTest runs ordered test cases to verify the FHIR Condition API using RestAssured.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConditionServiceTest {

    private static String conditionId;
    private static final String patientReference = "Patient/mohanad-albadri";
    private static final String diagnosisCode = "44054006";
    private static final String diagnosisDisplay = "Diabetes mellitus type 2";
    private static final String diagnosisText = "Type 2 Diabetes Mellitus";

    @Test
    @Order(1)
    @DisplayName("✅ Create Condition")
    void testCreateCondition() {
        Response response = ConditionService.createCondition(patientReference, diagnosisCode, diagnosisDisplay, diagnosisText);
        System.out.println("✅ [CREATE] Response:\n" + response.asPrettyString());

        assertEquals(201, response.getStatusCode(), "Expected status code 201 for creation");
        conditionId = response.jsonPath().getString("id");
        assertNotNull(conditionId, "Condition ID should not be null");

        System.out.println("🆔 Created Condition ID: " + conditionId);
    }

    @Test
    @Order(2)
    @DisplayName("📥 GET Condition by ID")
    void testGetCondition() {
        Response response = ConditionService.getCondition(conditionId);
        System.out.println("📥 [GET] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status code 200");
        assertEquals("Condition", response.jsonPath().getString("resourceType"), "Expected resourceType to be 'Condition'");
        assertEquals(diagnosisText, response.jsonPath().getString("code.text"), "Diagnosis text should match");
    }

    @Test
    @Order(3)
    @DisplayName("🔁 Update Condition")
    void testUpdateCondition() {
        String newText = "Updated Type 2 Diabetes";
        Response response = ConditionService.updateCondition(conditionId, patientReference, diagnosisCode, diagnosisDisplay, newText);
        System.out.println("🔁 [UPDATE] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status code 200 after update");
        assertEquals(newText, response.jsonPath().getString("code.text"), "Text should reflect the update");
    }

    @Test
    @Order(4)
    @DisplayName("🩹 Patch Condition Text")
    void testPatchCondition() {
        String patchedText = "Patched Diagnosis Text";
        Response response = ConditionService.patchCondition(conditionId, "/code/text", patchedText);
        System.out.println("🩹 [PATCH] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status code 200 after patching");
        assertEquals(patchedText, response.jsonPath().getString("code.text"), "Text should reflect the patch");
    }

    @Test
    @Order(5)
    @DisplayName("🔍 Search Conditions by Patient")
    void testSearchByPatient() {
        Response response = ConditionService.searchConditionsByPatient(patientReference);
        System.out.println("🔍 [SEARCH] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status code 200 for search");
        assertTrue(response.getBody().asString().contains("Condition"), "Response should contain 'Condition'");
    }

    @Test
    @Order(6)
    @DisplayName("🧪 Validate Condition Resource")
    void testValidateCondition() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("resourceType", "Condition");
        condition.put("subject", Map.of("reference", patientReference));
        condition.put("code", Map.of("text", diagnosisText));

        Response response = ConditionService.validateConditionResource(condition);
        System.out.println("🧪 [VALIDATE] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected validation to return 200");
    }

    @Test
    @Order(7)
    @DisplayName("📂 Post Condition from File")
    void testPostConditionFromFile() {
        String filePath = "src/test/resources/sample_condition.json";
        Response response = ConditionService.postConditionFromFile(filePath);
        System.out.println("📂 [POST-FILE] Response:\n" + response.asPrettyString());

        assertEquals(201, response.getStatusCode(), "Expected 201 Created from file input");
    }

    @Test
    @Order(8)
    @DisplayName("❌ Delete Condition")
    void testDeleteCondition() {
        Response response = ConditionService.deleteCondition(conditionId);
        System.out.println("❌ [DELETE] Response:\n" + response.asPrettyString());

        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204, "Expected 200 or 204 for deletion");
        System.out.println("🧹 Condition with ID " + conditionId + " deleted.");
    }

    @AfterAll
    static void summary() {
        System.out.println("\n✅✅ All FHIR Condition tests completed successfully.");
    }
}

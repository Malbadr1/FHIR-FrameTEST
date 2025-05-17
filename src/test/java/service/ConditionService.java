package service;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.ConfigReader;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * ✅ This class provides reusable REST API methods for Condition FHIR operations.
 *
 * Handles the following:
 * - ✅ GET condition by ID
 * - ✅ POST a new condition
 * - ✅ PUT to update condition
 * - ✅ PATCH to partially update condition
 * - ✅ DELETE condition
 * - ✅ Validate FHIR Condition resource
 * - ✅ Search condition by patient reference
 * - ✅ POST FHIR Condition from external JSON file
 */
public class ConditionService {

    // ✅ Dynamically load baseURI and basePath from config.properties
    static {
        baseURI = ConfigReader.get("base.uri");
        basePath = ConfigReader.get("base.path");
    }

    private static final String BASE_PATH = "/Condition";

    /**
     * ✅ GET a condition by ID
     */
    public static Response getCondition(String id) {
        System.out.println("📥 [GET] " + BASE_PATH + "/" + id);
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_PATH + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * ✅ POST - Create new condition resource from Map
     */
    public static Response createCondition(Map<String, Object> condition) {
        System.out.println("➕ [POST] Creating new condition");
        return given()
                .contentType(ContentType.JSON)
                .body(condition)
                .when()
                .post(BASE_PATH)
                .then()
                .extract()
                .response();
    }

    /**
     * ✅ POST - Create new condition resource from parameters
     */
    public static Response createCondition(String patientReference, String diagnosisCode, String diagnosisDisplay, String diagnosisText) {
        Map<String, Object> condition = buildConditionPayload(patientReference, diagnosisCode, diagnosisDisplay, diagnosisText);
        return createCondition(condition);
    }

    /**
     * ✅ PUT - Update entire condition resource from Map
     */
    public static Response updateCondition(String id, Map<String, Object> condition) {
        condition.put("id", id);
        System.out.println("🔁 [PUT] Updating condition ID: " + id);
        return given()
                .contentType(ContentType.JSON)
                .body(condition)
                .when()
                .put(BASE_PATH + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * ✅ PUT - Update condition from parameters
     */
    public static Response updateCondition(String conditionId, String patientReference, String diagnosisCode, String diagnosisDisplay, String newText) {
        Map<String, Object> condition = buildConditionPayload(patientReference, diagnosisCode, diagnosisDisplay, newText);
        condition.put("id", conditionId);
        return updateCondition(conditionId, condition);
    }

    /**
     * 🩹 PATCH condition using FHIR JSON Patch (application/json-patch+json)
     */
    public static Response patchCondition(String id, String path, String newValue) {
        String patchPayload = String.format("[{\"op\":\"replace\", \"path\":\"%s\", \"value\":\"%s\"}]", path, newValue);
        System.out.println("🩹 [PATCH] " + BASE_PATH + "/" + id + " with: " + patchPayload);
        return given()
                .contentType("application/json-patch+json")
                .body(patchPayload)
                .when()
                .patch(BASE_PATH + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * ✅ DELETE a condition resource by ID
     */
    public static Response deleteCondition(String id) {
        System.out.println("🗑️ [DELETE] " + BASE_PATH + "/" + id);
        return given()
                .when()
                .delete(BASE_PATH + "/" + id)
                .then()
                .extract()
                .response();
    }

    /**
     * 🧪 Validate FHIR condition resource without storing
     */
    public static Response validateConditionResource(Map<String, Object> condition) {
        System.out.println("🧪 [VALIDATE] Sending condition resource to $validate");
        return given()
                .contentType(ContentType.JSON)
                .body(condition)
                .when()
                .post(BASE_PATH + "/$validate")
                .then()
                .extract()
                .response();
    }

    /**
     * 🔍 Search for conditions by patient reference
     */
    public static Response searchConditionByPatient(String patientRef) {
        System.out.println("🔍 [SEARCH] Conditions for patient: " + patientRef);
        return given()
                .queryParam("subject", patientRef)
                .when()
                .get(BASE_PATH)
                .then()
                .extract()
                .response();
    }

    /**
     * 📂 POST FHIR Condition from external JSON file
     */
    public static Response postConditionFromFile(String filePath) {
        File file = new File(filePath);
        System.out.println("📂 [POST] Condition from file: " + filePath);
        return given()
                .contentType(ContentType.JSON)
                .body(file)
                .when()
                .post(BASE_PATH)
                .then()
                .extract()
                .response();
    }

    /**
     * 🔍 Search for all conditions related to a specific patient using their reference.
     *
     * @param patientReference the FHIR reference to the patient (e.g., "Patient/mohanad-albadri")
     * @return the API response containing a Bundle of conditions
     */
    public static Response searchConditionsByPatient(String patientReference) {
        System.out.println("🔍 [SEARCH] Fetching all conditions for patient: " + patientReference);
        return given()
                .contentType(ContentType.JSON)
                .queryParam("subject", patientReference)
                .when()
                .get(BASE_PATH)
                .then()
                .extract()
                .response();
    }

    /**
     * 🧱 Build a FHIR-compliant Condition resource payload
     */
    private static Map<String, Object> buildConditionPayload(String patientReference, String code, String display, String text) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("resourceType", "Condition");
        condition.put("subject", Map.of("reference", patientReference));

        Map<String, Object> codeMap = new HashMap<>();
        codeMap.put("text", text);
        codeMap.put("coding", Collections.singletonList(
                Map.of(
                        "system", "http://snomed.info/sct",
                        "code", code,
                        "display", display
                )
        ));

        condition.put("code", codeMap);
        return condition;
    }
}

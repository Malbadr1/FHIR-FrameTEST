package service;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.ConfigReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * ✅ This class provides reusable REST API methods for Patient FHIR operations.
 *
 * Handles the following:
 * - ✅ GET patient by ID
 * - ✅ POST a new patient with basic fields
 * - ✅ PUT to update patient resource
 * - ✅ PATCH to partially update patient (FHIR JSON Patch)
 * - ✅ DELETE patient
 * - ✅ Search by parameters (e.g., name, gender)
 * - ✅ Validate FHIR resource before sending
 * - ✅ Read historical versions of a patient
 * - ✅ Send Bundle transaction
 * - ✅ Create Patient with embedded Condition
 * - ✅ POST FHIR resource from external JSON file
 */
public class PatientService {

    private static final String BASE_PATH = "/Patient";

    static {
        // 🛠️ Initialize base URI and base path from config file
        baseURI = ConfigReader.get("base.uri");
        basePath = ConfigReader.get("base.path");
    }

    /**
     * ✅ GET a patient by ID
     */
    public static Response getPatient(String id) {
        System.out.println("📥 [GET] /Patient/" + id);
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_PATH + "/" + id)
                .then()
                .extract().response();
    }

    /**
     * ✅ POST - Create new patient resource
     */
    public static Response createPatient(String name, String gender, String birthDate) {
        Map<String, Object> patient = new HashMap<>();
        patient.put("resourceType", "Patient");
        patient.put("gender", gender);
        patient.put("birthDate", birthDate);

        Map<String, Object> nameObject = new HashMap<>();
        nameObject.put("use", "official");
        nameObject.put("text", name);
        patient.put("name", new Map[]{nameObject});

        System.out.println("➕ [POST] Creating new patient: " + name);
        return given()
                .contentType(ContentType.JSON)
                .body(patient)
                .when()
                .post(BASE_PATH)
                .then()
                .extract().response();
    }

    /**
     * ✅ PUT - Update entire patient resource
     */
    public static Response updatePatient(String id, String name, String gender, String birthDate) {
        Map<String, Object> patient = new HashMap<>();
        patient.put("resourceType", "Patient");
        patient.put("id", id);
        patient.put("gender", gender);
        patient.put("birthDate", birthDate);

        Map<String, Object> nameObject = new HashMap<>();
        nameObject.put("use", "official");
        nameObject.put("text", name);
        patient.put("name", new Map[]{nameObject});

        System.out.println("🔁 [PUT] Updating patient ID: " + id);
        return given()
                .contentType(ContentType.JSON)
                .body(patient)
                .when()
                .put(BASE_PATH + "/" + id)
                .then()
                .extract().response();
    }

    /**
     * ✅ DELETE a patient resource by ID
     */
    public static Response deletePatient(String id) {
        System.out.println("🗑️ [DELETE] /Patient/" + id);
        return given()
                .when()
                .delete(BASE_PATH + "/" + id)
                .then()
                .extract().response();
    }

    /**
     * 🔍 Search for patient by parameters (name, gender, birthDate)
     */
    public static Response searchPatientByName(String name) {
        System.out.println("🔍 [SEARCH] Patient by name: " + name);
        return given()
                .queryParam("name", name)
                .when()
                .get(BASE_PATH)
                .then()
                .extract().response();
    }

    /**
     * 🧪 Validate FHIR patient resource without storing
     */
    public static Response validatePatientResource(Map<String, Object> resource) {
        System.out.println("🧪 [VALIDATE] Sending resource to $validate");
        return given()
                .contentType(ContentType.JSON)
                .body(resource)
                .when()
                .post(BASE_PATH + "/$validate")
                .then()
                .extract().response();
    }

    /**
     * 🔄 Read specific version of patient resource
     */
    public static Response getPatientVersion(String id, String versionId) {
        System.out.println("📜 [GET Version] /Patient/" + id + "/_history/" + versionId);
        return given()
                .when()
                .get(BASE_PATH + "/" + id + "/_history/" + versionId)
                .then()
                .extract().response();
    }

    /**
     * 🩹 PATCH patient using FHIR JSON Patch (application/json-patch+json)
     */
    public static Response patchPatient(String id, String path, String newValue) {
        String patch = String.format("[{\"op\":\"replace\", \"path\":\"%s\", \"value\":\"%s\"}]", path, newValue);
        System.out.println("🩹 [PATCH] /Patient/" + id + " with: " + patch);
        return given()
                .contentType("application/json-patch+json")
                .body(patch)
                .when()
                .patch(BASE_PATH + "/" + id)
                .then()
                .extract().response();
    }

    /**
     * 📦 Send a transaction bundle to the FHIR server
     */
    public static Response sendTransactionBundle(Map<String, Object> bundle) {
        System.out.println("📦 [BUNDLE] Sending transaction bundle");
        return given()
                .contentType(ContentType.JSON)
                .body(bundle)
                .when()
                .post("/")
                .then()
                .extract().response();
    }

    /**
     * ➕ POST - Create Patient with embedded Condition using transaction Bundle
     */
    public static Response createPatientWithConditionBundle(Map<String, Object> bundle) {
        System.out.println("➕ [POST BUNDLE] Patient + Condition");
        return given()
                .contentType(ContentType.JSON)
                .body(bundle)
                .when()
                .post("/")
                .then()
                .extract().response();
    }

    /**
     * 📂 POST FHIR resource from external JSON file
     *
     * @param filePath path to the FHIR JSON file
     * @return Response from the FHIR server
     */
    public static Response postFhirResourceFromFile(String filePath) {
        System.out.println("📂 [POST] Resource from file: " + filePath);
        File file = new File(filePath);
        return given()
                .contentType(ContentType.JSON)
                .body(file)
                .when()
                .post("/")
                .then()
                .extract().response();
    }
}

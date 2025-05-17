package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import service.PatientService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 PatientServiceTest verifies the full lifecycle of FHIR Patient operations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientServiceTest {

    private static String patientId;
    private static String versionId;
    private static final String patientName = "Mohanad Al Badri";
    private static final String gender = "male";
    private static final String birthDate = "1992-01-01";

    /**
     * ✅ Create a new Patient
     */
    @Test
    @Order(1)
    @DisplayName("✅ Create Patient")
    void testCreatePatient() {
        Response response = PatientService.createPatient(patientName, gender, birthDate);
        System.out.println("✅ [CREATE] Response:\n" + response.asPrettyString());

        assertEquals(201, response.getStatusCode(), "Expected status 201 Created");
        patientId = response.jsonPath().getString("id");
        assertNotNull(patientId, "Patient ID should not be null");

        System.out.println("🆔 Created Patient ID: " + patientId);
    }

    /**
     * 📥 Get Patient by ID
     */
    @Test
    @Order(2)
    @DisplayName("📥 GET Patient by ID")
    void testGetPatient() {
        Response response = PatientService.getPatient(patientId);
        System.out.println("📥 [GET] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status 200");
        assertTrue(response.jsonPath().getString("name[0].text").contains("Mohanad"), "Patient name should contain 'Mohanad'");
        versionId = response.jsonPath().getString("meta.versionId");

        System.out.println("📜 Patient Version ID: " + versionId);
    }

    /**
     * 🔁 Update Patient
     */
    @Test
    @Order(3)
    @DisplayName("🔁 Update Patient (PUT)")
    void testUpdatePatient() {
        String updatedName = "Mohanad Updated";
        Response response = PatientService.updatePatient(patientId, updatedName, gender, birthDate);
        System.out.println("🔁 [UPDATE] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status 200 after update");
        assertEquals(updatedName, response.jsonPath().getString("name[0].text"), "Updated name should match");
    }

    /**
     * 🩹 Patch Patient Name
     */
    @Test
    @Order(4)
    @DisplayName("🩹 Patch Patient Name")
    void testPatchPatient() {
        String patchedName = "Mohanad Patched";
        Response response = PatientService.patchPatient(patientId, "/name/0/text", patchedName);
        System.out.println("🩹 [PATCH] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected status 200 for patch");
        assertEquals(patchedName, response.jsonPath().getString("name[0].text"), "Patched name should match");
    }

    /**
     * 🔍 Search Patient by Name
     */
    @Test
    @Order(5)
    @DisplayName("🔍 Search Patient by Name")
    void testSearchPatient() {
        Response response = PatientService.searchPatientByName("Mohanad");
        System.out.println("🔍 [SEARCH] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected 200 for search");
        assertTrue(response.getBody().asString().contains("Mohanad"), "Search result should contain 'Mohanad'");
    }

    /**
     * 🧪 Validate Patient Resource
     */
    @Test
    @Order(6)
    @DisplayName("🧪 Validate Patient Resource")
    void testValidatePatientResource() {
        Map<String, Object> resource = new HashMap<>();
        resource.put("resourceType", "Patient");
        resource.put("gender", gender);
        resource.put("birthDate", birthDate);
        Map<String, Object> name = new HashMap<>();
        name.put("use", "official");
        name.put("text", patientName);
        resource.put("name", new Map[]{name});

        Response response = PatientService.validatePatientResource(resource);
        System.out.println("🧪 [VALIDATE] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected validation success (200)");
    }

    /**
     * 📜 Get Patient Version
     */
    @Test
    @Order(7)
    @DisplayName("📜 Get Patient Version")
    void testGetPatientVersion() {
        Response response = PatientService.getPatientVersion(patientId, versionId);
        System.out.println("📜 [VERSION] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected 200 when retrieving version");
        assertEquals(versionId, response.jsonPath().getString("meta.versionId"), "Version ID should match");
    }

    /**
     * 📦 Send Transaction Bundle
     */
    @Test
    @Order(8)
    @DisplayName("📦 Send Transaction Bundle")
    void testSendTransactionBundle() {
        Map<String, Object> bundle = new HashMap<>();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");

        Map<String, Object> patient = new HashMap<>();
        patient.put("resourceType", "Patient");
        patient.put("id", UUID.randomUUID().toString());

        Map<String, Object> entry = new HashMap<>();
        entry.put("resource", patient);
        entry.put("request", Map.of("method", "POST", "url", "Patient"));

        bundle.put("entry", new Map[]{entry});

        Response response = PatientService.sendTransactionBundle(bundle);
        System.out.println("📦 [BUNDLE] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected 200 from transaction bundle");
    }

    /**
     * ➕ Create Patient + Condition via Bundle
     */
    @Test
    @Order(9)
    @DisplayName("➕ Create Patient + Condition via Bundle")
    void testCreatePatientWithCondition() {
        Map<String, Object> patient = new HashMap<>();
        patient.put("resourceType", "Patient");
        patient.put("id", "mohanad-albadri");
        patient.put("gender", "male");
        patient.put("birthDate", "1992-05-15");

        Map<String, Object> condition = new HashMap<>();
        condition.put("resourceType", "Condition");
        condition.put("subject", Map.of("reference", "Patient/mohanad-albadri"));
        condition.put("code", Map.of("text", "Type 2 Diabetes Mellitus"));

        Map<String, Object> entry1 = Map.of("resource", patient, "request", Map.of("method", "PUT", "url", "Patient/mohanad-albadri"));
        Map<String, Object> entry2 = Map.of("resource", condition, "request", Map.of("method", "POST", "url", "Condition"));

        Map<String, Object> bundle = Map.of(
                "resourceType", "Bundle",
                "type", "transaction",
                "entry", new Map[]{entry1, entry2}
        );

        Response response = PatientService.createPatientWithConditionBundle(bundle);
        System.out.println("➕ [PATIENT+CONDITION] Response:\n" + response.asPrettyString());

        assertEquals(200, response.getStatusCode(), "Expected 200 for transaction bundle with patient + condition");
    }

    /**
     * 📂 Post FHIR Resource From File
     */
    @Test
    @Order(10)
    @DisplayName("📂 Post FHIR Resource From File")
    void testPostFhirFromFile() {
        String path = "src/test/resources/sample_patient_condition.json";
        File file = new File(path);
        assertTrue(file.exists(), "❌ File not found: " + path);

        Response response = PatientService.postFhirResourceFromFile(path);
        System.out.println("📂 [POST FILE] Response:\n" + response.asPrettyString());

        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, "Expected 200 or 201 from file post");
    }

    /**
     * ❌ Delete Patient
     */
    @Test
    @Order(11)
    @DisplayName("❌ Delete Patient")
    void testDeletePatient() {
        Response response = PatientService.deletePatient(patientId);
        int status = response.getStatusCode();
        System.out.println("❌ [DELETE] Response:\n" + response.asPrettyString());

        assertTrue(status == 204 || status == 200, "Expected 204 (No Content) or 200 (OK), but got: " + status);
        System.out.println("🧹 Deleted Patient ID: " + patientId);
    }

    @AfterAll
    static void testSummary() {
        System.out.println("\n✅✅ All Patient API tests completed successfully.");
    }
}

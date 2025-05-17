# 🏥 FHIR API Test Automation – Java + Rest-Assured

A lightweight testing framework for FHIR REST APIs using JUnit5, Rest-Assured, and Allure, focused on validating `Patient` and `Condition` resources via the [HAPI FHIR server](https://hapi.fhir.org/).

---

## 📁 Project Structure

```
FHIR-TEST/
├── service/                   # API logic (PatientService, ConditionService)
├── tests/                     # Test classes (JUnit 5)
├── utils/                     # ConfigReader.java
├── resources/                
│   ├── config.properties      # Contains base.uri and base.path
│   └── sample_*.json          # Example FHIR payloads
└── pom.xml                    # Maven dependencies
```

---

## ⚙️ Configuration

`config.properties`:
```properties
base.uri=https://hapi.fhir.org
base.path=/baseR4
```

---

## 🚀 Running the Tests

Run all tests using Maven:

```bash
mvn clean test
```

Generate Allure Report:

```bash
mvn allure:serve
```

---

## ✅ Covered Tests

### `ConditionServiceTest.java`
- ✅ Create Condition
- 📥 GET by ID
- 🔁 PUT update
- 🩹 PATCH update
- 🔍 Search by Patient
- 🧪 Validate Resource
- 📂 Post from JSON
- ❌ Delete Resource

### `PatientServiceTest.java`
- ✅ Create Patient
- 📥 GET Patient
- 🔁 PUT update
- 🩹 PATCH name
- 🔍 Search by name
- 🧪 Validate resource
- 📜 Get version
- 📦 Send transaction bundle
- ➕ Create Patient+Condition bundle
- 📂 Post from JSON file
- ❌ Delete Patient

---


## 📌 Tips

- Logs are printed in a readable format with emoji.
- `ConfigReader` handles base URI and path.
- JSON files are used for example FHIR payloads.
- JUnit tests are ordered using `@Order(...)`.

---

## 👨‍💻 Author

Mohanad Albadri 
Austria · 2025
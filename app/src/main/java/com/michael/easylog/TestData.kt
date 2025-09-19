package com.michael.easylog

import android.os.Bundle

// Data classes for testing
data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String
)

data class Company(
    val name: String,
    val industry: String,
    val employees: Int,
    val isPublic: Boolean
)

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val isActive: Boolean,
    val address: Address,
    val company: Company?,
    val skills: List<String>,
    val certifications: Array<String>,
    val metadata: Map<String, Any>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (age != other.age) return false
        if (isActive != other.isActive) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (email != other.email) return false
        if (address != other.address) return false
        if (company != other.company) return false
        if (skills != other.skills) return false
        if (!certifications.contentEquals(other.certifications)) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + age
        result = 31 * result + isActive.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (company?.hashCode() ?: 0)
        result = 31 * result + skills.hashCode()
        result = 31 * result + certifications.contentHashCode()
        result = 31 * result + metadata.hashCode()
        return result
    }
}

data class Team(
    val name: String,
    val lead: User,
    val members: List<User>,
    val projects: List<String>,
    val budget: Double
)

enum class Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Priority,
    val assignee: User?,
    val tags: Set<String>,
    val isCompleted: Boolean
)


//fun setupEasyLog() {
//    EasyLog.setUp {
//        debugMode(true)
//        filterTag("EASYLOG_DEMO")
//        addDefaultLogger(DefaultLogger.DEFAULT_ANDROID)
//        minimumLogLevel(LogType.DEBUG)
//    }
//
//    "EasyLog Demo Started".logI("App initialization")
//}

fun runLoggingDemos() {
    demonstratePrimitiveLogging()
    demonstrateListAndArrayLogging()
    demonstrateSimpleObjectLogging()
    demonstrateNestedObjectLogging()
    demonstrateComplexScenarios()
    demonstrateErrorScenarios()
    demonstrateLogLevelFiltering()
}

fun demonstratePrimitiveLogging() {
    "=== PRIMITIVE LOGGING DEMO ===".logI()

    // Strings
    "Hello World".logD("String test")
    "".logD("Empty string")
    "Multi\nLine\nString".logV("Multiline test")

    // Numbers
    42.logD("Integer")
    3.14159.logD("Double")
    100L.logI("Long number")
    (-999).logW("Negative number")

    // Boolean
    true.logD("Boolean true")
    false.logD("Boolean false")

    // Character
    'A'.logV("Single character")

    // Null handling
    val nullString: String? = null
    nullString.logInlineNullable("Testing null value")

    // Inline logging with return
    val processedValue = "Important Data"
        .logInline("Processing")
        .uppercase()
        .logInline("After uppercase")

    processedValue.logI("Final processed value")
}

private fun demonstrateListAndArrayLogging() {
    "=== LIST AND ARRAY LOGGING DEMO ===".logI()

    // Empty collections
    emptyList<String>().logD("Empty list")
    emptyArray<Int>().logD("Empty array")

    // Small lists
    listOf("apple", "banana", "cherry").logD("Small fruit list")
    listOf(1, 2, 3, 4, 5).logI("Number list")

    // Large list (to test truncation)
    val largeList = (1..25).map { "Item $it" }
    largeList.logD("Large list with 25 items")

    // Mixed type list
    listOf("string", 42, true, null, 3.14).logV("Mixed types")

    // Arrays
    arrayOf("red", "green", "blue").logD("Color array")
    intArrayOf(10, 20, 30, 40, 50).logI("Int array")

    // Nested lists
    val nestedList = listOf(
        listOf("Group A", "Item 1", "Item 2"),
        listOf("Group B", "Item 3", "Item 4", "Item 5"),
        listOf("Group C")
    )
    nestedList.logD("Nested list structure")

    // List of numbers for calculations
    val scores = listOf(85, 92, 78, 96, 88, 91)
    scores.logI("Student scores")
    scores.average().logD("Average score")
}

private fun demonstrateSimpleObjectLogging() {
    "=== SIMPLE OBJECT LOGGING DEMO ===".logI()

    // Simple data classes
    val address = Address(
        street = "123 Main St",
        city = "New York",
        zipCode = "10001",
        country = "USA"
    )
    address.logD("User address")

    val company = Company(
        name = "Tech Innovation Inc",
        industry = "Software Development",
        employees = 150,
        isPublic = false
    )
    company.logI("Company details")

    // Enum logging
    Priority.HIGH.logD("Task priority")
    Priority.CRITICAL.logE("Critical priority level")

    // Object with null field
    val companyWithNull = Company(
        name = "Startup LLC",
        industry = "AI",
        employees = 5,
        isPublic = false
    )
    companyWithNull.logD("Small company")
}

private fun demonstrateNestedObjectLogging() {
    "=== NESTED OBJECT LOGGING DEMO ===".logI()

    val address = Address(
        street = "456 Oak Avenue",
        city = "San Francisco",
        zipCode = "94102",
        country = "USA"
    )

    val company = Company(
        name = "Future Systems",
        industry = "Artificial Intelligence",
        employees = 250,
        isPublic = true
    )

    val user = User(
        id = 1001L,
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@futuresystems.com",
        age = 32,
        isActive = true,
        address = address,
        company = company,
        skills = listOf("Kotlin", "Android", "Machine Learning", "Cloud Computing"),
        certifications = arrayOf("AWS Certified", "Google Cloud Professional", "Scrum Master"),
        metadata = mapOf(
            "joinDate" to "2022-03-15",
            "department" to "Engineering",
            "level" to "Senior",
            "projects" to listOf("Project Alpha", "Project Beta"),
            "performance" to 4.8,
            "remote" to true
        )
    )

    user.logD("Complete user profile")

    // User without company
    val freelancer = user.copy(
        id = 1002L,
        firstName = "Jane",
        lastName = "Smith",
        email = "jane.smith@freelancer.com",
        company = null,
        skills = listOf("iOS", "Swift", "UI/UX Design"),
        certifications = emptyArray(),
        metadata = mapOf(
            "type" to "Freelancer",
            "hourlyRate" to 75.0,
            "availability" to "Part-time"
        )
    )
    freelancer.logI("Freelancer profile")
}

private fun demonstrateComplexScenarios() {
    "=== COMPLEX SCENARIOS DEMO ===".logI()

    // Create multiple users
    val users = createSampleUsers()

    // Team with nested users
    val team = Team(
        name = "Mobile Development Team",
        lead = users[0],
        members = users,
        projects = listOf("EasyLog Library", "Shopping App", "Travel Planner"),
        budget = 250000.0
    )
    team.logD("Development team structure")

    // Tasks with complex relationships
    val tasks = listOf(
        Task(
            id = 1,
            title = "Implement logging framework",
            description = "Create enhanced logging with tree formatting",
            priority = Priority.HIGH,
            assignee = users[0],
            tags = setOf("logging", "framework", "android"),
            isCompleted = true
        ),
        Task(
            id = 2,
            title = "Design user interface",
            description = "Create modern UI with Material Design 3",
            priority = Priority.MEDIUM,
            assignee = users[1],
            tags = setOf("ui", "design", "material"),
            isCompleted = false
        ),
        Task(
            id = 3,
            title = "Critical bug fix",
            description = "Fix memory leak in image loading",
            priority = Priority.CRITICAL,
            assignee = null,
            tags = setOf("bug", "memory", "performance"),
            isCompleted = false
        )
    )

    tasks.logE("Project tasks overview")

    // Map of complex data
    val projectStats = mapOf(
        "totalTasks" to tasks.size,
        "completedTasks" to tasks.count { it.isCompleted },
        "teamSize" to team.members.size,
        "avgAge" to team.members.map { it.age }.average(),
        "skillDistribution" to team.members.flatMap { it.skills }.groupingBy { it }.eachCount(),
        "activeTasks" to tasks.filter { !it.isCompleted },
        "urgentTasks" to tasks.filter { it.priority == Priority.CRITICAL || it.priority == Priority.HIGH }
    )
    projectStats.logI("Project statistics")
}

private fun demonstrateErrorScenarios() {
    "=== ERROR SCENARIOS DEMO ===".logW()

    try {
        // Simulate an error
        val result = 10 / 0
        result.logD("Division result")
    } catch (e: Exception) {
        e.message?.logE("Division by zero error")
        e.stackTrace.take(3).logE("Stack trace preview")
    }

    // Network simulation
    val networkResponse = mapOf(
        "status" to "error",
        "code" to 404,
        "message" to "User not found",
        "timestamp" to System.currentTimeMillis(),
        "requestId" to "req_123456",
        "details" to mapOf(
            "userId" to 9999,
            "endpoint" to "/api/users/9999",
            "method" to "GET"
        )
    )
    networkResponse.logE("API Error Response")

    // Validation errors
    val validationErrors = listOf(
        "Email format is invalid",
        "Password must be at least 8 characters",
        "Phone number is required",
        "Age must be between 18 and 100"
    )
    validationErrors.logW("Form validation errors")
}

private fun demonstrateLogLevelFiltering() {
    "=== LOG LEVEL FILTERING DEMO ===".logI()

    "Current minimum level: ${EasyLog.getMinimumLogLevel()}".logI()

    // Log messages at different levels
    "This is a verbose message".logV("Before filtering")
    "This is a debug message".logD("Before filtering")
    "This is an info message".logI("Before filtering")
    "This is a warning message".logW("Before filtering")
    "This is an error message".logE("Before filtering")

    // Change minimum level to WARNING
    "Setting minimum log level to WARNING".logI("Filter change")
    EasyLog.setMinimumLogLevel(LogType.WARNING)

    "This verbose should be filtered".logV("After filtering")
    "This debug should be filtered".logD("After filtering")
    "This info should be filtered".logI("After filtering")
    "This warning should appear".logW("After filtering")
    "This error should appear".logE("After filtering")

    // Reset to DEBUG
    EasyLog.setMinimumLogLevel(LogType.DEBUG)
    "Log level reset to DEBUG".logI("Filter reset")
    "This debug message should appear again".logD("After reset")
}

private fun createSampleUsers(): List<User> {
    val addresses = listOf(
        Address("123 Tech Street", "San Francisco", "94105", "USA"),
        Address("456 Innovation Ave", "Seattle", "98101", "USA"),
        Address("789 Developer Blvd", "Austin", "73301", "USA")
    )

    val companies = listOf(
        Company("TechCorp", "Software", 500, true),
        Company("StartupInc", "AI/ML", 50, false),
        Company("Enterprise Solutions", "Consulting", 1000, true)
    )

    return listOf(
        User(
            id = 1L,
            firstName = "Alice",
            lastName = "Johnson",
            email = "alice@techcorp.com",
            age = 28,
            isActive = true,
            address = addresses[0],
            company = companies[0],
            skills = listOf("Kotlin", "Android", "Firebase", "MVP Architecture"),
            certifications = arrayOf("Android Developer", "Firebase Certified"),
            metadata = mapOf(
                "level" to "Senior",
                "projects" to 12,
                "rating" to 4.9
            )
        ),
        User(
            id = 2L,
            firstName = "Bob",
            lastName = "Chen",
            email = "bob@startupinc.com",
            age = 35,
            isActive = true,
            address = addresses[1],
            company = companies[1],
            skills = listOf("UI/UX", "Figma", "Design Systems", "User Research"),
            certifications = arrayOf("UX Certified", "Design Thinking"),
            metadata = mapOf(
                "level" to "Lead",
                "portfolio" to "https://bobchen.design",
                "awards" to listOf("Best Mobile Design 2023")
            )
        ),
        User(
            id = 3L,
            firstName = "Carol",
            lastName = "Davis",
            email = "carol@enterprise.com",
            age = 31,
            isActive = false,
            address = addresses[2],
            company = companies[2],
            skills = listOf("Project Management", "Agile", "Scrum", "Risk Management"),
            certifications = arrayOf("PMP", "Scrum Master", "Agile Coach"),
            metadata = mapOf(
                "level" to "Principal",
                "onLeave" to true,
                "returnDate" to "2024-02-01"
            )
        )
    )
}
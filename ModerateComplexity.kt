/**
 * Process order with various conditions including membership discounts, coupons, volume discounts, holiday premiums, and free shipping.
 */
class ModerateComplexity {
    /**
     * Validates a password based on multiple rules.
     *
     * This function checks if a given password meets several criteria: minimum
     * length of 8 characters, presence of at least one uppercase letter, one
     * lowercase letter, one digit, and one special character. It iterates through
     * each character in the password to determine if all conditions are met. If
     * any condition fails, it returns false. Otherwise, it returns true.
     *
     */
    fun validatePassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }
        
        var hasUpperCase = false
        var hasLowerCase = false
        var hasDigit = false
        var hasSpecialChar = false
        
        for (char in password) {
            when {
                char.isUpperCase() -> hasUpperCase = true
                char.isLowerCase() -> hasLowerCase = true
                char.isDigit() -> hasDigit = true
                !char.isLetterOrDigit() -> hasSpecialChar = true
            }
        }
        
        if (!hasUpperCase) return false
        if (!hasLowerCase) return false
        if (!hasDigit) return false
        if (!hasSpecialChar) return false
        
        return true
    }

    /**
     * Process order with various conditions
     * Cyclomatic Complexity: 12
     */
    fun processOrder(
        orderAmount: Double, 
        isExistingCustomer: Boolean, 
        membershipLevel: String, 
        couponCode: String?, 
        isHoliday: Boolean, 
        itemCount: Int
    ): Double {
        var finalAmount = orderAmount
        
        // Apply membership discount
        if (isExistingCustomer) {
            when (membershipLevel) {
                "gold" -> finalAmount *= 0.85
                "silver" -> finalAmount *= 0.90
                "bronze" -> finalAmount *= 0.95
                else -> finalAmount *= 0.98
            }
        }
        
        // Apply coupon if valid
        if (!couponCode.isNullOrBlank()) {
            when (couponCode) {
                "SAVE10" -> finalAmount *= 0.90
                "SAVE20" -> finalAmount *= 0.80
                "FREE" -> finalAmount = 0.0
                else -> finalAmount *= 0.95
            }
        }
        
        // Volume discount
        if (itemCount > 10) {
            finalAmount -= 5.0
        } else if (itemCount > 5) {
            finalAmount -= 2.0
        }
        
        // Holiday premium
        if (isHoliday) {
            finalAmount *= 1.1
        }
        
        // Free shipping threshold
        if (finalAmount > 100) {
            // Free shipping
        } else if (finalAmount > 50) {
            finalAmount += 5.0
        } else {
            finalAmount += 10.0
        }
        
        return if (finalAmount < 0) 0.0 else finalAmount
    }
    /**
     * Parse and validate complex input string.
     */
    fun parseInputData(input: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        
        if (input.isEmpty()) {
            throw IllegalArgumentException("Input cannot be empty")
        }
        
        val parts = input.split(";")
        if (parts.size < 3) {
            throw IllegalArgumentException("Input format invalid")
        }
        
        // Process name part
        val namePart = parts[0].split("=")
        if (namePart.size != 2 || namePart[0] != "name") {
            throw IllegalArgumentException("Name field missing or invalid")
        }
        val name = namePart[1]
        if (name.length < 2) {
            throw IllegalArgumentException("Name too short")
        }
        result["name"] = name
        
        // Process age part
        val agePart = parts[1].split("=")
        if (agePart.size != 2 || agePart[0] != "age") {
            throw IllegalArgumentException("Age field missing or invalid")
        }
        val age = agePart[1].toIntOrNull()
        if (age == null) {
            throw IllegalArgumentException("Age must be a number")
        }
        if (age < 0 || age > 150) {
            throw IllegalArgumentException("Age out of valid range")
        }
        result["age"] = age
        
        // Process data part
        val dataPart = parts[2].split("=")
        if (dataPart.size != 2 || dataPart[0] != "data") {
            throw IllegalArgumentException("Data field missing or invalid")
        }
        
        // Parse data JSON-like format
        val dataString = dataPart[1].trim()
        if (!dataString.startsWith("{") || !dataString.endsWith("}")) {
            throw IllegalArgumentException("Data must be in JSON-like format")
        }
        
        val innerData = mutableMapOf<String, String>()
        val keyValuePairs = dataString.substring(1, dataString.length - 1).split(",")
        
        for (pair in keyValuePairs) {
            val keyValue = pair.split(":")
            if (keyValue.size != 2) {
                throw IllegalArgumentException("Invalid key-value format in data")
            }
            innerData[keyValue[0].trim()] = keyValue[1].trim()
        }
        
        result["data"] = innerData
        return result
    }
}

/**
 * Class to handle authentication and authorization
 * Methods with moderate cyclomatic complexity
 */
class AuthenticationManager {
    private val users = mutableMapOf<String, UserInfo>()
    private val roles = mapOf(
        "admin" to listOf("read", "write", "delete", "manage"),
        "editor" to listOf("read", "write"),
        "viewer" to listOf("read")
    )
    
    data class UserInfo(val passwordHash: String, val role: String, val isActive: Boolean, val lastLogin: Long)
    /**
     * Authenticate a user with validation and logging.
     *
     * This function authenticates a user by validating credentials, checking
     * account status, and handling login attempts. It logs failed attempts for
     * various reasons such as empty credentials, unknown users, inactive
     * accounts, expired accounts, and invalid passwords. If successful, it
     * updates the last login time and logs the successful login.
     *
     */
    fun authenticateUser(username: String, password: String, ipAddress: String, device: String): Boolean {
        val currentTime = System.currentTimeMillis()
        
        if (username.isBlank() || password.isBlank()) {
            logFailedAttempt(username, ipAddress, "Empty credentials")
            return false
        }
        
        val userInfo = users[username] ?: run {
            logFailedAttempt(username, ipAddress, "Unknown user")
            return false
        }
        
        if (!userInfo.isActive) {
            logFailedAttempt(username, ipAddress, "Inactive account")
            return false
        }
        
        if (userInfo.lastLogin > 0 && currentTime - userInfo.lastLogin > 30L * 24 * 60 * 60 * 1000) {
            logFailedAttempt(username, ipAddress, "Account expired")
            return false
        }
        
        val calculatedHash = hashPassword(password)
        if (calculatedHash != userInfo.passwordHash) {
            logFailedAttempt(username, ipAddress, "Invalid password")
            return false
        }
        
        users[username] = userInfo.copy(lastLogin = currentTime)
        logSuccessfulLogin(username, ipAddress, device)
        return true
    }
    
    /**
     * Log a failed login attempt with the username, IP address, and reason.
     */
    private fun logFailedAttempt(username: String, ipAddress: String, reason: String) {
        println("Failed login: $username from $ipAddress - $reason")
    }
    
    /**
     * Logs a successful login event with user details.
     */
    private fun logSuccessfulLogin(username: String, ipAddress: String, device: String) {
        println("Successful login: $username from $ipAddress using $device")
    }
    
    /**
     * Reverses the input password string as a mock implementation.
     */
    private fun hashPassword(password: String): String {
        // Simple mock implementation - in real code would use proper hashing
        return password.reversed()
    }
    /**
     * Check if user has permission for action on resource.
     */
    fun hasPermission(username: String, action: String, resource: String, context: Map<String, String>): Boolean {
        val userInfo = users[username] ?: return false
        
        if (!userInfo.isActive) {
            return false
        }
        
        // Special case for system resources
        if (resource.startsWith("system.") && userInfo.role != "admin") {
            return false
        }
        
        // Get permissions for user role
        val permissions = roles[userInfo.role] ?: return false
        
        // Check if action is allowed for role
        if (action !in permissions) {
            return false
        }
        
        // Resource owner can do anything
        if (context["owner"] == username) {
            return true
        }
        
        // Public resources are readable by everyone
        if (resource.startsWith("public.") && action == "read") {
            return true
        }
        
        // Check project access
        if (resource.startsWith("project.")) {
            val projectId = resource.substringAfter("project.")
            if (context["projectRole"] == "manager" && (action == "read" || action == "write")) {
                return true
            }
            if (context["projectRole"] == "member" && action == "read") {
                return true
            }
        }
        
        // Department resources
        if (resource.startsWith("department.")) {
            val department = resource.substringAfter("department.")
            if (context["department"] == department) {
                return action == "read" || (action == "write" && userInfo.role != "viewer")
            }
        }
        
        return false
    }
    /**
     * Register a new user with validation.
     */
    fun registerUser(username: String, password: String, email: String, role: String): Boolean {
        // Check if username already exists
        if (username in users) {
            return false
        }
        
        // Validate username
        if (username.length < 3 || username.length > 20) {
            return false
        }
        
        if (!username.all { it.isLetterOrDigit() || it == '_' || it == '.' }) {
            return false
        }
        
        // Validate password
        if (password.length < 8) {
            return false
        }
        
        var passwordScore = 0
        if (password.any { it.isUpperCase() }) passwordScore++
        if (password.any { it.isLowerCase() }) passwordScore++
        if (password.any { it.isDigit() }) passwordScore++
        if (password.any { !it.isLetterOrDigit() }) passwordScore++
        
        if (passwordScore < 3) {
            return false
        }
        
        // Validate email
        if (!email.contains("@") || !email.contains(".") || email.length < 5) {
            return false
        }
        
        // Validate role
        if (role !in roles.keys) {
            return false
        }
        
        // Add the user
        users[username] = UserInfo(
            passwordHash = hashPassword(password),
            role = role,
            isActive = true,
            lastLogin = 0
        )
        
        return true
    }
}
/**
 * Process text with various transformations based on complex rules.
 */
class TextProcessor {
    /**
     * Analyze text sentiment and categories.
     */
    fun analyzeText(text: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        
        if (text.isEmpty()) {
            result["error"] = "Empty text"
            return result
        }
        
        // Word count and statistics
        val words = text.split(Regex("\\s+")).filter { it.isNotEmpty() }
        result["wordCount"] = words.size
        
        val wordLengths = words.map { it.length }
        result["avgWordLength"] = if (words.isNotEmpty()) wordLengths.average() else 0.0
        
        // Sentiment analysis (simplified)
        val positiveWords = setOf("good", "great", "excellent", "happy", "nice", "love", "best")
        val negativeWords = setOf("bad", "awful", "terrible", "sad", "hate", "worst")
        
        val positiveCount = words.count { it.lowercase() in positiveWords }
        val negativeCount = words.count { it.lowercase() in negativeWords }
        
        val sentiment = when {
            positiveCount > negativeCount * 2 -> "very positive"
            positiveCount > negativeCount -> "positive"
            negativeCount > positiveCount * 2 -> "very negative"
            negativeCount > positiveCount -> "negative"
            positiveCount > 0 || negativeCount > 0 -> "mixed"
            else -> "neutral"
        }
        result["sentiment"] = sentiment
        
        // Category detection
        val categories = mutableSetOf<String>()
        if (Regex("\\b(stock|market|investor|dividend|nasdaq|dow|nyse)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            categories.add("finance")
        }
        if (Regex("\\b(team|score|win|lose|player|game|match)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            categories.add("sports")
        }
        if (Regex("\\b(code|program|developer|software|algorithm|function|class)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            categories.add("technology")
        }
        if (Regex("\\b(health|doctor|patient|hospital|medicine|disease|treatment)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            categories.add("healthcare")
        }
        if (Regex("\\b(president|government|election|political|senator|congress|vote)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            categories.add("politics")
        }
        
        result["categories"] = categories
        
        // Complexity metrics
        val sentences = text.split(Regex("[.!?]")).filter { it.isNotEmpty() }
        result["sentenceCount"] = sentences.size
        
        val avgWordsPerSentence = if (sentences.isNotEmpty()) {
            words.size.toDouble() / sentences.size
        } else 0.0
        result["avgWordsPerSentence"] = avgWordsPerSentence
        
        val complexWords = words.count { word -> 
            word.length > 6 && word.count { it.isLetter() && it.isLowerCase() } > 2
        }
        result["readingLevel"] = when {
            avgWordsPerSentence > 20 && complexWords.toDouble() / words.size > 0.2 -> "advanced"
            avgWordsPerSentence > 15 || complexWords.toDouble() / words.size > 0.1 -> "intermediate"
            else -> "basic"
        }
        
        return result
    }
    /**
     * Parse and extract structured data from text.
     */
    fun extractStructuredData(text: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        
        // Extract emails
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val emails = emailRegex.findAll(text).map { it.value }.toList()
        if (emails.isNotEmpty()) {
            result["emails"] = emails
        }
        
        // Extract phone numbers (simplified patterns)
        val phoneRegex = Regex("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}")
        val phones = phoneRegex.findAll(text).map { it.value }.toList()
        if (phones.isNotEmpty()) {
            result["phones"] = phones
        }
        
        // Extract URLs
        val urlRegex = Regex("https?://[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#[\\]@!$&'()*+,;=]+")
        val urls = urlRegex.findAll(text).map { it.value }.toList()
        if (urls.isNotEmpty()) {
            result["urls"] = urls
        }
        
        // Extract dates (various formats)
        val datePatterns = listOf(
            Regex("\\d{1,2}/\\d{1,2}/\\d{2,4}"),
            Regex("\\d{1,2}-\\d{1,2}-\\d{2,4}"),
            Regex("\\d{4}-\\d{1,2}-\\d{1,2}")
        )
        
        val dates = mutableListOf<String>()
        for (pattern in datePatterns) {
            dates.addAll(pattern.findAll(text).map { it.value }.toList())
        }
        
        if (dates.isNotEmpty()) {
            result["dates"] = dates
        }
        
        // Extract monetary values
        val moneyRegex = Regex("\\$\\d+(?:\\.\\d{2})?")
        val moneyValues = moneyRegex.findAll(text).map { it.value }.toList()
        if (moneyValues.isNotEmpty()) {
            result["monetaryValues"] = moneyValues
        }
        
        // Extract percentages
        val percentRegex = Regex("\\d+(?:\\.\\d+)?%")
        val percentages = percentRegex.findAll(text).map { it.value }.toList()
        if (percentages.isNotEmpty()) {
            result["percentages"] = percentages
        }
        
        // Extract hashtags
        val hashtagRegex = Regex("#[\\w\\d]+")
        val hashtags = hashtagRegex.findAll(text).map { it.value }.toList()
        if (hashtags.isNotEmpty()) {
            result["hashtags"] = hashtags
        }
        
        // Extract mentions
        val mentionRegex = Regex("@[\\w\\d]+")
        val mentions = mentionRegex.findAll(text).map { it.value }.toList()
        if (mentions.isNotEmpty()) {
            result["mentions"] = mentions
        }
        
        // Extract IP addresses
        val ipRegex = Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")
        val ips = ipRegex.findAll(text).map { it.value }.filter { ip ->
            ip.split(".").all { it.toInt() in 0..255 }
        }.toList()
        if (ips.isNotEmpty()) {
            result["ipAddresses"] = ips
        }
        
        // Extract key-value pairs
        val keyValueRegex = Regex("(\\w+)\\s*:\\s*([\\w\\s]+)")
        val keyValues = keyValueRegex.findAll(text).associate { 
            it.groupValues[1] to it.groupValues[2].trim() 
        }
        if (keyValues.isNotEmpty()) {
            result["keyValuePairs"] = keyValues
        }
        
        return result
    }
    
    /**
     * Process text with various transformations based on complex rules
     * Cyclomatic Complexity: 14
     */
    fun transformText(
        text: String, 
        capitalize: Boolean = false, 
        formatNames: Boolean = false,
        redactEmails: Boolean = false,
        summarize: Boolean = false,
        maxLength: Int = -1,
        replaceAbbreviations: Boolean = false,
        highlight: List<String> = emptyList(),
        removeUrls: Boolean = false,
        normalizePunctuation: Boolean = false
    ): String {
        if (text.isEmpty()) {
            return text
        }
        
        var result = text
        
        // Capitalize sentences if requested
        if (capitalize) {
            val sentenceRegex = Regex("(^|[.!?]\\s+)([a-z])")
            result = sentenceRegex.replace(result) { matchResult ->
                matchResult.groupValues[1] + matchResult.groupValues[2].uppercase()
            }
        }
        
        // Format names (simple name detection - words that start with capitals)
        if (formatNames) {
            val nameRegex = Regex("\\b[A-Z][a-z]+ [A-Z][a-z]+\\b")
            result = nameRegex.replace(result) { matchResult ->
                "**${matchResult.value}**"
            }
        }
        
        // Redact emails for privacy
        if (redactEmails) {
            val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            result = emailRegex.replace(result) { "[EMAIL REDACTED]" }
        }
        
        // Remove URLs
        if (removeUrls) {
            val urlRegex = Regex("https?://[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#[\\]@!$&'()*+,;=]+")
            result = urlRegex.replace(result) { "[URL REMOVED]" }
        }
        
        // Replace common abbreviations
        if (replaceAbbreviations) {
            val abbreviations = mapOf(
                "\\bdr\\.\\s" to "Doctor ",
                "\\bmr\\.\\s" to "Mister ",
                "\\bmrs\\.\\s" to "Misses ",
                "\\bprof\\.\\s" to "Professor ",
                "\\bdept\\.\\s" to "Department ",
                "\\bappt\\.\\s" to "Appointment ",
                "\\bapprox\\.\\s" to "Approximately ",
                "\\be\\.g\\.\\s" to "for example ",
                "\\bi\\.e\\.\\s" to "that is ",
                "\\betc\\.\\s" to "etcetera "
            )
            
            var tempResult = result
            for ((abbr, full) in abbreviations) {
                tempResult = Regex(abbr, RegexOption.IGNORE_CASE).replace(tempResult, full)
            }
            result = tempResult
        }
        
        // Highlight specific terms
        if (highlight.isNotEmpty()) {
            for (term in highlight) {
                val escapedTerm = Regex.escape(term)
                val highlightRegex = Regex("\\b$escapedTerm\\b", RegexOption.IGNORE_CASE)
                result = highlightRegex.replace(result) { "**${it.value}**" }
            }
        }
        
        // Normalize punctuation (fix common issues)
        if (normalizePunctuation) {
            // Fix multiple spaces
            result = result.replace(Regex("\\s{2,}"), " ")
            
            // Fix spacing around punctuation
            result = result.replace(Regex("\\s+([.,;:!?])"), "$1")
            
            // Fix quotes
            result = result.replace(Regex("\"(.*?)\"")) { "\"${it.groupValues[1]}\"" }
            
            // Fix ellipsis
            result = result.replace(Regex("\\.{2,}"), "...")
        }
        
        // Create a simple summary if requested
        if (summarize) {
            val sentences = result.split(Regex("[.!?]\\s+")).filter { it.isNotEmpty() }
            if (sentences.size > 3) {
                val firstSentence = sentences.first()
                val lastSentence = sentences.last()
                var middleSentence = ""
                
                if (sentences.size > 2) {
                    // Try to pick a sentence with important keywords
                    val keywordMatch = sentences.drop(1).dropLast(1).firstOrNull { sentence ->
                        val keywords = listOf("important", "significant", "key", "essential", "critical", "main")
                        keywords.any { keyword -> keyword in sentence.lowercase() }
                    }
                    
                    middleSentence = keywordMatch ?: sentences[sentences.size / 2]
                }
                
                result = if (middleSentence.isNotEmpty()) {
                    "$firstSentence. $middleSentence. $lastSentence."
                } else {
                    "$firstSentence. $lastSentence."
                }
            }
        }
        
        // Truncate to max length if specified
        if (maxLength > 0 && result.length > maxLength) {
            // Try to cut at a sentence boundary
            val lastSentenceBoundary = result.substring(0, maxLength).lastIndexOfAny(charArrayOf('.', '!', '?'))
            result = if (lastSentenceBoundary > 0) {
                result.substring(0, lastSentenceBoundary + 1)
            } else {
                // If no sentence boundary, cut at word boundary
                val lastWordBoundary = result.substring(0, maxLength).lastIndexOf(' ')
                if (lastWordBoundary > 0) {
                    result.substring(0, lastWordBoundary) + "..."
                } else {
                    result.substring(0, maxLength) + "..."
                }
            }
        }
        
        return result
    }
}

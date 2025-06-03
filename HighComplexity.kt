package com.github.test

/**
 * Class containing functions with high cyclomatic complexity (>15)
 * These functions demonstrate complex decision paths and nested logic.
 */
class HighComplexity {

    /**
     * Validates a complex user input with multiple rules and validations
     * Cyclomatic Complexity: ~25
     * 
     * @param input The user input to validate
     * @param userType Type of user making the request
     * @param context Additional context for validation
     * @return ValidationResult containing status and error messages
     */
    fun validateComplexInput(input: String, userType: String, context: Map<String, Any>): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Check basic empty conditions
        if (input.isEmpty()) {
            return ValidationResult(false, listOf("Input cannot be empty"))
        }
        
        // Check input format
        if (!input.contains(":") || !input.contains("|")) {
            errors.add("Input must contain both ':' and '|' characters")
        }
        
        // Split input into sections
        val sections = input.split("|")
        if (sections.size < 3) {
            errors.add("Input must have at least 3 sections separated by '|'")
            return ValidationResult(false, errors)
        }
        
        // Process metadata section
        val metadataParts = sections[0].split(":")
        if (metadataParts.size != 2) {
            errors.add("Metadata section must have format 'key:value'")
        } else {
            val metadataKey = metadataParts[0].trim()
            val metadataValue = metadataParts[1].trim()
            
            // Validate metadata key
            if (metadataKey.length < 2 || !metadataKey.all { it.isLetterOrDigit() || it == '_' }) {
                errors.add("Invalid metadata key format")
            }
            
            // Validate metadata value by user type
            when (userType) {
                "admin" -> {
                    if (metadataValue.length > 50) {
                        errors.add("Admin metadata value too long")
                    }
                }
                "manager" -> {
                    if (metadataValue.isEmpty() || metadataValue.length > 30) {
                        errors.add("Manager metadata must be 1-30 characters")
                    }
                }
                "user" -> {
                    if (!metadataValue.startsWith("USR_") || metadataValue.length < 8) {
                        errors.add("User metadata must start with 'USR_' and be at least 8 chars")
                    }
                }
                else -> errors.add("Unknown user type")
            }
        }
        
        // Process payload section
        val payloadData = sections[1].trim()
        if (payloadData.isEmpty()) {
            errors.add("Payload cannot be empty")
        } else {
            try {
                // Check if JSON-like format with braces
                if (payloadData.startsWith("{") && payloadData.endsWith("}")) {
                    val payload = payloadData.substring(1, payloadData.length - 1)
                    val fields = payload.split(",")
                    
                    if (fields.isEmpty()) {
                        errors.add("Payload must contain at least one field")
                    }
                    
                    for (field in fields) {
                        if (!field.contains(":")) {
                            errors.add("Each payload field must use 'key:value' format")
                            continue
                        }
                        
                        val (key, value) = field.split(":", limit = 2)
                        
                        // Validate specific fields based on user type and context
                        when (key.trim()) {
                            "id" -> {
                                if (!value.trim().matches(Regex("\\d{4,10}"))) {
                                    errors.add("ID must be 4-10 digits")
                                }
                            }
                            "name" -> {
                                if (value.trim().length < 2 || value.contains(";")) {
                                    errors.add("Name must be at least 2 chars without semicolons")
                                }
                            }
                            "date" -> {
                                if (!value.trim().matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                    errors.add("Date must be in YYYY-MM-DD format")
                                }
                            }
                            "category" -> {
                                val allowedCategories = context["allowedCategories"] as? List<String> ?: listOf()
                                if (value.trim() !in allowedCategories) {
                                    errors.add("Invalid category")
                                }
                            }
                        }
                    }
                } else {
                    errors.add("Payload must be in {key1:value1,key2:value2} format")
                }
            } catch (e: Exception) {
                errors.add("Invalid payload format: ${e.message}")
            }
        }
        
        // Process flags section
        val flags = sections[2].trim().split(",")
        if (flags.isEmpty()) {
            errors.add("At least one flag required")
        } else {
            // Check allowed flags based on user type
            val allowedFlags = when (userType) {
                "admin" -> setOf("DEBUG", "VERBOSE", "TRACE", "SENSITIVE", "PRIVILEGED")
                "manager" -> setOf("DEBUG", "VERBOSE", "TRACE")
                "user" -> setOf("DEBUG")
                else -> emptySet()
            }
            
            // Maximum flags allowed by user type
            val maxFlags = when (userType) {
                "admin" -> 5
                "manager" -> 3
                "user" -> 1
                else -> 0
            }
            
            if (flags.size > maxFlags) {
                errors.add("Too many flags for $userType (max: $maxFlags)")
            }
            
            for (flag in flags) {
                val trimmedFlag = flag.trim().uppercase()
                if (trimmedFlag !in allowedFlags) {
                    errors.add("Flag '$trimmedFlag' not allowed for $userType")
                }
                
                // Special checks for certain flags
                if (trimmedFlag == "SENSITIVE" && context["securityLevel"] != "high") {
                    errors.add("SENSITIVE flag requires high security level")
                }
                
                if (trimmedFlag == "TRACE" && context["environment"] == "production") {
                    errors.add("TRACE flag not allowed in production")
                }
            }
        }
        
        // Optional section validation
        if (sections.size > 3) {
            val timestamp = sections[3].trim()
            if (timestamp.isNotEmpty()) {
                try {
                    val timestampValue = timestamp.toLong()
                    val currentTime = System.currentTimeMillis()
                    
                    // Timestamp validations
                    if (timestampValue > currentTime) {
                        errors.add("Timestamp cannot be in the future")
                    }
                    
                    if (currentTime - timestampValue > 86400000) { // 24 hours in milliseconds
                        errors.add("Timestamp too old (> 24 hours)")
                    }
                } catch (e: NumberFormatException) {
                    errors.add("Invalid timestamp format")
                }
            }
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    /**
     * Processes complex business rules for insurance risk calculation
     * Cyclomatic Complexity: ~20
     * 
     * @param age Age of applicant
     * @param healthScore Health score from 0-100
     * @param hasPreExistingConditions Whether applicant has pre-existing conditions
     * @param smokingStatus Smoking status of applicant
     * @param familyHistory Map of family medical history
     * @param region Geographic region of applicant
     * @param occupation Occupation of applicant
     * @param activityLevel Activity level from 0-10
     * @return RiskAssessment containing risk score and categorization
     */
    fun calculateInsuranceRisk(
        age: Int,
        healthScore: Int,
        hasPreExistingConditions: Boolean,
        smokingStatus: String,
        familyHistory: Map<String, Boolean>,
        region: String,
        occupation: String,
        activityLevel: Int
    ): RiskAssessment {
        var riskScore = 50 // Start with neutral risk
        var riskFactors = mutableListOf<String>()
        
        // Age factor
        when {
            age < 18 -> {
                riskScore += 10
                riskFactors.add("minor")
            }
            age <= 30 -> riskScore -= 15
            age <= 45 -> riskScore -= 5
            age <= 60 -> riskScore += 10
            age <= 75 -> {
                riskScore += 25
                riskFactors.add("senior")
            }
            else -> {
                riskScore += 40
                riskFactors.add("elderly")
            }
        }
        
        // Health score factor
        when {
            healthScore >= 90 -> riskScore -= 20
            healthScore >= 75 -> riskScore -= 10
            healthScore >= 50 -> {} // Neutral
            healthScore >= 25 -> riskScore += 15
            else -> {
                riskScore += 30
                riskFactors.add("poor health score")
            }
        }
        
        // Pre-existing conditions
        if (hasPreExistingConditions) {
            riskScore += 25
            riskFactors.add("pre-existing conditions")
        }
        
        // Smoking status
        when (smokingStatus.lowercase()) {
            "never" -> riskScore -= 10
            "former" -> {
                riskScore += 5
                riskFactors.add("former smoker")
            }
            "occasional" -> {
                riskScore += 15
                riskFactors.add("occasional smoker")
            }
            "regular" -> {
                riskScore += 30
                riskFactors.add("regular smoker")
            }
            "heavy" -> {
                riskScore += 50
                riskFactors.add("heavy smoker")
            }
            else -> riskScore += 10 // Unknown status
        }
        
        // Family history
        val criticalConditions = setOf("cancer", "heart disease", "diabetes")
        var familyRiskCount = 0
        
        for ((condition, present) in familyHistory) {
            if (present && condition.lowercase() in criticalConditions) {
                familyRiskCount++
                riskFactors.add("family history of $condition")
            }
        }
        
        when {
            familyRiskCount >= 3 -> riskScore += 30
            familyRiskCount == 2 -> riskScore += 20
            familyRiskCount == 1 -> riskScore += 10
        }
        
        // Region risk factors
        val highRiskRegions = setOf("coastal", "flood zone", "tornado alley")
        val moderateRiskRegions = setOf("urban", "mountain", "desert")
        val lowRiskRegions = setOf("suburban", "rural")
        
        when {
            region.lowercase() in highRiskRegions -> {
                riskScore += 15
                riskFactors.add("high-risk region")
            }
            region.lowercase() in moderateRiskRegions -> riskScore += 5
            region.lowercase() in lowRiskRegions -> riskScore -= 5
        }
        
        // Occupation risk
        val highRiskJobs = setOf("miner", "firefighter", "stuntperson", "police", "military", "logger")
        val moderateRiskJobs = setOf("construction", "driver", "chef", "nurse", "electrician")
        val lowRiskJobs = setOf("office worker", "teacher", "programmer", "artist", "accountant")
        
        when {
            occupation.lowercase() in highRiskJobs -> {
                riskScore += 20
                riskFactors.add("high-risk occupation")
            }
            occupation.lowercase() in moderateRiskJobs -> riskScore += 8
            occupation.lowercase() in lowRiskJobs -> riskScore -= 8
        }
        
        // Activity level
        when {
            activityLevel >= 8 -> {
                // Very active might be both good for health but could indicate dangerous sports
                if (occupation in highRiskJobs || region in highRiskRegions) {
                    riskScore += 10
                    riskFactors.add("high activity in risky context")
                } else {
                    riskScore -= 15
                    riskFactors.remove("poor health score") // Override if present
                }
            }
            activityLevel >= 5 -> riskScore -= 10
            activityLevel >= 3 -> riskScore -= 5
            activityLevel >= 1 -> {} // Neutral
            else -> {
                riskScore += 15
                riskFactors.add("sedentary lifestyle")
            }
        }
        
        // Combined factors create additional risks
        if (age > 60 && activityLevel < 3) {
            riskScore += 10
            riskFactors.add("inactive senior")
        }
        
        if (smokingStatus.lowercase() != "never" && hasPreExistingConditions) {
            riskScore += 15
            riskFactors.add("smoking with pre-existing conditions")
        }
        
        if (familyRiskCount > 0 && healthScore < 50) {
            riskScore += 10
            riskFactors.add("poor health with family risk factors")
        }
        
        // Cap risk score between 0 and 100
        riskScore = riskScore.coerceIn(0, 100)
        
        // Assign risk category
        val riskCategory = when {
            riskScore <= 20 -> "Minimal Risk"
            riskScore <= 40 -> "Low Risk"
            riskScore <= 60 -> "Moderate Risk"
            riskScore <= 80 -> "High Risk"
            else -> "Severe Risk"
        }
        
        // Determine if eligible for standard insurance
        val eligible = when {
            riskScore > 85 -> false
            riskScore > 70 && riskFactors.size > 3 -> false
            hasPreExistingConditions && age > 60 && smokingStatus.lowercase() != "never" -> false
            else -> true
        }
        
        return RiskAssessment(riskScore, riskCategory, riskFactors, eligible)
    }
    
    /**
     * Evaluates a complex investment portfolio strategy with multiple decision points
     * Cyclomatic Complexity: ~30
     * 
     * @param portfolio The current investment portfolio 
     * @param marketConditions Current market indicators
     * @param investorProfile Investor's risk tolerance and goals
     * @param economicData Economic indicators and forecasts
     * @return Investment recommendations and portfolio adjustments
     */
    fun evaluateInvestmentStrategy(
        portfolio: Map<String, Double>,
        marketConditions: MarketConditions,
        investorProfile: InvestorProfile,
        economicData: Map<String, Double>
    ): InvestmentRecommendation {
        val recommendations = mutableListOf<String>()
        val adjustments = mutableMapOf<String, Double>()
        
        // Calculate current allocation ratios
        val totalValue = portfolio.values.sum()
        val currentAllocation = mutableMapOf<String, Double>()
        portfolio.forEach { (asset, value) -> 
            currentAllocation[asset] = (value / totalValue) * 100.0
        }
        
        // Determine risk level and recommended allocations
        val baseStockAllocation = when (investorProfile.riskTolerance) {
            "conservative" -> 30.0
            "moderate" -> 60.0
            "aggressive" -> 80.0
            "very aggressive" -> 95.0
            else -> 50.0 // moderate default
        }
        
        // Adjust stock allocation based on age if retirement is a goal
        var targetStockAllocation = baseStockAllocation
        if (investorProfile.goals.contains("retirement")) {
            val yearsToRetirement = max(investorProfile.targetRetirementAge - investorProfile.age, 0)
            when {
                yearsToRetirement > 30 -> targetStockAllocation += 10
                yearsToRetirement > 20 -> targetStockAllocation += 5
                yearsToRetirement > 10 -> targetStockAllocation += 0
                yearsToRetirement > 5 -> targetStockAllocation -= 10
                else -> targetStockAllocation -= 20
            }
            
            // Cap the allocation
            targetStockAllocation = targetStockAllocation.coerceIn(20.0, 95.0)
        }
        
        // Adjust for market conditions
        when {
            marketConditions.trend == "strongly bullish" -> {
                if (investorProfile.riskTolerance != "conservative") targetStockAllocation += 5
                recommendations.add("Market shows strong upward trend, slight increase in equity exposure")
            }
            marketConditions.trend == "bullish" -> {
                if (investorProfile.riskTolerance != "conservative") targetStockAllocation += 2
                recommendations.add("Positive market trend supports current equity allocation")
            }
            marketConditions.trend == "bearish" -> {
                targetStockAllocation -= 5
                recommendations.add("Negative market trend suggests reducing equity exposure")
            }
            marketConditions.trend == "strongly bearish" -> {
                targetStockAllocation -= 10
                recommendations.add("Strong negative trend indicates defensive positioning")
            }
        }
        
        // Adjust for market volatility
        when {
            marketConditions.volatility > 25 -> {
                targetStockAllocation -= 8
                recommendations.add("High volatility suggests reducing risk exposure")
            }
            marketConditions.volatility > 15 -> {
                targetStockAllocation -= 3
                recommendations.add("Elevated volatility suggests caution")
            }
            marketConditions.volatility < 10 && investorProfile.riskTolerance != "conservative" -> {
                targetStockAllocation += 3
                recommendations.add("Low volatility environment favorable for equity exposure")
            }
        }
        
        // Economic indicators influence
        if (economicData.containsKey("inflation")) {
            when {
                economicData["inflation"]!! > 5.0 -> {
                    recommendations.add("High inflation environment - consider inflation protected securities")
                    adjustments["TIPS"] = 10.0
                    adjustments["commodities"] = 5.0
                }
                economicData["inflation"]!! > 3.0 -> {
                    recommendations.add("Elevated inflation - maintain inflation hedges")
                    adjustments["TIPS"] = 5.0
                }
            }
        }
        
        if (economicData.containsKey("gdpGrowth")) {
            when {
                economicData["gdpGrowth"]!! > 3.0 -> {
                    if (investorProfile.riskTolerance != "conservative") {
                        recommendations.add("Strong economic growth supports cyclical sectors")
                        adjustments["cyclicals"] = 5.0
                    }
                }
                economicData["gdpGrowth"]!! < 1.0 -> {
                    recommendations.add("Weak economic growth suggests defensive positioning")
                    adjustments["defensives"] = 5.0
                    adjustments["utilities"] = 3.0
                }
                economicData["gdpGrowth"]!! < -1.0 -> {
                    recommendations.add("Economic contraction - move to defensive assets")
                    adjustments["treasuries"] = 10.0
                    adjustments["gold"] = 5.0
                    targetStockAllocation -= 10
                }
            }
        }
        
        if (economicData.containsKey("interestRate")) {
            when {
                economicData["interestRate"]!! > 5.0 -> {
                    recommendations.add("High interest rates - reduce duration in fixed income")
                    adjustments["shortTerm"] = 10.0
                    adjustments["longTerm"] = -10.0
                }
                economicData["interestRate"]!! < 1.0 -> {
                    recommendations.add("Low interest rates - consider dividend stocks")
                    adjustments["dividendStocks"] = 5.0
                    
                    if (marketConditions.trend != "bearish" && 
                        marketConditions.trend != "strongly bearish") {
                        recommendations.add("Low rates support growth stocks in positive market")
                        adjustments["growthStocks"] = 5.0
                    }
                }
            }
        }
        
        if (economicData.containsKey("unemploymentRate") && economicData["unemploymentRate"]!! > 6.0) {
            recommendations.add("Elevated unemployment suggests economic weakness")
            adjustments["consumerDiscretionary"] = -5.0
            adjustments["consumerStaples"] = 5.0
        }
        
        // Special cash allocation based on multiple factors
        var targetCashAllocation = 5.0
        
        // Increase cash in bad conditions
        if (marketConditions.trend == "strongly bearish" && marketConditions.volatility > 20) {
            targetCashAllocation = 15.0
            recommendations.add("High market risk suggests increased cash position")
        }
        
        // Adjust for emergency needs
        if (investorProfile.goals.contains("emergency fund") && 
            (!currentAllocation.containsKey("cash") || currentAllocation["cash"]!! < 15)) {
            targetCashAllocation = max(targetCashAllocation, 15.0)
            recommendations.add("Building emergency fund requires minimum 15% cash allocation")
        }
        
        // Adjust cash for upcoming financial goals
        if (investorProfile.shortTermNeedAmount > 0 && investorProfile.shortTermNeedMonths <= 12) {
            val neededCash = (investorProfile.shortTermNeedAmount / totalValue) * 100.0
            targetCashAllocation = max(targetCashAllocation, neededCash)
            recommendations.add("Short-term financial need requires increased cash reserves")
        }
        
        // Calculate bond allocation as remainder
        val targetBondAllocation = (100.0 - targetStockAllocation - targetCashAllocation)
            .coerceIn(0.0, 100.0)
        
        // Check current vs target allocations for major categories
        val currentStockAllocation = currentAllocation.filter { (key, _) -> 
            key.contains("stock") || key.contains("equity") || key == "cyclicals" || key == "growthStocks" || key == "dividendStocks"
        }.values.sum()
        
        val currentBondAllocation = currentAllocation.filter { (key, _) -> 
            key.contains("bond") || key.contains("treasury") || key.contains("Term") || key == "TIPS"
        }.values.sum()
        
        val currentCashAllocation = currentAllocation["cash"] ?: 0.0
        
        // Determine rebalancing needs
        if (abs(currentStockAllocation - targetStockAllocation) > 5.0) {
            val direction = if (currentStockAllocation < targetStockAllocation) "increase" else "decrease"
            recommendations.add("Recommend ${direction} equity allocation to target ${targetStockAllocation.toInt()}%")
            adjustments["equity"] = targetStockAllocation - currentStockAllocation
        }
        
        if (abs(currentBondAllocation - targetBondAllocation) > 5.0) {
            val direction = if (currentBondAllocation < targetBondAllocation) "increase" else "decrease"
            recommendations.add("Recommend ${direction} bond allocation to target ${targetBondAllocation.toInt()}%")
            adjustments["bonds"] = targetBondAllocation - currentBondAllocation
        }
        
        if (abs(currentCashAllocation - targetCashAllocation) > 3.0) {
            val direction = if (currentCashAllocation < targetCashAllocation) "increase" else "decrease"
            recommendations.add("Recommend ${direction} cash allocation to target ${targetCashAllocation.toInt()}%")
            adjustments["cash"] = targetCashAllocation - currentCashAllocation
        }
        
        // Check for diversification issues
        val singleAssetThreshold = when (investorProfile.riskTolerance) {
            "conservative" -> 15.0
            "moderate" -> 20.0
            "aggressive" -> 25.0
            "very aggressive" -> 30.0
            else -> 20.0
        }
        
        val overconcentratedAssets = portfolio.filter { (_, value) -> 
            (value / totalValue) * 100 > singleAssetThreshold
        }.keys
        
        if (overconcentratedAssets.isNotEmpty()) {
            recommendations.add("Reduce concentration in: ${overconcentratedAssets.joinToString(", ")}")
            overconcentratedAssets.forEach { asset ->
                adjustments[asset] = singleAssetThreshold - ((portfolio[asset] ?: 0.0) / totalValue * 100)
            }
        }
        
        // International exposure check
        val internationalAllocation = currentAllocation.filter { (key, _) -> 
            key.contains("international") || key.contains("emerging") || key.contains("foreign")
        }.values.sum()
        
        val targetInternational = when (investorProfile.riskTolerance) {
            "conservative" -> 20.0
            "moderate" -> 30.0
            "aggressive" -> 40.0
            "very aggressive" -> 50.0
            else -> 30.0
        }
        
        if (abs(internationalAllocation - targetInternational) > 10.0) {
            val direction = if (internationalAllocation < targetInternational) "increase" else "decrease"
            recommendations.add("${direction} international exposure to ${targetInternational.toInt()}% for better diversification")
            adjustments["international"] = targetInternational - internationalAllocation
        }
        
        // Return comprehensive recommendation
        return InvestmentRecommendation(
            actionRequired = recommendations.isNotEmpty(),
            recommendations = recommendations,
            adjustments = adjustments,
            targetAllocations = mapOf(
                "equity" to targetStockAllocation,
                "bonds" to targetBondAllocation,
                "cash" to targetCashAllocation
            ),
            riskLevel = when {
                targetStockAllocation > 80 -> "High risk"
                targetStockAllocation > 60 -> "Moderate-high risk"
                targetStockAllocation > 40 -> "Moderate risk"
                targetStockAllocation > 20 -> "Low-moderate risk"
                else -> "Low risk"
            }
        )
    }
    
    /**
     * Helper function for max of two doubles
     */
    private fun max(a: Double, b: Double): Double = if (a > b) a else b
    
    /**
     * Helper function for absolute value of a double
     */
    private fun abs(value: Double): Double = if (value < 0) -value else value
}

/**
 * Data classes for storing results from complex functions
 */
data class ValidationResult(val isValid: Boolean, val errors: List<String>)

data class RiskAssessment(
    val riskScore: Int,
    val riskCategory: String,
    val riskFactors: List<String>,
    val eligible: Boolean
)

data class InvestmentRecommendation(
    val actionRequired: Boolean,
    val recommendations: List<String>,
    val adjustments: Map<String, Double>,
    val targetAllocations: Map<String, Double>,
    val riskLevel: String
)

data class MarketConditions(
    val trend: String, // "strongly bullish", "bullish", "neutral", "bearish", "strongly bearish"
    val volatility: Double, // VIX or similar metric
    val pe: Double, // Market P/E ratio
    val sentiment: String // "fearful", "neutral", "greedy"
)

data class InvestorProfile(
    val age: Int,
    val riskTolerance: String,
    val goals: List<String>,
    val targetRetirementAge: Int,
    val shortTermNeedAmount: Double,
    val shortTermNeedMonths: Int
)

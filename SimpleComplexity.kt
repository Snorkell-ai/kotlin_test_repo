package com.github.test

/**
 * Class with simple complexity methods (low cyclomatic complexity: 1-5)
 */
class SimpleComplexity {
    /**
     * Simple function that returns a greeting
     * Cyclomatic Complexity: 1
     */
    fun greet(name: String): String {
        return "Hello, $name!"
    }

    /**
     * Function to check if a number is positive, negative, or zero
     * Cyclomatic Complexity: 3
     */
    fun checkNumber(num: Int): String {
        return when {
            num > 0 -> "Positive"
            num < 0 -> "Negative"
            else -> "Zero"
        }
    }

    /**
     * Function to determine grade based on score
     * Cyclomatic Complexity: 5
     */
    fun determineGrade(score: Int): String {
        return when {
            score >= 90 -> "A"
            score >= 80 -> "B"
            score >= 70 -> "C"
            score >= 60 -> "D"
            else -> "F"
        }
    }
}

/**
 * Class to handle simple user operations
 * Methods with low cyclomatic complexity
 */
class UserManager {
    private val users = mutableListOf<String>()

    /**
     * Add a user to the list
     * Cyclomatic Complexity: 1
     */
    fun addUser(username: String): Boolean {
        return if (!users.contains(username)) {
            users.add(username)
            true
        } else {
            false
        }
    }

    /**
     * Check if user exists and has proper permissions
     * Cyclomatic Complexity: 4
     */
    fun checkUserPermission(username: String, role: String, resource: String): Boolean {
        if (!users.contains(username)) {
            return false
        }
        
        if (role == "admin") {
            return true
        }
        
        if (role == "user" && (resource == "public" || resource == "shared")) {
            return true
        }
        
        return false
    }

    /**
     * Validate user data with various checks
     * Cyclomatic Complexity: 5
     */
    fun validateUserData(username: String, email: String, age: Int): List<String> {
        val errors = mutableListOf<String>()
        
        if (username.length < 3) {
            errors.add("Username too short")
        }
        
        if (!email.contains("@")) {
            errors.add("Invalid email")
        }
        
        if (age < 18) {
            errors.add("Must be 18 or older")
        }
        
        if (users.contains(username)) {
            errors.add("Username already exists")
        }
        
        return errors
    }
}

/**
 * Class for simple calculations
 * Methods with low cyclomatic complexity
 */
class Calculator {
    /**
     * Basic addition operation
     * Cyclomatic Complexity: 1
     */
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    /**
     * Determine if a number is prime
     * Cyclomatic Complexity: 4
     */
    fun isPrime(number: Int): Boolean {
        if (number <= 1) {
            return false
        }
        
        if (number <= 3) {
            return true
        }
        
        if (number % 2 == 0 || number % 3 == 0) {
            return false
        }
        
        var i = 5
        while (i * i <= number) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false
            }
            i += 6
        }
        
        return true
    }

    /**
     * Calculates factorial with error handling
     * Cyclomatic Complexity: 3
     */
    fun factorial(n: Int): Long {
        if (n < 0) {
            throw IllegalArgumentException("Factorial not defined for negative numbers")
        }
        
        if (n > 20) {
            throw IllegalArgumentException("Value too large, will cause overflow")
        }
        
        var result: Long = 1
        for (i in 1..n) {
            result *= i
        }
        return result
    }
}

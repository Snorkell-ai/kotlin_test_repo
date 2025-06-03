package com.github.test

/**
 * Class with simple complexity methods (low cyclomatic complexity: 1-5)
 */
class SimpleComplexity {
    /**
     * Returns a greeting message for the given name.
     */
    fun greet(name: String): String {
        return "Hello, $name!"
    }
    /**
     * Determines if a number is positive, negative, or zero.
     */
    fun checkNumber(num: Int): String {
        return when {
            num > 0 -> "Positive"
            num < 0 -> "Negative"
            else -> "Zero"
        }
    }
    /**
     * Determines the grade based on the given score.
     *
     * This function evaluates the input score and assigns a letter grade according to predefined thresholds:
     * - "A" for scores 90 and above
     * - "B" for scores 80 to 89
     * - "C" for scores 70 to 79
     * - "D" for scores 60 to 69
     * - "F" for scores below 60
     *
     * @param score The student's score as an integer.
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
     * Adds a user to the list if it does not already exist.
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
     * Determines if a user has permission to access a specific resource based on their role.
     *
     * The function first checks if the user exists in the system. If the user does not exist,
     * it returns false. For existing users, it grants access if the user is an admin or if
     * the user is a regular user with permissions for 'public' or 'shared' resources.
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
     * Validates user data by checking multiple conditions.
     *
     * This function performs a series of checks on the provided username, email,
     * and age to ensure they meet specific criteria. It collects any validation errors
     * in a list and returns them. The checks include verifying the length of the
     * username, the presence of an "@" symbol in the email, the minimum age requirement,
     * and whether the username already exists in the users database.
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
     * Performs basic addition of two integers.
     */
    fun add(a: Int, b: Int): Int {
        return a + b
    }
    /**
     * Determines if a given number is prime.
     *
     * The function first handles edge cases where numbers less than or equal to 1 are not prime,
     * and numbers 2 and 3 are prime. It then eliminates even numbers and multiples of 3.
     * For numbers greater than 3, it uses a loop to check divisibility starting from 5,
     * incrementing by 6 each time (i.e., checking numbers of the form 6k ± 1).
     *
     * @param number The integer to be checked for primality.
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
     * Computes the factorial of a non-negative integer with overflow check.
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

package com.github.test

/**
 * Class with simple complexity methods (low cyclomatic complexity: 1-5)
 */
class SimpleComplexity {
    /**
     * Simple function that returns a greeting.
     */
    fun greet(name: String): String {
        return "Hello, $name!"
    }
    /**
     * Check if a number is positive, negative, or zero.
     */
    fun checkNumber(num: Int): String {
        return when {
            num > 0 -> "Positive"
            num < 0 -> "Negative"
            else -> "Zero"
        }
    }
    /**
     * Function to determine grade based on score.
     *
     * This function evaluates a given score and returns the corresponding letter
     * grade. It uses a series of conditional checks to map the score to grades
     * 'A', 'B', 'C', 'D', or 'F'. The cyclomatic complexity of this function is
     * 5, due to multiple branching conditions.
     *
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
     * Add a user to the list.
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
     * Check if a user exists and has proper permissions for a given resource.
     *
     * This function first checks if the user exists in the system. If the user
     * does not exist, it returns false. For existing users, it checks the role
     * and resource to determine permission. Admins have access to all resources,
     * while regular users can access 'public' and 'shared' resources. It returns
     * true if the user has the appropriate permissions, otherwise false.
     *
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
     * Validate user data with various checks.
     *
     * This function performs several checks on the provided username, email, and
     * age. It validates the length of the username, the presence of '@' in the
     * email, ensures the user is at least 18 years old, and checks if the
     * username already exists in the `users` list. It returns a list of error
     * messages corresponding to any failed validations.
     *
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
     * Perform basic addition of two integers.
     */
    fun add(a: Int, b: Int): Int {
        return a + b
    }
    /**
     * Determine if a number is prime.
     *
     * This function checks if a given integer is a prime number. It returns false
     * for numbers less than or equal to 1. For numbers greater than 3, it first
     * checks divisibility by 2 and 3. If not divisible, it uses a loop starting
     * from 5 to check divisibility up to the square root of the number,
     * incrementing by 6 each time.
     *
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
     * Calculates factorial with error handling.
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

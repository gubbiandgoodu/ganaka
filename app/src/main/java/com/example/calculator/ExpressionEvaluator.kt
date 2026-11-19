package com.example.calculator

import java.util.Stack

object ExpressionEvaluator {

    fun evaluate(expression: String): Double? {
        if (expression.isBlank()) return null

        try {
            // Clean and normalize expression
            val normalized = expression
                .replace("x", "*")
                .replace("×", "*")
                .replace("X", "*")
                .replace("÷", "/")
                .replace(" ", "")

            val tokens = tokenize(normalized)
            if (tokens.isEmpty()) return null

            val preprocessed = preprocessPercentage(tokens)
            val rpn = infixToRPN(preprocessed) ?: return null
            return evaluateRPN(rpn)
        } catch (_: Exception) {
            return null
        }
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }
                c == '+' || c == '*' || c == '/' || c == '(' || c == ')' || c == '%' -> {
                    tokens.add(c.toString())
                    i++
                }
                c == '-' -> {
                    // Check if unary minus
                    val isUnary = tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "(")
                    if (isUnary) {
                        // Check if followed by digit
                        if (i + 1 < expr.length && (expr[i + 1].isDigit() || expr[i + 1] == '.')) {
                            val sb = StringBuilder("-")
                            i++
                            while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                                sb.append(expr[i])
                                i++
                            }
                            tokens.add(sb.toString())
                            continue
                        } else {
                            tokens.add("0")
                            tokens.add("-")
                            i++
                        }
                    } else {
                        tokens.add("-")
                        i++
                    }
                }
                else -> i++
            }
        }
        return tokens
    }

    private fun preprocessPercentage(tokens: List<String>): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "%") {
                if (result.isNotEmpty()) {
                    val percentVal = result.removeAt(result.size - 1)
                    if (result.size >= 2 && (result[result.size - 1] == "+" || result[result.size - 1] == "-")) {
                        // There is a + or - before this percentage (e.g. 1000 + 18%)
                        // Find the base operand before the + or -
                        val op = result[result.size - 1]
                        val tokensBeforeOp = result.subList(0, result.size - 1)
                        val baseTokens = findPrecedingOperand(tokensBeforeOp)
                        if (baseTokens.isNotEmpty()) {
                            // Add ( base * percentVal / 100 )
                            result.add("(")
                            result.addAll(baseTokens)
                            result.add("*")
                            result.add(percentVal)
                            result.add("/")
                            result.add("100")
                            result.add(")")
                        } else {
                            result.add("(")
                            result.add(percentVal)
                            result.add("/")
                            result.add("100")
                            result.add(")")
                        }
                    } else {
                        // Standard percentage conversion (e.g. 1000 * 18% or just 18%)
                        result.add("(")
                        result.add(percentVal)
                        result.add("/")
                        result.add("100")
                        result.add(")")
                    }
                }
                i++
            } else {
                result.add(token)
                i++
            }
        }
        return result
    }

    private fun findPrecedingOperand(tokens: List<String>): List<String> {
        if (tokens.isEmpty()) return emptyList()
        if (tokens.last() == ")") {
            var depth = 0
            for (j in tokens.indices.reversed()) {
                if (tokens[j] == ")") depth++
                else if (tokens[j] == "(") {
                    depth--
                    if (depth == 0) {
                        return tokens.subList(j, tokens.size).toList()
                    }
                }
            }
            return tokens.toList()
        } else {
            return listOf(tokens.last())
        }
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            "%" -> 3
            else -> 0
        }
    }

    private fun infixToRPN(tokens: List<String>): List<String>? {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                output.add(token)
            } else if (token == "(") {
                stack.push(token)
            } else if (token == ")") {
                while (stack.isNotEmpty() && stack.peek() != "(") {
                    output.add(stack.pop())
                }
                if (stack.isEmpty() || stack.pop() != "(") {
                    return null // mismatched parens
                }
            } else {
                // Operator
                while (stack.isNotEmpty() && stack.peek() != "(" &&
                    precedence(stack.peek()) >= precedence(token)
                ) {
                    output.add(stack.pop())
                }
                stack.push(token)
            }
        }

        while (stack.isNotEmpty()) {
            val op = stack.pop()
            if (op == "(" || op == ")") return null
            output.add(op)
        }

        return output
    }

    private fun evaluateRPN(rpn: List<String>): Double? {
        val stack = Stack<Double>()

        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
            } else if (token == "%") {
                if (stack.isEmpty()) return null
                val a = stack.pop()
                stack.push(a / 100.0)
            } else {
                if (stack.size < 2) return null
                val b = stack.pop()
                val a = stack.pop()
                val res = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) return null
                        a / b
                    }
                    else -> return null
                }
                stack.push(res)
            }
        }

        return if (stack.size == 1) stack.pop() else null
    }
}

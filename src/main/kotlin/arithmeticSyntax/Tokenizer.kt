package arithmeticSyntax

// Tokenizer is only called within the codeAnalysis package, so it is INTERNAL
internal class Tokenizer(val text: String) {
    private var position: Int = 0
    private val currentChar: Char
        get() = if (position >= text.length) Char.MIN_VALUE else text[position]
    private val localDiagnostics: MutableList<String> = mutableListOf<String>()
    val diagnostics: List<String>
        get() = localDiagnostics

    private fun next() {
        ++position
    }

    fun nextToken(): SyntaxToken =
        if (position >= text.length) {
            SyntaxToken(TokenType.EOF, position, "${Char.MIN_VALUE}", null)
        } else if (currentChar.isDigit()) {
            createNumberSyntaxToken()
        } else if (currentChar.isWhitespace()) {
            createWhitespaceSyntaxToken()
        } else if (currentChar.isLetter()) {
            createWordToken()
        } else {
            selectOperatorToken()
        }

    private fun selectOperatorToken(): SyntaxToken =
        when (currentChar) {
            '+' -> createOperatorToken(TokenType.PLUS, "+")
            '-' -> createOperatorToken(TokenType.MINUS, "-")
            '*' -> createOperatorToken(TokenType.TIMES, "*")
            '/' -> createOperatorToken(TokenType.SLASH, "/")
            '(' -> createOperatorToken(TokenType.OPEN_PAREN, "(")
            ')' -> createOperatorToken(TokenType.CLOSE_PAREN, ")")
            '^' -> createOperatorToken(TokenType.EXPONENT_ARROW, "^")
            '%' -> createOperatorToken(TokenType.MODULO, "%")
            else -> {
                localDiagnostics.add("ERROR: bad character input: '${currentChar}' at position ${position}")
                createOperatorToken(TokenType.BAD_TOKEN, text.substring(position - 1, position))
            }
        }

    private fun createWordToken(): SyntaxToken {
        val start = position
        while (currentChar.isLetter()) {
            next()
        }
        val wordAsText = text.substring(start, position)
        return when(wordAsText.lowercase()) {
            "sqr" -> SyntaxToken(TokenType.SQR, start, "sqr", null)
            "sqrt" -> SyntaxToken(TokenType.SQRT, start, "sqrt", null)
            else -> {
                localDiagnostics.add("ERROR: The characters ${wordAsText} isn't a recognized token")
                createOperatorToken(TokenType.BAD_TOKEN, wordAsText)
            }
        }
    }

    private fun createWhitespaceSyntaxToken(): SyntaxToken {
        val start = position
        while (currentChar.isWhitespace()) {
            next()
        }
        val whitespaceAsText = text.substring(start, position)
        return SyntaxToken(TokenType.WHITESPACE, start, whitespaceAsText, null)
    }

    private fun createNumberSyntaxToken(): SyntaxToken {
        val start = position
        while (currentChar.isDigit()) {
            next()
        }
        val numberAsText = text.substring(start, position)
        val number = numberAsText.toInt()

        if (number.toString() != numberAsText)
            localDiagnostics.add("ERROR: The number ${numberAsText} can't be represented by a 32-bit integer")

        return SyntaxToken(TokenType.NUMBER, start, numberAsText, number)
    }

    private fun createOperatorToken(type: TokenType, text: String): SyntaxToken {
        next()
        return SyntaxToken(type, position - 1, text, null)
    }
}

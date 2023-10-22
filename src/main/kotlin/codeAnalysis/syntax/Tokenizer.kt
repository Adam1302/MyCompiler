package codeAnalysis.syntax

// Tokenizer is only called within the codeAnalysis package, so it is INTERNAL
internal class Tokenizer(val text: String) {
    private var position: Int = 0
    private val currentChar: Char
        get() = peek(0)
    private val lookaheadChar: Char
        get() = peek(1)
    private val localDiagnostics: MutableList<String> = mutableListOf<String>()
    val diagnostics: List<String>
        get() = localDiagnostics

    private fun peek(offset: Int): Char {
        val idx = position + offset
        return if (idx >= text.length) Char.MIN_VALUE else text[position]
    }

    private fun next() {
        ++position
    }

    fun nextToken(): SyntaxToken {
        /* Starting Point:
            We are looking for:
                - numbers
                - operators
                - whitespace
         */
        if (position >= text.length) {
            return SyntaxToken(TokenType.EOF, position, "${Char.MIN_VALUE}", null)
        }
        if (currentChar.isDigit()) {
            val start = position
            while (currentChar.isDigit()) {
                next()
            }
            val numberAsText = text.substring(start, position)
            val number = numberAsText.toInt()

            if (number.toString() != numberAsText)
                localDiagnostics.add("ERROR: The number ${numberAsText} can't be represented by a 32-bit integer")

            return SyntaxToken(TokenType.NUMBER, start, numberAsText, number)
        } else if (currentChar.isWhitespace()) {
            val start = position
            while (currentChar.isWhitespace()) {
                next()
            }
            val whitespaceAsText = text.substring(start, position)
            return SyntaxToken(TokenType.WHITESPACE, start, whitespaceAsText, null)
        } else if (currentChar.isLetter()) {
            val start = position;
            while (currentChar.isLetter()) {
                next()
            }
            val text = text.substring(start, position)
            val kind = SyntaxRules.getKeywordKind(text)
            return SyntaxToken(kind, start, text, null)
        }
        when (currentChar) {
            '+' -> {
                next()
                return SyntaxToken(TokenType.PLUS, position-1, "+", null)
            }
            '-' -> {
                next()
                return SyntaxToken(TokenType.MINUS, position-1, "-", null)
            }
            '*' -> {
                next()
                return SyntaxToken(TokenType.TIMES, position-1, "*", null)
            }
            '/' -> {
                next()
                return SyntaxToken(TokenType.SLASH, position-1, "/", null)
            }
            '(' -> {
                next()
                return SyntaxToken(TokenType.OPEN_PAREN, position-1, "(", null)
            }
            ')' -> {
                next()
                return SyntaxToken(TokenType.CLOSE_PAREN, position-1, ")", null)
            }
            '!' -> {
                next()
                return SyntaxToken(TokenType.BANG, position-1, "!", null)
            }
            '&' -> {
                if (lookaheadChar == '&') {
                    next()
                    next()
                    return SyntaxToken(
                        TokenType.AMPERSAND_DOUBLE,
                        position - 2,
                        "&&", null
                    )
                }
            }
            '|' -> {
                if (lookaheadChar == '|') {
                    next()
                    next()
                    return SyntaxToken(
                        TokenType.PIPE_DOUBLE,
                        position - 2,
                        "||", null
                    )
                }
            }
            else -> {
                localDiagnostics.add("ERROR: bad character input: '${currentChar}' at position ${position}")
                next() // BAD TOKEN
            }
        }

        return SyntaxToken(
            TokenType.BAD_TOKEN, position-1,
            text.substring(position-1, position), null)
    }
}

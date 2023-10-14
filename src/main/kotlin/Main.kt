fun main(args: Array<String>) {
    while (true) {
        print("> ")

        val line: String = readlnOrNull()?: ""
        if (line.isBlank()) break

        val tokenizer: Tokenizer = Tokenizer(line)
        while (true) {
            var currentToken = tokenizer.nextToken()
            if (currentToken.type == TokenType.EOF) break;
            print("${currentToken.type}: '${currentToken.text}'")
            if (currentToken.value != null) {
                print(" ${currentToken.value}")
            }

            println()
        }
    }
}

enum class TokenType {
    NUMBER, WHITESPACE, PLUS, MINUS, TIMES, SLASH, OPEN_PAREN, CLOSE_PAREN, BAD_TOKEN, EOF
}

class SyntaxToken(val type: TokenType, val position: Int, val text: String, val value: Any?) {

}

class Tokenizer(val text: String) {
     var position: Int = 0
     private val currentChar: Char
         get() = if (position >= text.length) Char.MIN_VALUE else text[position]

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
        else if (currentChar.isDigit()) {
            val start = position
            while (currentChar.isDigit()) {
                next()
            }
            val numberAsText = text.substring(start, position)
            val number = numberAsText.toInt()
            return SyntaxToken(TokenType.NUMBER, start, numberAsText, number)
        } else if (currentChar.isWhitespace()) {
            val start = position
            while (currentChar.isWhitespace()) {
                next()
            }
            val whitespaceAsText = text.substring(start, position)
            return SyntaxToken(TokenType.WHITESPACE, start, whitespaceAsText, null)
        } else if (currentChar == '+') {
            next()
            return SyntaxToken(TokenType.PLUS, position-1, "+", null)
        } else if (currentChar == '-') {
            next()
            return SyntaxToken(TokenType.MINUS, position-1, "-", null)
        } else if (currentChar == '*') {
            next()
            return SyntaxToken(TokenType.TIMES, position-1, "*", null)
        } else if (currentChar == '/') {
            next()
            return SyntaxToken(TokenType.SLASH, position-1, "/", null)
        } else if (currentChar == '(') {
            next()
            return SyntaxToken(TokenType.OPEN_PAREN, position-1, "(", null)
        } else if (currentChar == ')') {
            next()
            return SyntaxToken(TokenType.CLOSE_PAREN, position-1, ")", null)
        } else {
            next() // BAD TOKEN
        }

        return SyntaxToken(TokenType.BAD_TOKEN, position-1,
            text.substring(position-1, position), null)
    }
}

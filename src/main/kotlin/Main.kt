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
    NUMBER, WHITESPACE, PLUS, MINUS, TIMES, SLASH, OPEN_PAREN, CLOSE_PAREN, BAD_TOKEN, EOF, NUMBER_EXPRESSION, BINARY_EXPRESSION
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

// Once we tokenize, it's time to build the AST (Abstract Syntax Tree)
abstract class SyntaxNode { // ABSTRACT NODE
    abstract val kind: TokenType
}

abstract class ExpressionSyntaxNode : SyntaxNode() {

}

class NumberExpressionSyntaxNode(val numberToken: SyntaxToken) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.NUMBER_EXPRESSION
}
class BinaryExpressionSyntaxNode(
    val operatorToken: SyntaxToken,
    val leftExpSyntaxNode: ExpressionSyntaxNode,
    val rightExpSyntaxNode: ExpressionSyntaxNode
) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.BINARY_EXPRESSION
}

class Parser(val text: String) {
    private var position: Int = 0 // position in the token list
    private lateinit var tokens: List<SyntaxToken>
    private val current: SyntaxToken
        get() = peek(0)

    init {
        var tokenizer: Tokenizer = Tokenizer(text) // Tokenizer is just local, we won't need it after we have a token list
        var tokenList: MutableList<SyntaxToken> = mutableListOf()
        lateinit var token: SyntaxToken

        token = tokenizer.nextToken()
        while (token.type != TokenType.EOF) { // Populating token list
            if (token.type !in listOf(TokenType.WHITESPACE, TokenType.BAD_TOKEN)) {
                tokenList.add(token)
            }

            token = tokenizer.nextToken()
        }

        tokens = tokenList
    }

    private fun peek(offset: Int): SyntaxToken {
        val idx = offset + position
        if (idx >= tokens.size)
            return tokens.last()

        return tokens[idx]
    }

    private fun next() { ++position }

    private fun nextToken(): SyntaxToken {
        val localCurrent = current
        ++position
        return localCurrent
    }

    private fun match(tokenType: TokenType): SyntaxToken {
        if (current.type == tokenType)
            return nextToken()

        return SyntaxToken(tokenType, current.position, "${Char.MIN_VALUE}", null)
    }

    fun parse(): ExpressionSyntaxNode {
        var leftSide = parseNextExpression()
        while (current.type in listOf(
                TokenType.PLUS, TokenType.MINUS, TokenType.TIMES, TokenType.SLASH
        )) {
            val operatorToken = nextToken()
            val rightSide = parseNextExpression()
            leftSide = BinaryExpressionSyntaxNode(operatorToken, leftSide, rightSide)
        }

        return leftSide
    }

    fun parseNextExpression(): ExpressionSyntaxNode {
        val numberToken = match(TokenType.NUMBER) // if it's a number, use it, otherwise tokenize
        return NumberExpressionSyntaxNode(numberToken)
    }
}

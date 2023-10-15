import java.lang.Exception

// Everything after this is in the colour
val red = "\u001b[31m"
val green = "\u001b[32m"
val yellow = "\u001b[33m"
val blue = "\u001b[34m"
val cyan = "\u001b[36m"
val white = "\u001b[37m"
// Resets previous color codes
val reset = "\u001b[0m"

fun main(args: Array<String>) {
    while (true) {
        print("> ")

        val line: String = readlnOrNull()?: ""
        if (line.isBlank()) break

        val parser = Parser(line)
        val abstractSyntaxTree = parser.parse()

        prettyPrint(abstractSyntaxTree.root)

        if (abstractSyntaxTree.diagnostics.isNotEmpty()) {
            print(red)
            println("ERRORS:")
            for (diagnostic in parser.diagnostics) {
                println(diagnostic)
            }
            print(reset)
        } else {
            val evaluator = Evaluator(abstractSyntaxTree.root)
            val result = evaluator.evaluate()
            println(result)
        }
    }
}

fun prettyPrint(syntaxNode: SyntaxNode, indent: String = "", isLast: Boolean = true) {
    print(cyan) // COLOURING
    val indicator = if (isLast) "└──" else "├──"

    print(indent + indicator + syntaxNode.kind)
    if (syntaxNode is SyntaxToken && (syntaxNode as SyntaxToken).value != null) {
        print(" " + (syntaxNode as SyntaxToken).value)
    }
    println()

    val localIndent = if (isLast) "$indent     " else "${indent}|    "

    for (child in syntaxNode.getChildren())
        prettyPrint(child, localIndent, child == syntaxNode.getChildren().last())

    print(reset)
}

enum class TokenType {
    NUMBER, WHITESPACE, PLUS, MINUS, TIMES, SLASH, OPEN_PAREN, CLOSE_PAREN, BAD_TOKEN, EOF, NUMBER_EXPRESSION, BINARY_EXPRESSION, PARENTHESIZED_EXPRESSION
}

class SyntaxToken(val type: TokenType, val position: Int, val text: String, val value: Any?) : SyntaxNode() {
    override val kind: TokenType
        get() = type

    override fun getChildren(): List<SyntaxNode> = emptyList()
}

class Tokenizer(val text: String) {
     private var position: Int = 0
     private val currentChar: Char
         get() = if (position >= text.length) Char.MIN_VALUE else text[position]
    private val localDiagnostics: MutableList<String> = mutableListOf<String>()
    val diagnostics: List<String>
        get() = localDiagnostics

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
            localDiagnostics.add("ERROR: bad character input: '${currentChar}' at position ${position}")
            next() // BAD TOKEN
        }

        return SyntaxToken(TokenType.BAD_TOKEN, position-1,
            text.substring(position-1, position), null)
    }
}

// Once we tokenize, it's time to build the AST (Abstract Syntax Tree)
abstract class SyntaxNode { // ABSTRACT NODE
    abstract val kind: TokenType

    abstract fun getChildren(): List<SyntaxNode>
}

abstract class ExpressionSyntaxNode : SyntaxNode() {

}

class NumberExpressionSyntaxNode(val numberToken: SyntaxToken) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.NUMBER_EXPRESSION
    override fun getChildren(): List<SyntaxNode> = listOf(numberToken)
}
class BinaryExpressionSyntaxNode(
    val operatorToken: SyntaxToken,
    val leftExpSyntaxNode: ExpressionSyntaxNode,
    val rightExpSyntaxNode: ExpressionSyntaxNode
) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.BINARY_EXPRESSION
    override fun getChildren(): List<SyntaxNode> {
        return listOf(leftExpSyntaxNode, operatorToken, rightExpSyntaxNode)
    }
}
class ParanthesizedExpressionSyntaxNode(
    val openBracketSyntaxToken: SyntaxToken,
    val expressionSyntaxNode: ExpressionSyntaxNode,
    val closedBracketSyntaxToken: SyntaxToken
) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.PARENTHESIZED_EXPRESSION
    override fun getChildren(): List<SyntaxNode> {
        return listOf(openBracketSyntaxToken, expressionSyntaxNode, closedBracketSyntaxToken)
    }
}

class AbstractSyntaxTree(
    val root: ExpressionSyntaxNode,
    val eofToken: SyntaxToken,
    val diagnostics: List<String>)
{

}

// RECURSIVE DECENT PARSER
class Parser(val text: String) {
    private var position: Int = 0 // position in the token list
    private lateinit var tokens: List<SyntaxToken>
    private val current: SyntaxToken
        get() = peek(0)
    private val localDiagnostics: MutableList<String> = mutableListOf<String>()
    val diagnostics: List<String>
        get() = localDiagnostics

    init {
        val tokenizer: Tokenizer = Tokenizer(text) // Tokenizer is just local, we won't need it after we have a token list
        val tokenList: MutableList<SyntaxToken> = mutableListOf()
        lateinit var token: SyntaxToken

        token = tokenizer.nextToken()
        while (token.type != TokenType.EOF) { // Populating token list
            if (token.type !in listOf(TokenType.WHITESPACE, TokenType.BAD_TOKEN)) {
                tokenList.add(token)
            }

            token = tokenizer.nextToken()
        }

        tokenList.add(token)
        tokens = tokenList
        localDiagnostics.addAll(tokenizer.diagnostics)
    }

    private fun peek(offset: Int): SyntaxToken {
        val idx = offset + position
        if (idx >= tokens.size)
            return tokens.last()

        return tokens[idx]
    }

    private fun next() { ++position }

    // nextToken returns the current token and moves to the next one
    private fun nextToken(): SyntaxToken {
        val localCurrent = current
        ++position
        return localCurrent
    }

    private fun match(tokenType: TokenType): SyntaxToken {
        if (current.type == tokenType)
            return nextToken()
        else
            localDiagnostics.add("ERROR: Unexpected token of type <${current.type}> with value" +
                    " ${current.value} at position ${current.position}, expected <${tokenType}>")
        return SyntaxToken(tokenType, current.position, "${Char.MIN_VALUE}", null)
    }

    fun parse(): AbstractSyntaxTree {
        val expression = parseTerms() // ACTUAL PARSE
        val eofToken = match(TokenType.EOF) // ASSERT remaining token after parse is EOF token
        return AbstractSyntaxTree(expression, eofToken, diagnostics)
    }

    // PRIMARY EXPRESSION: A LITERAL, LIKE A NUMBER, OR AN EXPRESSION ENCLOSED IN PARENTHESES
    fun parsePrimaryExpression(): ExpressionSyntaxNode {
        if (current.type == TokenType.OPEN_PAREN) {
            val left = nextToken()
            val expression = parseTerms()
            val right = match(TokenType.CLOSE_PAREN)

            return ParanthesizedExpressionSyntaxNode(left, expression, right)
        } else {
            val numberToken = match(TokenType.NUMBER) // if it's a number, use it, otherwise tokenize the operator
            return NumberExpressionSyntaxNode(numberToken)
        }

    }

    // FACTOR: * or /
    fun parseFactor(): ExpressionSyntaxNode { // BASIC LEFT-TO-RIGHT PARSING forming AST
        var leftSide = parsePrimaryExpression() // starting with a NUMBER and moving to next
        while (current.type in listOf(
                TokenType.TIMES, TokenType.SLASH
            )) { // this'll keep going so long as * or / operators remain in the token list
            val operatorToken = nextToken() // we know we have an operator, so we store and move to the next token
            val rightSide = parsePrimaryExpression()
            leftSide = BinaryExpressionSyntaxNode(operatorToken, leftSide, rightSide)
        }

        return leftSide
    }

    // TERMS: + or -
    fun parseTerms(): ExpressionSyntaxNode { // BASIC LEFT-TO-RIGHT PARSING forming AST
        var leftSide = parseFactor() // starting with a NUMBER and moving to next
        while (current.type in listOf(
                TokenType.PLUS, TokenType.MINUS
            )) { // this'll keep going so long as +/- operators remain in the token list
            val operatorToken = nextToken() // we know we have an operator, so we store and move to the next token
            val rightSide = parseFactor()
            leftSide = BinaryExpressionSyntaxNode(operatorToken, leftSide, rightSide)
        }

        return leftSide
    }
}

class Evaluator(val root: ExpressionSyntaxNode) {
    fun evaluate(): Int = evaluateExpression(root)

    private fun evaluateExpression(node: ExpressionSyntaxNode) : Int {
        when (node) {
            is NumberExpressionSyntaxNode -> {
                return node.numberToken.value as Int
            }
            is BinaryExpressionSyntaxNode -> {
                val left = evaluateExpression((node as BinaryExpressionSyntaxNode).leftExpSyntaxNode)
                val right = evaluateExpression((node as BinaryExpressionSyntaxNode).rightExpSyntaxNode)

                return when (node.operatorToken.type) {
                    TokenType.PLUS -> left + right
                    TokenType.MINUS -> left - right
                    TokenType.TIMES -> left * right
                    TokenType.SLASH -> left / right
                    else -> throw Exception("Unexpected binary operator")
                }
            }
            is ParanthesizedExpressionSyntaxNode -> {
                return evaluateExpression(node.expressionSyntaxNode)
            }
            else -> throw Exception("Unexpected node <${node.kind}>")
        }
    }
}

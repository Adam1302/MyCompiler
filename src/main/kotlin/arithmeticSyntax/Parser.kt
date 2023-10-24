package arithmeticSyntax

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
        createTokenList()
    }

    private fun createTokenList() {
        val tokenizer = Tokenizer(text) // Tokenizer is just local, we won't need it after we have a token list
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

    fun parse(): SyntaxTree {
        val expression = parseExpression() // ACTUAL PARSE
        val eofToken = matchToken(TokenType.EOF) // ASSERT remaining token after parse is EOF token
        return SyntaxTree(expression, eofToken, diagnostics)
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

    // GETS CURRENT TOKEN IF IT'S A CERTAIN TYPE
    private fun matchToken(tokenType: TokenType): SyntaxToken {
        if (current.type == tokenType)
            return nextToken()
        else
            localDiagnostics.add("ERROR: Unexpected token of type <${current.type}> with value" +
                    " ${current.value} at position ${current.position}, expected <${tokenType}>")
        return SyntaxToken(tokenType, current.position, "${Char.MIN_VALUE}", null)
    }

    // PRIMARY EXPRESSION: A LITERAL, LIKE A NUMBER, OR AN EXPRESSION ENCLOSED IN PARENTHESES
    private fun parsePrimaryExpression(): ExpressionSyntaxNode =
        if (current.type == TokenType.OPEN_PAREN) {
            getParanthesizedExpressionSyntaxNode()
        } else {
            getLiteralExpressionSyntaxNode()
        }

    private fun getLiteralExpressionSyntaxNode(): LiteralExpressionSyntaxNode {
        val numberToken = matchToken(TokenType.NUMBER) // if it's a number, use it, otherwise tokenize the operator
        return LiteralExpressionSyntaxNode(numberToken)
    }

    private fun getParanthesizedExpressionSyntaxNode(): ParanthesizedExpressionSyntaxNode {
        val left = nextToken()
        val expression = parseExpression()
        val right = matchToken(TokenType.CLOSE_PAREN)

        return ParanthesizedExpressionSyntaxNode(left, expression, right)
    }

    private fun parseExpression(parentNodePrecedence: Int = 0): ExpressionSyntaxNode {
        var left: ExpressionSyntaxNode

        val unaryOperatorPrecedence = SyntaxRules.getUnaryOperatorPrecedence(current.type)
        left = if (unaryOperatorPrecedence != 0 && unaryOperatorPrecedence >= parentNodePrecedence) {
            getUnaryExpressionSyntaxNode(unaryOperatorPrecedence)
        } else {
            parsePrimaryExpression()
        }

        while (true) {
            val currentNodePrecedence = SyntaxRules.getBinaryOperatorPrecedence(current.type)
            if (currentNodePrecedence == 0 || currentNodePrecedence <= parentNodePrecedence) break

            left = getBinaryExpressionSyntaxNode(currentNodePrecedence, left)
        }
        return left
    }

    private fun getUnaryExpressionSyntaxNode(unaryOperatorPrecedence: Int): UnaryExpressionSyntaxNode {
        val operatorToken = nextToken()
        val operand = parseExpression(unaryOperatorPrecedence)
        return UnaryExpressionSyntaxNode(operatorToken, operand)
    }

    private fun getBinaryExpressionSyntaxNode(
        currentNodePrecedence: Int,
        left: ExpressionSyntaxNode
    ): ExpressionSyntaxNode {
        var left1 = left
        val operatorToken = nextToken()
        val right = parseExpression(currentNodePrecedence)
        left1 = BinaryExpressionSyntaxNode(operatorToken, left1, right)
        return left1
    }
}

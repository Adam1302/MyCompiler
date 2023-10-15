package codeAnalysis

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
    private fun parsePrimaryExpression(): ExpressionSyntaxNode {
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
    private fun parseFactor(): ExpressionSyntaxNode { // BASIC LEFT-TO-RIGHT PARSING forming AST
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
    private fun parseTerms(): ExpressionSyntaxNode { // BASIC LEFT-TO-RIGHT PARSING forming AST
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

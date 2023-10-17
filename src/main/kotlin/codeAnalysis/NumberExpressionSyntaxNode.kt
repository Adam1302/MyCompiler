package codeAnalysis

class LiteralExpressionSyntaxNode(val literalToken: SyntaxToken) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.NUMBER_EXPRESSION
    override fun getChildren(): List<SyntaxNode> = listOf(literalToken)
}

package codeAnalysis

class NumberExpressionSyntaxNode(val numberToken: SyntaxToken) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.NUMBER_EXPRESSION
    override fun getChildren(): List<SyntaxNode> = listOf(numberToken)
}

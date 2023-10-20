package codeAnalysis.syntax

class LiteralExpressionSyntaxNode(val literalToken: SyntaxToken) : ExpressionSyntaxNode() {
    override val kind: TokenType = TokenType.LITERAL_EXPRESSION
    override fun getChildren(): List<SyntaxNode> = listOf(literalToken)
}

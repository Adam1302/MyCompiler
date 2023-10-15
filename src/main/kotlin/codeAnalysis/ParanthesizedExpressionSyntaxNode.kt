package codeAnalysis

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

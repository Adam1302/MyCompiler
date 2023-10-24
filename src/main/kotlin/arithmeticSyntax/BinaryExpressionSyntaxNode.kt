package arithmeticSyntax

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

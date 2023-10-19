package codeAnalysis

class UnaryExpressionSyntaxNode(
    val operatorToken: SyntaxToken,
    val expSyntaxNode: ExpressionSyntaxNode
    ) : ExpressionSyntaxNode() {
        override val kind: TokenType = TokenType.UNARY_EXPRESSION
        override fun getChildren(): List<SyntaxNode> {
            return listOf(operatorToken, expSyntaxNode)
        }
    }

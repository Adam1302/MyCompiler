package codeAnalysis.syntax

import utils.VarType

class LiteralExpressionSyntaxNode(val literalToken: SyntaxToken, val value: Any?) : ExpressionSyntaxNode() {
    constructor(literalToken: SyntaxToken)
            : this(literalToken, literalToken.value) {}

    override val kind: TokenType = TokenType.LITERAL_EXPRESSION
    override fun getChildren(): List<SyntaxNode> = listOf(literalToken)
}

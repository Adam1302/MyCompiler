package arithmeticSyntax

class SyntaxToken(val type: TokenType, val position: Int, val text: String, val value: Any?) : SyntaxNode() {
    override val kind: TokenType
        get() = type

    override fun getChildren(): List<SyntaxNode> = emptyList()
}

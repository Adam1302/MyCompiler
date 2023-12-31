package arithmeticSyntax

abstract class SyntaxNode { // ABSTRACT NODE
    abstract val kind: TokenType
    abstract fun getChildren(): List<SyntaxNode>
}

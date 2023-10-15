package codeAnalysis

class AbstractSyntaxTree(
    val root: ExpressionSyntaxNode,
    val eofToken: SyntaxToken,
    val diagnostics: List<String>)
{
    companion object {
        fun parse(text: String): AbstractSyntaxTree {
            val parser = Parser(text)
            return parser.parse()
        }
    }
}

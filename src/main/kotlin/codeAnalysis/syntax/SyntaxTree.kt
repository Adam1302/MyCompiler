package codeAnalysis.syntax

class SyntaxTree(
    val root: ExpressionSyntaxNode,
    val eofToken: SyntaxToken,
    val diagnostics: List<String>)
{
    companion object {
        fun parse(text: String): SyntaxTree {
            val parser = Parser(text)
            return parser.parse()
        }
    }
}

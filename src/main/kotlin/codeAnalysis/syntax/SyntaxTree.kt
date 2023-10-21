package codeAnalysis.syntax

/*
A CONCRETE SYNTAX TREE is a representation of tokens in a tree-like form
- It does not contain all the simplifications that an Abstract Syntax Tree does
- It contains purely syntactic information, but still checks for proper structure according to its grammar rules
- ASTs, on the other hand, simplify the tree to only what is necessary to translate to binary or evaluate directly
 */

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

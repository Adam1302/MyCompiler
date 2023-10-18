package codeAnalysis

internal class SyntaxRules {
    companion object {
        fun getBinaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                in listOf(TokenType.TIMES, TokenType.SLASH) -> 2
                in listOf(TokenType.PLUS, TokenType.MINUS) -> 1
                else -> 0
            }
        }
    }
}
package codeAnalysis.syntax

internal class SyntaxRules {
    companion object {
        fun getUnaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                in listOf(
                    TokenType.PLUS, TokenType.MINUS,
                    TokenType.BANG
                ) -> 5 // HIGHEST PRECEDENCE SO UNARY OPERATORS ARE EVALUATED FIRST
                // THIS ISN'T ACTUALLY NECESSARY SINCE MULTIPLICATION IS TRANSITIVE
                // HENCE, WE COULD JUST AS WELL HAVE A 1 INSTEAD OF 3
                else -> 0
            }
        }

        fun getBinaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                in listOf(TokenType.TIMES, TokenType.SLASH) -> 4
                in listOf(TokenType.PLUS, TokenType.MINUS) -> 3
                TokenType.AMPERSAND_DOUBLE -> 2
                TokenType.PIPE_DOUBLE -> 1
                else -> 0
            }
        }

        fun getKeywordKind(text: String): TokenType {
            return when(text) {
                "true" -> TokenType.TRUE_KEYWORD
                "false" -> TokenType.FALSE_KEYWORD
                else -> TokenType.IDENTIFIER
            }
        }
    }
}
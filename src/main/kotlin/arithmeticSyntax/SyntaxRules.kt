package arithmeticSyntax

internal class SyntaxRules {
    companion object {
        fun getUnaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                in listOf(TokenType.PLUS, TokenType.MINUS) -> 5 // HIGHEST PRECEDENCE SO UNARY OPERATORS ARE EVALUATED FIRST
                // THIS ISN'T ACTUALLY NECESSARY SINCE MULTIPLICATION IS TRANSITIVE
                // HENCE, WE COULD JUST AS WELL HAVE A 1 INSTEAD OF 5
                else -> 0
            }
        }

        fun getBinaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                TokenType.EXPONENT_ARROW -> 3
                in listOf(TokenType.TIMES, TokenType.SLASH, TokenType.MODULO) -> 2
                in listOf(TokenType.PLUS, TokenType.MINUS) -> 1
                else -> 0
            }
        }
    }
}
package arithmeticSyntax

internal class SyntaxRules {
    companion object {
        fun getUnaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                TokenType.PLUS, TokenType.MINUS -> 10 // HIGHEST PRECEDENCE SO UNARY OPERATORS ARE EVALUATED FIRST
                // THIS ISN'T ACTUALLY NECESSARY SINCE MULTIPLICATION IS TRANSITIVE
                // HENCE, WE COULD JUST AS WELL HAVE A 1 INSTEAD OF 5
                TokenType.SQRT, TokenType.SQR -> 6
                else -> 0
            }
        }
        fun getBinaryOperatorPrecedence(kind: TokenType): Int {
            return when(kind) {
                TokenType.EXPONENT_ARROW -> 8
                TokenType.TIMES, TokenType.SLASH, TokenType.MODULO -> 2
                TokenType.PLUS, TokenType.MINUS -> 1
                else -> 0
            }
        }
    }
}
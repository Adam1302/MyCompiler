package arithmeticSyntax

enum class TokenType {
    BAD_TOKEN, EOF, WHITESPACE,
    NUMBER,
    PLUS, MINUS, TIMES, SLASH,
    OPEN_PAREN, CLOSE_PAREN,
    EXPONENT_ARROW,
    LITERAL_EXPRESSION, UNARY_EXPRESSION, BINARY_EXPRESSION, PARENTHESIZED_EXPRESSION
}

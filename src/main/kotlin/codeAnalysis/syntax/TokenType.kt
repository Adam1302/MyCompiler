package codeAnalysis.syntax

enum class TokenType {
    BAD_TOKEN, EOF, WHITESPACE,
    NUMBER,
    PLUS, MINUS, TIMES, SLASH, OPEN_PAREN, CLOSE_PAREN,
    LITERAL_EXPRESSION, UNARY_EXPRESSION, BINARY_EXPRESSION, PARENTHESIZED_EXPRESSION,
    FALSE_KEYWORD, TRUE_KEYWORD,
    IDENTIFIER
}

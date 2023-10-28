package arithmeticSyntax

import java.lang.Exception
import kotlin.math.pow
import kotlin.math.sqrt

class Evaluator(val root: ExpressionSyntaxNode) {
    fun evaluate(): Double = evaluateExpression(root)

    private fun evaluateExpression(node: ExpressionSyntaxNode) : Double {
        when (node) {
            is LiteralExpressionSyntaxNode -> {
                return node.literalToken.value as Double
            }
            is UnaryExpressionSyntaxNode -> {
                val expr = evaluateExpression(node.expSyntaxNode)
                return when (node.operatorToken.type) {
                    TokenType.PLUS -> expr
                    TokenType.MINUS -> -expr
                    TokenType.SQRT -> sqrt(expr)
                    TokenType.SQR -> expr.pow(2)
                    else -> throw Exception("Unexpected unary operator ${node.operatorToken.text}")
                }
            }
            is BinaryExpressionSyntaxNode -> {
                val left = evaluateExpression(node.leftExpSyntaxNode)
                val right = evaluateExpression(node.rightExpSyntaxNode)

                return when (node.operatorToken.type) {
                    TokenType.PLUS -> left + right
                    TokenType.MINUS -> left - right
                    TokenType.TIMES -> left * right
                    TokenType.SLASH -> left / right
                    TokenType.MODULO -> left % right
                    TokenType.EXPONENT_ARROW -> left.pow(right)
                    else -> throw Exception("Unexpected binary operator ${node.operatorToken.text}")
                }
            }
            is ParanthesizedExpressionSyntaxNode -> {
                return evaluateExpression(node.expressionSyntaxNode)
            }
            else -> throw Exception("Unexpected node <${node.kind}>")
        }
    }
}

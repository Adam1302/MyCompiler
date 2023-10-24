package arithmeticSyntax

import java.lang.Exception
import kotlin.math.pow

class Evaluator(val root: ExpressionSyntaxNode) {
    fun evaluate(): Int = evaluateExpression(root)

    private fun evaluateExpression(node: ExpressionSyntaxNode) : Int {
        when (node) {
            is LiteralExpressionSyntaxNode -> {
                return node.literalToken.value as Int
            }
            is UnaryExpressionSyntaxNode -> {
                val expr = evaluateExpression(node.expSyntaxNode)
                return when (node.operatorToken.type) {
                    TokenType.PLUS -> expr
                    TokenType.MINUS -> -expr
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
                    TokenType.EXPONENT_ARROW -> ((left.toDouble()).pow(right).toInt())
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

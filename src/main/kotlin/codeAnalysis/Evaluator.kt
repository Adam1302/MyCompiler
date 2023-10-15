package codeAnalysis

import java.lang.Exception

class Evaluator(val root: ExpressionSyntaxNode) {
    fun evaluate(): Int = evaluateExpression(root)

    private fun evaluateExpression(node: ExpressionSyntaxNode) : Int {
        when (node) {
            is NumberExpressionSyntaxNode -> {
                return node.numberToken.value as Int
            }
            is BinaryExpressionSyntaxNode -> {
                val left = evaluateExpression((node as BinaryExpressionSyntaxNode).leftExpSyntaxNode)
                val right = evaluateExpression((node as BinaryExpressionSyntaxNode).rightExpSyntaxNode)

                return when (node.operatorToken.type) {
                    TokenType.PLUS -> left + right
                    TokenType.MINUS -> left - right
                    TokenType.TIMES -> left * right
                    TokenType.SLASH -> left / right
                    else -> throw Exception("Unexpected binary operator")
                }
            }
            is ParanthesizedExpressionSyntaxNode -> {
                return evaluateExpression(node.expressionSyntaxNode)
            }
            else -> throw Exception("Unexpected node <${node.kind}>")
        }
    }
}

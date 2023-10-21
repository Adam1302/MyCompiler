package codeAnalysis

import codeAnalysis.binding.BoundBinaryExpressionNode
import codeAnalysis.binding.BoundBinaryOperatorKind
import codeAnalysis.binding.BoundExpressionNode
import codeAnalysis.binding.BoundLiteralExpressionNode
import codeAnalysis.binding.BoundUnaryExpressionNode
import codeAnalysis.binding.BoundUnaryOperatorKind
import java.lang.Exception

internal class Evaluator(val root: BoundExpressionNode) {
    fun evaluate(): Int = evaluateExpression(root)

    private fun evaluateExpression(node: BoundExpressionNode) : Int {
        when (node) {
            is BoundLiteralExpressionNode ->
                return node.value as Int
            is BoundUnaryExpressionNode -> {
                val expr = evaluateExpression(node.operand)
                return when (node.operatorKind) {
                    BoundUnaryOperatorKind.IDENTITY -> expr
                    BoundUnaryOperatorKind.NEGATION -> -expr
                    else -> throw Exception("Unexpected unary operator ${node.operatorKind}")
                }
            }
            is BoundBinaryExpressionNode -> {
                val left = evaluateExpression(node.left)
                val right = evaluateExpression(node.right)

                return when (node.operator) {
                    BoundBinaryOperatorKind.ADDITION -> left + right
                    BoundBinaryOperatorKind.SUBTRACTION -> left - right
                    BoundBinaryOperatorKind.MULTIPLICATION -> left * right
                    BoundBinaryOperatorKind.DIVISION -> left / right
                    else -> throw Exception("Unexpected binary operator ${node.operator}")
                }
            }
            else -> throw Exception("Unexpected node <${node.kind}>")
        }
    }
}

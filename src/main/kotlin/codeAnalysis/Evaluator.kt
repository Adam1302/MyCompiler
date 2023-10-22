package codeAnalysis

import codeAnalysis.binding.BoundBinaryExpressionNode
import codeAnalysis.binding.BoundBinaryOperatorKind
import codeAnalysis.binding.BoundExpressionNode
import codeAnalysis.binding.BoundLiteralExpressionNode
import codeAnalysis.binding.BoundUnaryExpressionNode
import codeAnalysis.binding.BoundUnaryOperatorKind
import java.lang.Exception

internal class Evaluator(val root: BoundExpressionNode) {
    fun evaluate(): Any = evaluateExpression(root)

    private fun evaluateExpression(node: BoundExpressionNode) : Any {
        when (node) {
            is BoundLiteralExpressionNode ->
                return node.value
            is BoundUnaryExpressionNode -> {
                val expr = evaluateExpression(node.operand)
                return when (node.operatorKind) {
                    BoundUnaryOperatorKind.IDENTITY -> expr as Int
                    BoundUnaryOperatorKind.NEGATION -> -(expr as Int)
                    BoundUnaryOperatorKind.LOGICAL_NEGATION -> (expr as Boolean).not()
                    else -> throw Exception("Unexpected unary operator ${node.operatorKind}")
                }
            }
            is BoundBinaryExpressionNode -> {
                val left = evaluateExpression(node.left)
                val right = evaluateExpression(node.right)

                return when (node.operator) {
                    BoundBinaryOperatorKind.ADDITION -> (left as Int) + (right as Int)
                    BoundBinaryOperatorKind.SUBTRACTION -> (left as Int) - (right as Int)
                    BoundBinaryOperatorKind.MULTIPLICATION -> (left as Int) * (right as Int)
                    BoundBinaryOperatorKind.DIVISION -> (left as Int) / (right as Int)
                    BoundBinaryOperatorKind.LOGICAL_AND -> (left as Boolean) && (right as Boolean)
                    BoundBinaryOperatorKind.LOGICAL_OR -> (left as Boolean) || (right as Boolean)
                    else -> throw Exception("Unexpected binary operator ${node.operator}")
                }
            }
            else -> throw Exception("Unexpected node <${node.kind}>")
        }
    }
}

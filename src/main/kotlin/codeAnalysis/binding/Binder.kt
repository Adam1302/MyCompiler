package codeAnalysis.binding

import codeAnalysis.syntax.*
import utils.VarType

/*
For now, the binder takes the syntax tree and creates a more generalized tree for the evaluator
It's like CST -> AST, or parse tree -> syntax tree
 */

internal class Binder {
    private val localDiagnostics: MutableList<String> = mutableListOf<String>()
    val diagnostics: List<String>
        get() = localDiagnostics

    fun bindExpression(syntax: ExpressionSyntaxNode): BoundExpressionNode {
        return when (syntax.kind) {
            TokenType.LITERAL_EXPRESSION -> bindLiteralExpression(
                syntax as LiteralExpressionSyntaxNode
            )
            TokenType.UNARY_EXPRESSION -> bindUnaryExpression(
                syntax as UnaryExpressionSyntaxNode
            )
            TokenType.BINARY_EXPRESSION -> bindBinaryExpression(
                syntax as BinaryExpressionSyntaxNode
            )
            else -> throw Exception("Unexpected syntax ${syntax.kind}")
        }
    }

    private fun bindLiteralExpression(syntax: LiteralExpressionSyntaxNode)
    : BoundLiteralExpressionNode {
        val value = syntax.literalToken.value as Int? ?: 0 // currently we can only handle integers
        return BoundLiteralExpressionNode(value)
    }

    private fun bindUnaryExpression(syntax: UnaryExpressionSyntaxNode)
    : BoundExpressionNode {
        val boundOperand = bindExpression(syntax.expSyntaxNode)
        val boundOperandKind = bindUnaryOperatorKind(syntax.operatorToken.kind, boundOperand.type)

        if (boundOperandKind == null) {
            localDiagnostics.add(
                "Unary operator <${syntax.operatorToken.text}> is not defined for type ${boundOperand.type}"
            )
            return boundOperand
        }

        return BoundUnaryExpressionNode(
            boundOperandKind, boundOperand
        )
    }

    private fun bindBinaryExpression(syntax: BinaryExpressionSyntaxNode): BoundExpressionNode {
        val boundLeft = bindExpression(syntax.leftExpSyntaxNode)
        val boundRight = bindExpression(syntax.rightExpSyntaxNode)
        val boundOperatorKind = bindBinaryOperatorKind(
            syntax.operatorToken.kind,
            boundLeft.type,
            boundRight.type
        )

        if (boundOperatorKind == null) {
            localDiagnostics.add(
                "Binary operator <${syntax.operatorToken.text}> is not defined " +
                        "for types ${boundLeft.type} and ${boundRight.type}"
            )
            return boundLeft
        }

        return BoundBinaryExpressionNode(
            boundLeft,
            boundOperatorKind,
            boundRight
        )
    }

    private fun bindUnaryOperatorKind(kind: TokenType, operandType: VarType)
    : BoundUnaryOperatorKind? {
        if (operandType != VarType.INT) return null

        return when (kind) {
            TokenType.PLUS -> BoundUnaryOperatorKind.IDENTITY
            TokenType.MINUS -> BoundUnaryOperatorKind.NEGATION
            else -> throw Exception("Unexpected unary operator <${kind}>")
        }
    }

    private fun bindBinaryOperatorKind(
        kind: TokenType, leftType: VarType, rightType: VarType
    ): BoundBinaryOperatorKind? {
        if (leftType != VarType.INT || rightType != VarType.INT) {
            return null
        }

        return when (kind) {
            TokenType.PLUS -> BoundBinaryOperatorKind.ADDITION
            TokenType.MINUS -> BoundBinaryOperatorKind.SUBTRACTION
            TokenType.TIMES -> BoundBinaryOperatorKind.MULTIPLICATION
            TokenType.SLASH -> BoundBinaryOperatorKind.DIVISION
            else -> throw Exception("Unexpected binary operator <${kind}>")
        }
    }
}

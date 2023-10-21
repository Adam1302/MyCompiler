package codeAnalysis.binding

import codeAnalysis.syntax.TokenType
import utils.VarType

internal class BoundUnaryExpressionNode(
    val operatorKind: BoundUnaryOperatorKind,
    val operand: BoundExpressionNode,
) : BoundExpressionNode() {
    override val kind: BoundNodeKind = BoundNodeKind.UNARY_EXPRESSION
    override val type: VarType = operand.type
}

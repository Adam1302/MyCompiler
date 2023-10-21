package codeAnalysis.binding

import utils.VarType

internal class BoundBinaryExpressionNode(
    val left: BoundExpressionNode,
    val operator: BoundBinaryOperatorKind,
    val right: BoundExpressionNode
): BoundExpressionNode() {
    override val kind: BoundNodeKind = BoundNodeKind.UNARY_EXPRESSION
    override val type: VarType = left.type
}
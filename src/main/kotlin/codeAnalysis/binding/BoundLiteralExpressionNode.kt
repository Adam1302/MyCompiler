package codeAnalysis.binding

import utils.VarType
import utils.varTypesMap

internal class BoundLiteralExpressionNode(
    val value: Any
) : BoundExpressionNode() {
    override val kind: BoundNodeKind = BoundNodeKind.UNARY_EXPRESSION
    override val type: VarType = varTypesMap[value::class.simpleName] ?: VarType.ERR_NO_TYPE
}

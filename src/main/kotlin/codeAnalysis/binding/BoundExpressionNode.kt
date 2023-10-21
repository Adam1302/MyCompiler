package codeAnalysis.binding

import utils.VarType

internal abstract class BoundExpressionNode : BoundNode() {
    abstract val type: VarType
}

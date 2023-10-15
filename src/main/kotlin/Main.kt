import codeAnalysis.AbstractSyntaxTree
import codeAnalysis.Evaluator
import codeAnalysis.SyntaxNode
import codeAnalysis.SyntaxToken

// Everything after this is in the colour
val red = "\u001b[31m"
val green = "\u001b[32m"
val yellow = "\u001b[33m"
val blue = "\u001b[34m"
val cyan = "\u001b[36m"
val white = "\u001b[37m"
// Resets previous color codes
val reset = "\u001b[0m"

fun main(args: Array<String>) {
    var showTree: Boolean = false
    while (true) {
        print("> ")

        val line: String = readlnOrNull()?: ""
        if (line.isBlank()) break

        if (line == "#showTree") {
            showTree = !showTree
            println(
                if (showTree) "Showing parse trees" else "Not showing parse trees"
            )
            continue
        }

        val abstractSyntaxTree = AbstractSyntaxTree.parse(line)

        if (showTree)
            prettyPrint(abstractSyntaxTree.root)

        if (abstractSyntaxTree.diagnostics.isNotEmpty()) {
            print(red)
            println("ERRORS:")
            for (diagnostic in abstractSyntaxTree.diagnostics) {
                println(diagnostic)
            }
            print(reset)
        } else {
            val evaluator = Evaluator(abstractSyntaxTree.root)
            val result = evaluator.evaluate()
            println(result)
        }
    }
}

fun prettyPrint(syntaxNode: SyntaxNode, indent: String = "", isLast: Boolean = true) {
    print(cyan) // COLOURING
    val indicator = if (isLast) "└──" else "├──"

    print(indent + indicator + syntaxNode.kind)
    if (syntaxNode is SyntaxToken && (syntaxNode as SyntaxToken).value != null) {
        print(" " + (syntaxNode as SyntaxToken).value)
    }
    println()

    val localIndent = if (isLast) "$indent     " else "${indent}|    "

    for (child in syntaxNode.getChildren())
        prettyPrint(child, localIndent, child == syntaxNode.getChildren().last())

    print(reset)
}





import arithmeticSyntax.SyntaxTree
import arithmeticSyntax.Evaluator
import arithmeticSyntax.SyntaxNode
import arithmeticSyntax.SyntaxToken

// Everything after this is in the colour
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val BLUE = "\u001b[34m"
const val CYAN = "\u001b[36m"
const val WHITE = "\u001b[37m"
// Resets previous color codes
const val RESET = "\u001b[0m"

fun main(args: Array<String>) {
    var showTree = false
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

        val syntaxTree = SyntaxTree.parse(line)

        if (showTree)
            prettyPrint(syntaxTree.root)

        if (syntaxTree.diagnostics.isNotEmpty()) {
            print(RED)
            println("ERRORS:")
            for (diagnostic in syntaxTree.diagnostics) {
                println(diagnostic)
            }
            print(RESET)
        } else {
            val evaluator = Evaluator(syntaxTree.root)
            val result = evaluator.evaluate()
            println(result)
        }
    }
}

fun prettyPrint(syntaxNode: SyntaxNode, indent: String = "", isLast: Boolean = true) {
    print(CYAN) // COLOURING
    val indicator = if (isLast) "└──" else "├──"

    print(indent + indicator + syntaxNode.kind)
    if (syntaxNode is SyntaxToken && syntaxNode.value != null) {
        print(" " + syntaxNode.value)
    }
    println()

    val localIndent = if (isLast) "$indent     " else "${indent}|    "

    for (child in syntaxNode.getChildren())
        prettyPrint(child, localIndent, child == syntaxNode.getChildren().last())

    print(RESET)
}





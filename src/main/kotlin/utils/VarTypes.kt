package utils

enum class VarType {
    STRING, INT, DOUBLE, CHAR,
    ERR_NO_TYPE
}

val varTypesMap = mapOf<String, VarType>(
    Pair("String", VarType.STRING),
    Pair("Int", VarType.INT),
    Pair("Double", VarType.DOUBLE),
    Pair("Char", VarType.CHAR)
)

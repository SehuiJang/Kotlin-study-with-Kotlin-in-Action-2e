val kotlinLogo =
    """
    | //
    |//
    |/ \
    """.trimIndent()

val kotlinLogo2 =
    """
    | //
    |//
    |/ \
    """ //.trimIndent()

fun main() {
    println(kotlinLogo)
    // | //
    // |//
    // |/ \
    println(kotlinLogo2)
    // 
    //     | //
    //     |//
    //     |/ \
    // 

    run {
        val think1 = """Hmm \uD83E\uDD14"""
        println(think1)
        // Hmm \uD83E\uDD14

        val think2 = """Hmm ${"\uD83E\uDD14"}"""
        println(think2)
        // Hmm 🤔

        val tmp = "\uD83E\uDD14"
        val think3 = """Hmm ${tmp}"""    // equivalent to `think2`
        println(think3)
        // Hmm 🤔
    }

    run{
        // val text1 = """The variable is \$name""" // error: unresolved reference
        // println(text1)

        val text2 = """The variable is ${'$'}name"""
        println(text2)
        // The variable is $name

        val tmp = '$'
        val text3 = """The variable is ${tmp}name"""    // equivalent to `text2`
        println(text3)
        // The variable is $name
    }    
}
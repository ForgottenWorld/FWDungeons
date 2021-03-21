package it.forgottenworld.dungeons.core.scripting

object ScriptingUtils {

    private object Patterns {
        val WHITESPACE_NOT_IN_QUOTES = """\s+(?=([^"]*"[^"]*")*[^"]*$)""".toRegex()
    }

    fun removeNewLines(hay: String) = hay
        .replace("\n", "")
        .replace("\r", "")

    fun removeWhitespaceNotInQuotes(hay: String) = hay
        .replace(Patterns.WHITESPACE_NOT_IN_QUOTES, "")

    fun eatSemicolon(code: CharIterator) {
        if (code.next() != ';') throw ScriptingException("Expected ;")
    }

    private fun parseBrackets(hay: CharIterator, open: Char, close: Char): CharIterator {
        if (hay.next() != open) throw ScriptingException("Expected $open")
        var nested = 0
        val parsed = StringBuilder()
        while (hay.hasNext()) {
            val c = hay.next()
            if (c == open) {
                ++nested
            } else if (c == close) {
                if (nested == 0) break else --nested
            }
            parsed.append(c)
        }
        return parsed.iterator()
    }

    fun parseBlock(hay: CharIterator) = parseBrackets(hay, '{', '}')

    fun parseArguments(hay: CharIterator): Array<String> {
        val inner = parseBrackets(hay, '(', ')')
        var nested = 0
        var parsed = StringBuilder()
        val result = mutableListOf<String>()
        while (inner.hasNext()) {
            val c = inner.next()
            if (c == '(') {
                ++nested
            } else if (c == ')') {
                if (nested == 0) throw ScriptingException("Unexpected )")
                --nested
            } else if (c == ',') {
                result.add(parsed.toString())
                parsed = StringBuilder()
                continue
            }
            parsed.append(c)
        }
        return result.toTypedArray()
    }

    fun findKeyword(hay: CharIterator, vararg keywords: String): String? {
        val remaining = keywords.toMutableList()
        var parsed = ""
        while (hay.hasNext()) {
            val c = hay.next()
            parsed += c
            remaining.removeIf { !it.startsWith(parsed) }
            if (remaining.isEmpty()) throw ScriptingException("Invalid keyword")
            if (remaining.size == 1) {
                val need = remaining[0].drop(parsed.length).toMutableList()
                while (true) {
                    if (need.isEmpty()) return remaining[0]
                    if (hay.next() != need.removeFirst()) throw ScriptingException("Invalid keyword")
                }
            }
        }
        return null
    }
}
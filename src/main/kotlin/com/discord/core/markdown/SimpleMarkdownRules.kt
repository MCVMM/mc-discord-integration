/**
 * Edited 2020 Uga
 */

package com.discord.core.markdown

import com.discord.core.node.Node
import com.discord.core.node.StyleNode
import com.discord.core.node.TextNode
import com.discord.core.parser.ParseSpec
import com.discord.core.parser.Parser
import com.discord.core.parser.Rule
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object SimpleMarkdownRules {

  private val PATTERN_BOLD = Pattern.compile("^\\*\\*([\\s\\S]+?)\\*\\*(?!\\*)")
  private val PATTERN_UNDERLINE = Pattern.compile("^__([\\s\\S]+?)__(?!_)")
  private val PATTERN_STRIKETHRU = Pattern.compile("^~~(?=\\S)([\\s\\S]*?\\S)~~")
  private val PATTERN_NEWLINE = Pattern.compile("""^(?:\n *)*\n""")
  private val PATTERN_TEXT = Pattern.compile("^[\\s\\S]+?(?=[^0-9A-Za-z\\s\\u00c0-\\uffff]|\\n| {2,}\\n|\\w+:\\S|$)")
  private val PATTERN_ESCAPE = Pattern.compile("^\\\\([^0-9A-Za-z\\s])")

  private val PATTERN_ITALICS = Pattern.compile(
      // only match _s surrounding words.
      "^\\b_" + "((?:__|\\\\[\\s\\S]|[^\\\\_])+?)_" + "\\b" +
          "|" +
          // Or match *s that are followed by a non-space:
          "^\\*(?=\\S)(" +
          // Match any of:
          //  - `**`: so that bolds inside italics don't close the
          // italics
          //  - whitespace
          //  - non-whitespace, non-* characters
          "(?:\\*\\*|\\s+(?:[^*\\s]|\\*\\*)|[^\\s*])+?" +
          // followed by a non-space, non-* then *
          ")\\*(?!\\*)"
  )

  fun <F, S> createBoldRule(): Rule<F, Node<F>, S> =
      createSimpleStyleRule(PATTERN_BOLD, { Style.EMPTY.withBold(true) })

  fun <F, S> createUnderlineRule(): Rule<F, Node<F>, S> =
      createSimpleStyleRule(PATTERN_UNDERLINE, { Style.EMPTY.withFormatting(Formatting.UNDERLINE) })

  fun <F, S> createStrikethruRule(): Rule<F, Node<F>, S> =
      createSimpleStyleRule(PATTERN_STRIKETHRU, { Style.EMPTY.withFormatting(Formatting.STRIKETHROUGH) })

  fun <F, S> createTextRule(): Rule<F, Node<F>, S> {
    return object : Rule<F, Node<F>, S>(PATTERN_TEXT) {
      override fun parse(matcher: Matcher, parser: Parser<F, in Node<F>, S>, state: S): ParseSpec<F, Node<F>, S> {
        val node = TextNode<F>(matcher.group())
        return ParseSpec.createTerminal(node, state)
      }
    }
  }
  fun <F, S> createNewlineRule(): Rule<F, Node<F>, S> {
    return object : Rule.BlockRule<F, Node<F>, S>(PATTERN_NEWLINE) {
      override fun parse(matcher: Matcher, parser: Parser<F, in Node<F>, S>, state: S): ParseSpec<F, Node<F>, S> {
        val node = TextNode<F>("\n")
        return ParseSpec.createTerminal(node, state)
      }
    }
  }

  fun <F, S> createEscapeRule(): Rule<F, Node<F>, S> {
    return object : Rule<F, Node<F>, S>(PATTERN_ESCAPE) {
      override fun parse(matcher: Matcher, parser: Parser<F, in Node<F>, S>, state: S): ParseSpec<F, Node<F>, S> {
        return ParseSpec.createTerminal(TextNode(matcher.group(1)), state)
      }
    }
  }

  fun <F, S> createItalicsRule(): Rule<F, Node<F>, S> {
    return object : Rule<F, Node<F>, S>(PATTERN_ITALICS) {
      override fun parse(matcher: Matcher, parser: Parser<F, in Node<F>, S>, state: S): ParseSpec<F, Node<F>, S> {
        val startIndex: Int
        val endIndex: Int
        val asteriskMatch = matcher.group(2)
        if (asteriskMatch != null && asteriskMatch.length > 0) {
          startIndex = matcher.start(2)
          endIndex = matcher.end(2)
        } else {
          startIndex = matcher.start(1)
          endIndex = matcher.end(1)
        }

        val style = Style.EMPTY.withItalic(true)

        val node = StyleNode<F, Style>(style)
        return ParseSpec.createNonterminal(node, state, startIndex, endIndex)
      }
    }
  }

  @JvmOverloads @JvmStatic
  fun <F, S> createSimpleMarkdownRules(includeTextRule: Boolean = true): MutableList<Rule<F, Node<F>, S>> {
    val rules = ArrayList<Rule<F, Node<F>, S>>()
    rules.add(createEscapeRule())
    rules.add(createNewlineRule())
    rules.add(createBoldRule())
    rules.add(createUnderlineRule())
    rules.add(createItalicsRule())
    rules.add(createStrikethruRule())
    if (includeTextRule) {
      rules.add(createTextRule())
    }
    return rules
  }

  @JvmStatic
  fun <F, S> createSimpleStyleRule(pattern: Pattern, styleFactory: () -> Style) =
      object : Rule<F, Node<F>, S>(pattern) {
        override fun parse(matcher: Matcher, parser: Parser<F, in Node<F>, S>, state: S): ParseSpec<F, Node<F>, S> {
          val style = styleFactory()
          val node = StyleNode<F, Style>(style)
          return ParseSpec.createNonterminal(node, state, matcher.start(1), matcher.end(1))
        }
      }
}


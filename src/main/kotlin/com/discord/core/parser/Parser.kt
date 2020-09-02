/**
 * Edited 2020 Uga
 */

package com.discord.core.parser

import com.discord.core.node.Node
import java.util.*


/**
 * @param F The format context, can be any object that holds what's required for formatting. See [Node.render]
 * @param T The type of nodes that are handled.
 */
open class Parser<F, T : Node<F>, S> @JvmOverloads constructor(private val enableDebugging: Boolean = false) {

  private val rules = ArrayList<Rule<F, out T, S>>()

  fun <C : T> addRule(rule: Rule<F, C, S>): Parser<F, T, S> {
    rules.add(rule)
    return this
  }

  fun <C: T> addRules(rules: Collection<Rule<F, C, S>>): Parser<F, T, S> {
    for (rule in rules) {
      addRule(rule)
    }
    return this
  }

  /**
   * Transforms the [source] to a AST of [Node]s using the provided [rules].
   *
   * @param rules Ordered [List] of rules to use to convert the source to nodes.
   *    If not set, the parser will use its global list of [Parser.rules].
   *
   * @throws ParseException for certain specific error flows.
   */
  @JvmOverloads
  fun parse(source: CharSequence?, initialState: S, rules: List<Rule<F, out T, S>> = this.rules): MutableList<T> {
    val remainingParses = Stack<ParseSpec<F, out T, S>>()
    val topLevelNodes = ArrayList<T>()

    var lastCapture: String? = null

    if (source != null && !source.isEmpty()) {
      remainingParses.add(ParseSpec(null, initialState, 0, source.length))
    }

    while (!remainingParses.isEmpty()) {
      val builder = remainingParses.pop()

      if (builder.startIndex >= builder.endIndex) {
        break
      }

      val inspectionSource = source?.subSequence(builder.startIndex, builder.endIndex) ?: continue
      val offset = builder.startIndex

      var foundRule = false
      for (rule in rules) {
        val matcher = rule.match(inspectionSource, lastCapture, builder.state)
        if (matcher != null) {
          val matcherSourceEnd = matcher.end() + offset
          foundRule = true

          val newBuilder = rule.parse(matcher, this, builder.state)
          val parent = builder.root

          newBuilder.root?.let {
            parent?.addChild(it) ?: topLevelNodes.add(it)
          }

          // In case the last match didn't consume the rest of the source for this subtree,
          // make sure the rest of the source is consumed.
          if (matcherSourceEnd != builder.endIndex) {
            remainingParses.push(ParseSpec.createNonterminal(parent, builder.state, matcherSourceEnd, builder.endIndex))
          }

          // We want to speak in terms of indices within the source string,
          // but the Rules only see the matchers in the context of the substring
          // being examined. Adding this offset addresses that issue.
          if (!newBuilder.isTerminal) {
            newBuilder.applyOffset(offset)
            remainingParses.push(newBuilder)
          }

          try {
            lastCapture = matcher.group(0)
          } catch (throwable: Throwable) {
            throw ParseException(message = "matcher found no matches", source = source, cause = throwable)
          }
//          println("source: $inspectionSource -- depth: ${remainingParses.size}")

          break
        }
      }

      if (!foundRule) {
        throw ParseException("failed to find rule to match source", source)
      }
    }

    return topLevelNodes
  }

  companion object {

    private const val TAG = "Parser"
  }

  class ParseException(message: String, source: CharSequence?, cause: Throwable? = null)
    : RuntimeException("Error while parsing: $message \n Source: $source", cause)
}

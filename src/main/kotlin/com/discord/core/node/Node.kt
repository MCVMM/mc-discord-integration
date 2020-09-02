/**
 * Edited 2020 Uga
 */

package com.discord.core.node

import net.minecraft.text.LiteralText

/**
 * Represents a single node in an Abstract Syntax Tree. It can (but does not need to) have children.
 * @param F The format context, can be any object that holds what's required for formatting. See [format].
 */
abstract class Node<F> {

  private var children: MutableCollection<Node<F>>? = null

  fun getChildren(): Collection<Node<F>>? = children

  fun hasChildren(): Boolean = children?.isNotEmpty() == true

  fun addChild(child: Node<F>) {
    children = (children ?: ArrayList()).apply {
      add(child)
    }
  }

  abstract fun format(formattingContext: F): LiteralText
}

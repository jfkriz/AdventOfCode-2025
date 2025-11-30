@file:Suppress("unused")

package util.collections

import java.util.Stack

/**
 * Pop N items from the stack. Note that the items returned will be in the same order that they were in the stack. This is meant
 * to be used in conjunction with [pushN], to allow one to easily "lift" N items from one stack and "drop" them on another
 * stack, retaining the order
 */
fun <T> Stack<T>.popN(num: Int) = (0 until num).map { this.pop() }

/**
 * Push the list of items onto the stack. Note that the items will be individually pushed in reverse order, to maintain
 * the order in the stack that they appeared in the list. This is meant to be used in conjunction with [popN], to allow
 * one to easily "lift" N items from one stack and "drop" them on another stack, retaining the order
 */
fun <T> Stack<T>.pushN(items: List<T>): List<T> {
    items.reversed().forEach {
        this.push(it)
    }
    return items
}

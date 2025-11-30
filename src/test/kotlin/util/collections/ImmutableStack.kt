package util.collections

@Suppress("unused")
data class ImmutableStack<T>(private val elements: List<T> = emptyList()) {
    fun push(element: T): ImmutableStack<T> = ImmutableStack(elements + element)

    fun pop(): ImmutableStack<T> {
        require(isNotEmpty()) { "Cannot pop from an empty stack" }
        return ImmutableStack(elements.dropLast(1))
    }

    fun peek(): T {
        require(isNotEmpty()) { "Cannot peek an empty stack" }
        return elements.last()
    }

    fun isNotEmpty(): Boolean = elements.isNotEmpty()

    fun isEmpty(): Boolean = elements.isEmpty()

    override fun toString(): String = elements.reversed().joinToString(",")
}

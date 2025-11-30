package util

import java.util.stream.Collectors

interface DataFiles {
    fun loadInput(): List<String> = loadFile("input.txt")

    fun loadSampleInput(): List<String> = loadFile("test-input.txt")

    fun loadOtherInput(fileName: String): List<String> = loadFile(fileName)

    private fun loadFile(fileName: String) =
        javaClass.classLoader.getResourceAsStream("${javaClass.name.split('.')[0]}/$fileName")?.bufferedReader()
            ?.lines()?.collect(
                Collectors.toList(),
            )
            ?: throw IllegalStateException("Can't load data file $fileName")
}

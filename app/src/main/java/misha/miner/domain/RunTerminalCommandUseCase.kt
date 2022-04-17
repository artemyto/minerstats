package misha.miner.domain

import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

class RunTerminalCommandUseCase @Inject constructor() {

    fun execute(command: List<String>): String {

        val pb = ProcessBuilder(command)

        val sb = StringBuilder()

        var process: Process? = null
        try {
            process = pb.start()
        } catch (e: IOException) {
            sb.append("Couldn't start the process.")
            sb.append(e.printStackTrace())
        }
        val out = process?.inputStream

        out?.let {
            val buffer = ByteArray(4000)
            while (out.available() > 0 || process.isStillAlive()) {

                val no: Int = out.available()
                if (no > 0) {
                    val n: Int = out.read(buffer, 0, min(no, buffer.size))
                    sb.append(String(buffer, 0, n))
                }

                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                }
            }
        }

        return sb.toString()
    }
}

private fun Process?.isStillAlive(): Boolean {
    return this != null && try {
        this.exitValue()
        false
    } catch (e: IllegalThreadStateException) {
        true
    }
}

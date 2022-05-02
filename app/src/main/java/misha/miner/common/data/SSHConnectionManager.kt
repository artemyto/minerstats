package misha.miner.common.data

import com.jcraft.jsch.*
import java.io.IOException
import java.io.InputStream
import java.io.PrintStream
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*

class SSHConnectionManager() {
    private var session: Session? = null
    private var username = "user"
    private var password = "password"
    private var hostname = "myhost"
    private var port = 22

    private fun setup(hostname: String, port: Int, username: String, password: String) {
        this.hostname = hostname
        this.port = port
        this.username = username
        this.password = password
    }

    @JvmOverloads
    @Throws(JSchException::class)
    fun open(
        hostname: String = this.hostname,
        port: Int = this.port,
        username: String = this.username,
        password: String = this.password
    ) {

        setup(hostname, port, username, password)

        val jSch = JSch()
        session = jSch.getSession(username, hostname, port)
        session?.let {
            val config = Properties()
            config["StrictHostKeyChecking"] = "no" // not recommended
            it.setConfig(config)
            it.setPassword(password)
            it.connect()
        }
    }

    @Throws(JSchException::class, IOException::class)
    fun runCommand(command: String?): String {
        var ret = ""
        session?.let { session ->
            if (!session.isConnected) throw RuntimeException("Not connected to an open session.  Call open() first!")

            val sendPassword = command.toString().contains("sudo")

            val cmd =
                if (sendPassword)
                    command.toString().replace("sudo", "sudo -S -p ''")
                else command

            val channel = session.openChannel("exec") as ChannelExec
            channel.setCommand(cmd)
            channel.inputStream = null

            val iStr: InputStream = channel.inputStream
            channel.connect()

            if (sendPassword) {
                val out = PrintStream(channel.outputStream)
                out.println(password)
                out.flush()
            }

            ret = getChannelOutput(channel, iStr)
            channel.disconnect()
        }
        return ret
    }

    @Throws(IOException::class)
    private fun getChannelOutput(channel: Channel, iStr: InputStream): String {
        val buffer = ByteArray(1024)
        val strBuilder = StringBuilder()
        val line = ""
        while (true) {
            while (iStr.available() > 0) {
                val i: Int = iStr.read(buffer, 0, 1024)
                if (i < 0) {
                    break
                }
                strBuilder.append(String(buffer, 0, i))
                println(line)
            }
            if (line.contains("logout")) {
                break
            }
            if (channel.isClosed) {
                break
            }
            try {
                Thread.sleep(1000)
            } catch (ee: Exception) {
            }
        }
        return strBuilder.toString()
    }

    fun close() {
        session?.disconnect()
    }
}

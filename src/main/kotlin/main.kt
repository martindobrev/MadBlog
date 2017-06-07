import org.w3c.dom.HTMLButtonElement
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window

/**
 * A simple kotlin test spin - check out if it's capable of doing some stuff
 *
 *
 * Created by martindobrev on 23.05.17.
 */
fun main(args: Array<String>) {
    val message = "Hello JavaScript!"
    println(message)

    val button = document.getElementById("clickme") as HTMLButtonElement

    button.addEventListener("click", { document.body?.append("TEST")})


    val location = window.location
    println("Host: " + location.host)
    println("Pathname: " + location.pathname)
    println("Origin: " + location.origin)


    val xhttpRequest = XMLHttpRequest()
}
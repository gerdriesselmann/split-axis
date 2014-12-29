import scala.io._
import java.io.FileWriter
import java.io.File

object Main {
  def main(args : Array[String]) : Unit = {
    // Directory where Axis generated files are, including slash at the end
    val in_dir = "C:/your/directory/"
    // Directory where to put extracted class files, including slash at the end
    val out_dir = in_dir + "extracted/"
    // Class name prefix fpr Stub And CallbackHandler classes
    val classname_prefix = "ChangeThis"
    // Class name of Axis generated stub and callback handler class
    val classname_stub = classname_prefix + "Stub"
    val classname_callback = classname_prefix + "CallbackHandler"
    // Namespace of Axis generated stub class
    val namespace = "your.name.space"

    var buffer_out : FileWriter = null
    // Everything that is not extracted is kept in stub file
    val buffer_main = new FileWriter(out_dir + classname_stub + ".java")
    val in = in_dir + classname_stub + ".java"
    val namespace_stub = namespace + "." + classname_stub + "."

    var nextmatch = 'c'
    for( l <- Source.fromPath(in).getLines("n")) {
      var line = l.replace(namespace_stub, "")
      nextmatch match {
        // Look for next static class declaration (This is the starting point of next snippet)
        case 'c' =>
          if (line.contains("public static class ") 
               && !line.contains("Factory") 
               && !line.contains("ExtensionMapper")) {
            var classname = line.replace("public static class", "")
            classname = classname.trim
            classname = classname.split(' ')(0)
            classname = classname.replace("{", "")
            buffer_out = new FileWriter(out_dir + classname + ".java")
            buffer_out.write("package " + namespace + ";nn")
            buffer_out.write(line.replace("public static", "public"))
            nextmatch = 'f'
          }
          else {
            // Write to stub class file
            buffer_main.write(line)
          }
        // Look for end of factory class. The curly bracket after this 
        // is the end of class declaration
        case 'f' =>
          if (line.contains("//end of factory class")) nextmatch = 'b'
          // Just copy line
          buffer_out.write(line.replace("ExtensionMapper", classname_stub + ".ExtensionMapper"))
        // Look for closing curly bracket, ending extraction
        case 'b' =>
          buffer_out.write(line)
          if (line.contains("}")) {
            buffer_out.flush
            buffer_out.close
            // Look for next class
            nextmatch = 'c'
          }
      }
    }

    buffer_main.flush
    buffer_main.close

    // Change namespaces in CallbackHandler class
    val callback_file = new File(in_dir + classname_callback + ".java")
    if (callback_file.exists()) {
      val callback_out = new FileWriter(out_dir + classname_callback + ".java")
      for ( l <- Source.fromFile(callback_file).getLines("n")) 
        callback_out.write(l.replace(namespace_stub, ""))

      callback_out.flush
      callback_out.close
    }

    println("Finished")
  }
}

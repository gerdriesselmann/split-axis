split-axis
==========

Split large Axis file in smaller pieces. 

I wrote this to cope with an Axis generated file that was more than 13 MB in size, and contained more than 290.000 lines of code. 

See [my blog entry](http://www.gerd-riesselmann.net/scala/splitting-large-axis-generated-files-separate-classes)

To compile, place it a file, change the variables "in_dir", "classname_prefix", and "namespace", and compile it using the Scala compiler or run it as a script.


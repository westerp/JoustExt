/*
 * Copyright (C) 2014 Lymia Aluysia <lymiahugs@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package moe.lymia.joustext

import java.text.SimpleDateFormat
import java.util.Date

object JoustExt {
  def main(args: Array[String]) {
    if(args.length != 2) {
      println("Usage: [input file] [output file]")
      System.exit(1)
    } else {
      val time = System.currentTimeMillis()
      try {
        if(new java.io.File(args(0)).exists) {
          println("Parsing file...")
          val inputFile = scala.io.Source.fromFile(args(0)).mkString
          val parsed = Parser(inputFile)
          parsed match {
            case Left(ast) =>
              implicit val options = astops.GenerationOptions()

              var currentAst = ast
              for(phase <- phases.phases) {
                println("Running phase "+phase.shortName+" ("+phase.description+")")
                currentAst = phases.runPhase(phase, ast)
              }

              println("Writing output file...")
              val out = new java.io.PrintWriter(args(1))
              val date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
              out.println("Program compiled by JoustExt by Lymia on "+date)
              out.println("Source file name: "+args(0).replace(".", ",").replace("-", "="))
              out.println("")
              out.println(astops.generate(currentAst))
              out.close()

              println("Finished in "+(System.currentTimeMillis()-time)+" ms.")
            case Right(err) =>
              println(err)
              System.exit(1)
          }
        } else {
          println("Input file "+args(0)+" does not exist!")
          System.exit(1)
        }
      } catch {
        case x: Throwable =>
          x.printStackTrace()
          println("An error was encountered: "+x.getClass.getSimpleName+": "+x.getMessage)
          System.exit(1)
      }
    }
  }
}

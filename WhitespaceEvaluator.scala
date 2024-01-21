import scala.util.parsing.combinator.JavaTokenParsers
import scala.collection.mutable.{Stack, Map}
import scala.io.StdIn.{readLine, readInt}

class WhitespaceParser extends JavaTokenParsers {
    override val whiteSpace = """[^ \n\t]+""".r

    var stack = Stack[Int]()
    var heap = Map[Int, Int]().withDefault(_ => 0)
    var subroutines = Map[String, Int]()
    var returnAddress = Stack[Int]()
    var inputBuffer = List[Char]()
    var line = 0
    var pc = 0

    def space = " " // "[Space]"
    def lf = "\n" // "[LF]"
    def tab = "\t" // "[Tab]"

    // Numbers can be any number of bits wide, and are simply represented as a series of [Space] and [Tab], terminated by a [LF].
    // [Space] represents the binary digit 0, [Tab] represents 1.
    // The sign of a number is given by its first character, [Space] for positive and [Tab] for negative.
    // Note that this is not twos complement, it just indicates a sign.
    def number = opt(space ^^ {_ => "+"} | tab ^^ {_ => "-"}) ~ rep(space ^^ {_ => "0"} | tab ^^ {_ => "1"}) <~ lf ^^ 
                {case s ~ n => Integer.parseInt(s.getOrElse("") + (if (n.isEmpty) {"0"} else {n.mkString("")}), 2)}

    // Labels are simply [LF] terminated lists of spaces and tabs.
    // There is only one global namespace so all labels must be unique.
    def label = rep(space | tab) <~ lf ^^ {_.mkString("")}

    def program = rep(command)

    def command = (stackManipulation | arithmetic | heapAccess | flowControl | io) ^^ {c => line += 1; c;}

    def stackManipulation = space ~> (stackPush | stackDuplicateTop | stackCopyToTop | stackSwapTop | stackPop | stackSlide)
    // Push the number onto the stack
    def stackPush = space ~> number ^^ {n => () => stack.push(n)}
    // Duplicate the top item on the stack
    def stackDuplicateTop = lf ~> space ^^ {_ => () => stack.push(stack.top)}
    // Copy the nth item on the stack (given by the argument) onto the top of the stack
    def stackCopyToTop = tab ~> space ~> number ^^ {n => () => stack.push(stack(n))}
    // Swap the top two items on the stack
    def stackSwapTop = lf ~> tab ^^ {_ => () => {val f = stack.pop(); val s = stack.pop(); stack.push(f, s);}}
    // Discard the top item on the stack
    def stackPop = lf ~> lf ^^ {_ => () => stack.pop()}
    // Slide n items off the stack, keeping the top item
    def stackSlide = tab ~> lf ~> number ^^ {n => () => {val top = stack.pop(); stack = stack.slice(n, stack.size).push(top);}}

    // Arithmetic commands operate on the top two items on the stack, and replace them with the result of the operation.
    // The first item pushed is considered to be left of the operator.
    def arithmetic = tab ~> space ~> (addition | subtraction | multiplication | integerDivision | modulo)
    def addition = space ~> space ^^ {_ => () => {val r = stack.pop(); val l = stack.pop(); stack.push(l + r);}}
    def subtraction = space ~> tab ^^ {_ => () => {val r = stack.pop(); val l = stack.pop(); stack.push(l - r);}}
    def multiplication = space ~> lf ^^ {_ => () => {val r = stack.pop(); val l = stack.pop(); stack.push(l * r);}}
    def integerDivision = tab ~> space ^^ {_ => () => {val r = stack.pop(); val l = stack.pop(); stack.push(l / r);}}
    def modulo = tab ~> tab ^^ {_ => () => {val r = stack.pop(); val l = stack.pop(); stack.push(l % r);}}

    // Heap access commands look at the stack to find the address of items to be stored or retrieved.
    def heapAccess = tab ~> tab ~> (heapStore | heapRetrive)
    // To store an item, push the address then the value and run the store command.
    def heapStore = space ^^ {_ => () => {val v = stack.pop(); val k = stack.pop(); heap(k) = v;}}
    // To retrieve an item, push the address and run the retrieve command, which will place the value stored in the location at the top of the stack.
    def heapRetrive = tab ^^ {_ => () => stack.push(heap(stack.pop()))}

    // Flow control operations are also common.
    // Subroutines are marked by labels, as well as the targets of conditional and unconditional jumps, by which loops can be implemented.
    // Programs must be ended by means of [LF][LF][LF] so that the interpreter can exit cleanly. 
    def flowControl = lf ~> (markLocation | callSubroutine | jump | jumpZero | jumpNegative | endSubroutine | endProgram)
    // Mark a location in the program
    def markLocation = space ~> space ~> label ^^ {l => subroutines(l) = line; () => ();}
    // Call a subroutine
    def callSubroutine = space ~> tab ~> label ^^ {l => () => {returnAddress.push(pc); pc = subroutines(l);}}
    // Jump unconditionally to a label
    def jump = space ~> lf ~> label ^^ {l => () => pc = subroutines(l)}
    // Jump to a label if the top of the stack is zero
    def jumpZero = tab ~> space ~> label ^^ {l => () => if (stack.pop() == 0) {pc = subroutines(l)}}
    // Jump to a label if the top of the stack is negative
    def jumpNegative = tab ~> tab ~> label ^^ {l => () => if (stack.pop() < 0) {pc = subroutines(l)}}
    // End a subroutine and transfer control back to the caller
    def endSubroutine = tab ~> lf ^^ {l => () => pc = returnAddress.pop()}
    // End the program
    def endProgram = lf ~> lf ^^ {l => () => pc = -2}

    def io = tab ~> lf ~> (outputChar | outputNumber | inputChar | inputNumber)
    // Output the character at the top of the stack
    def outputChar = space ~> space ^^ {_ => () => print(stack.pop().toChar)}
    // Output the number at the top of the stack
    def outputNumber = space ~> tab ^^ {_ => () => print(stack.pop())}
    // Read a character and place it in the location given by the top of the stack
    def inputChar = tab ~> space ^^ {_ => () => {
        inputBuffer = inputBuffer match {
            case h::t => heap(stack.pop()) = h; t;
            case Nil => val input = (readLine() + "\n").toList; heap(stack.pop()) = input.head; input.tail;
        }}}
    // Read a number and place it in the location given by the top of the stack
    def inputNumber = tab ~> tab ^^ {_ => () => heap(stack.pop()) = readInt()}

    def run(commands:List[() => Any]) = {
        while (pc >= 0 && pc < commands.size) {
            commands(pc)()
            pc += 1
        }
    }

}

object WhitespaceEvaluator {
    def main(args:Array[String]) = {
        args.foreach{file =>
            val p = new WhitespaceParser
            val input = scala.io.Source.fromFile(file).mkString
            p.parseAll(p.program, input) match {
                case p.Success(result,_) => p.run(result)
                case x => println(x.toString)
            }
        }
    }
}

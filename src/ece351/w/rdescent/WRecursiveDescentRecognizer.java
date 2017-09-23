/* *********************************************************************
 * ECE351 
 * Department of Electrical and Computer Engineering 
 * University of Waterloo 
 * Term: Summer 2017 (1175)
 *
 * The base version of this file is the intellectual property of the
 * University of Waterloo. Redistribution is prohibited.
 *
 * By pushing changes to this file I affirm that I am the author of
 * all changes. I affirm that I have complied with the course
 * collaboration policy and have not plagiarized my work. 
 *
 * I understand that redistributing this file might expose me to
 * disciplinary action under UW Policy 71. I understand that Policy 71
 * allows for retroactive modification of my final grade in a course.
 * For example, if I post my solutions to these labs on GitHub after I
 * finish ECE351, and a future student plagiarizes them, then I too
 * could be found guilty of plagiarism. Consequently, my final grade
 * in ECE351 could be retroactively lowered. This might require that I
 * repeat ECE351, which in turn might delay my graduation.
 *
 * https://uwaterloo.ca/secretariat-general-counsel/policies-procedures-guidelines/policy-71
 * 
 * ********************************************************************/

package ece351.w.rdescent;

import ece351.util.Lexer;
import ece351.w.ast.Waveform;

public final class WRecursiveDescentRecognizer {
    private final Lexer lexer;

    public WRecursiveDescentRecognizer(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static void recognize(final String input) {
    	final WRecursiveDescentRecognizer r = new WRecursiveDescentRecognizer(new Lexer(input));
        r.recognize();
    }

    /**
     * Throws an exception to reject.
     */
    public void recognize() {
        program();
    }

    /**
     * What is the termination condition of the loop in program()?
     * Will this condition be met if the waveform() method does nothing?
     */
    public void program() {
        checkWaveform();
        // iterate while the end of file has not been reached
        while (!lexer.inspectEOF()) {
            checkWaveform();
        }
        lexer.consumeEOF();
    }

    public void checkWaveform() {
// TODO: longer code snippet
    	// if it is true then consume id
    	// Can assume that next token is colon excluding the spaces
    	// consume the colon
    	// loop and consume until semicolon has been reached
    	if(!lexer.inspectID()){
    		throw new ece351.util.Todo351Exception();
    	}
    	lexer.consumeID();
        if(!lexer.inspect(":")){
        	// throw exception
        	throw new ece351.util.Todo351Exception();
        }
        lexer.consume(":");
//        System.out.println(lexer.debugState());

        while (!lexer.inspect(";")) {
        	// short notation for accepting 0 or 1
        	if(!lexer.inspect(new String[] { "0", "1" })){
        		throw new ece351.util.Todo351Exception();
        	}
          lexer.consume(new String[] { "0", "1" });
        }
        lexer.consume(";");
    }
}

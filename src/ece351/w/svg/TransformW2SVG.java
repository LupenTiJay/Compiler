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

package ece351.w.svg;

import java.io.PrintWriter;
import java.io.StringWriter;

import ece351.util.CommandLine;
import ece351.util.Debug;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;
import ece351.w.rdescent.WRecursiveDescentParser;

public final class TransformW2SVG {
	public static void main(final CommandLine c) {
		final WProgram wp;
		wp = WRecursiveDescentParser.parse(c.readInputSpec());
		final PrintWriter pw = c.resolveOutputSpec();
		transform(wp,pw);
		pw.flush();
	}

	public static String transform(final WProgram wp) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		transform(wp, out);
		out.close();
		return sw.toString();
	}
	
	public static void transform(final WProgram wp, final PrintWriter out) {
		
		// header
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
		out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100%\" height=\"100%\" version=\"1.1\">");
		out.println("<style type=\"text/css\"><![CDATA[line{stroke:#006600;fill:#00 cc00;} text{font-size:\"large\";font-family:\"sans-serif\"}]]></style>");
		out.println();

		final int WIDTH = 100;
		
		int y_mid = 150;
		int y_prev = 150;
		int y_pos =150;
		final int y_off =50;

		// loop on waveforms
		// this line implicitly uses an iterator
		for (final Waveform w : wp.waveforms) {
			// reset the initial x position
			int x = 50;

			// write out the waveform name
			out.println(Pin.toSVG(w.name, x, y_mid));

			// advance the x position to start drawing
			x=100;

			// loop on bits
			for (final String bit : w.bits) {
				// set the y position according to the value of the bit
				
				switch (bit) {
					case "0":
						// set low bar
						out.println(horizontalLine(x, x+ WIDTH, y_mid));
						if (y_mid != y_prev){
							// draw vertical line 
							out.println(verticleLine(x + WIDTH, y_mid, y_mid + y_off));
							y_prev = y_mid;
						}
						
					case "1":
						out.println(horizontalLine(x, x+ WIDTH, y_mid));
						if (y_mid + y_off != y_prev){
							out.println(verticleLine(x + WIDTH, y_mid, y_mid + y_off));
							y_prev = y_prev + y_off;
						}						
						// set high bar
				}
				x = x + 100;
//// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
			}
			
			// advance the y position for the next pin
			
			y_mid = y_mid + 100;
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();

		}

		// footer
		out.println("</svg>");
		
	}
	
	public static String verticleLine (int x, int y1, int y2){
		return "<line "
				+ "x1=\"" + x + "\" " 
				+ "x2=\"" + x + "\" "
				+ "y1=\"" + y1 + "\" "
				+ "y2=\"" + y2 + "\" "
				+ "/>";
//		return "<line x1="100" x2="100" y1="150" y2="200" />";
	}
	
	public static String horizontalLine(int x1, int x2, int y){
		return "<line "
				+ "x1=\"" + x1 + "\" " 
				+ "x2=\"" + x2 + "\" "
				+ "y1=\"" + y + "\" "
				+ "y2=\"" + y + "\" "
				+ "/>";
	}

	/**
	 * Extra exploration activity (optional).
	 * Try changing the value of this flag and see how it changes the performance
	 * of the test harnesses. Why does that happen? What is the difference
	 * between a DOM-style XML parser and a SAX-style XML parser?
	 */
	public static final boolean USE_DOM_XML_PARSER = true; // TODO: replace this stub

}

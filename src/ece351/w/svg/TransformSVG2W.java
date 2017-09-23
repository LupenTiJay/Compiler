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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.parboiled.common.ImmutableList;

import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;


public final class TransformSVG2W {
	
	/**
	 * Transforms an instance of WSVG to an instance of WProgram.
	 * Write this algorithm in whatever way you wish.
	 * Remember that the AST is immutable.
	 * You might want to build up some mutable temporary structures.
	 * ImmutableList can be used as a "mutable" temporary structure if the 
	 * local variable is not final: just re-assign the local variable to the new list.
	 * 
	 * We used to give more detailed comments on the staff algorithm,
	 * but many students in several offerings of this course found
	 * those comments confusing, and asked for them to be removed.
	 * 
	 * @see #COMPARE_Y_X 
	 * @see #transformLinesToWaveform(List, List)
	 * @see java.util.ArrayList
	 * @see java.util.LinkedHashSet
	 */
	public static final WProgram transform(final WSVG wsvg) {
		final List<Line> lines = new ArrayList<Line>(wsvg.segments);
		final List<Pin> pins = new ArrayList<Pin>(wsvg.pins);
		WProgram program = new WProgram();
		WProgram waveforms = new WProgram();
		
		
//		program.append(transformLinesToWaveform(lines, pins));
		
		//throw new ece351.util.Todo351Exception();
		
		Collections.sort(lines, COMPARE_Y_X);

		
		
		
		do {
			Pin pin;
			List<Line> extractLine = new ArrayList<Line>();
			List <Pin> extractPin = new ArrayList<Pin>();
			if(pins.isEmpty() == false){
				extractPin.add(pins.remove(0));
			}
			
			// y value of pin is mid point
			// all lines that are +/- 50 form midpoint are part of this wave
			
			for(int i = 0; i < lines.size(); i++){
				
				if(Math.abs(lines.get(i).y1 - extractPin.get(0).y) <= 50){
					// line corresponds with pin
					extractLine.add(lines.get(i));
				}
			}
			
			// once done, check the validity
			if(extractLine.isEmpty()){
				// throw exception
			}
			
			program.append(transformLinesToWaveform(extractLine, extractPin));
			
			
		}while(!pins.isEmpty());
		
		
		return program;
	}
	


	/**
	 * Transform a list of Line to an instance of Waveform.
	 * The concept of a y-midpoint might be useful: 1 is a line above; 0 is a line below.
	 * What to do about "dots"?
	 * ImmutableList can be used as a "mutable" temporary structure if the 
	 * local variable is not final: just re-assign the local variable to the new list.
	 * 
	 * We used to give more detailed comments on the staff algorithm,
	 * but many students in several offerings of this course found
	 * those comments confusing, and asked for them to be removed.
	 * 
	 * @see #COMPARE_X
	 * @see #transform(WSVG)
	 * @see Pin#id
	 */
	private static Waveform transformLinesToWaveform(final List<Line> lines, final List<Pin> pins) {
		if(lines.isEmpty()) return null;
		List<Line> linescpy = lines;
		int midPoint = pins.get(0).y;
		Waveform wave;
		

		int size = lines.size();
	
		// implement the comparator given using collections 
		// http://stackoverflow.com/questions/2839137/how-to-use-comparator-in-java-to-sort
		
		Collections.sort(lines, COMPARE_X); 
		
		
		// determine the mid point
		
//		do{
//			
//			// get y value
////			System.out.println(lines.toString());
//			Line line= lines.remove(0);
//			if (line.y1 == line.y2 && line.x1 != line.x2){
//				// horizontal line
//				midPoint += line.y1;
//			}
//			else{
//				// throw exception
//			}
//			 
//			
//		}while(!lines.isEmpty());
//		
//		midPoint = midPoint / size;
		
		
		String waveId = pins.get(0).id;
//		for (int i = 0; i < pins.size(); i++){
//			Pin pin = pins.remove(0);
//			if (Math.abs(pin.y - midPoint) < 100){
//				waveId = pin.id;
//			}
//		}
		
		wave = new Waveform(waveId);
		
		// check if line is greater than mid point or less
		
		do {
			// check if horizontal line
			Line line = linescpy.remove(0);
			
			if (line.y1 == line.y2 && line.x1 != line.x2){
				// horizontal line
				if (line.y1 > midPoint){
					// write 1
					wave.append("1");
				}
				else{
					// write 0
					wave.append("0");
				}
			}
			else if(line.y1 != line.y2 && line.x1 == line.x2){
				// verticle line
			}
			else{
				// throw exception
			}
			
		}while(!linescpy.isEmpty());
		// based on that result determine if bit is 1

		
// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
		

		return wave;
	}

	/**
	 * Sort a list of lines according to their x position.
	 * 
	 * @see java.util.Comparator
	 */
	public final static Comparator<Line> COMPARE_X = new Comparator<Line>() {
		@Override
		public int compare(final Line l1, final Line l2) {
			if(l1.x1 < l2.x1) return -1;
			if(l1.x1 > l2.x1) return 1;
			if(l1.x2 < l2.x2) return -1;
			if(l1.x2 > l2.x2) return 1;
			return 0;
		}
	};

	/**
	 * Sort a list of lines according to their y position first, and then x position second.
	 * 
	 * @see java.util.Comparator
	 */
	public final static Comparator<Line> COMPARE_Y_X = new Comparator<Line>() {
		@Override
		public int compare(final Line l1, final Line l2) {
			final double y_mid1 = (double) (l1.y1 + l1.y2) / 2.0f;
			final double y_mid2 = (double) (l2.y1 + l2.y2) / 2.0f;
			final double x_mid1 = (double) (l1.x1 + l1.x2) / 2.0f;
			final double x_mid2 = (double) (l2.x1 + l2.x2) / 2.0f;
			if (y_mid1 < y_mid2) return -1;
			if (y_mid1 > y_mid2) return 1;
			if (x_mid1 < x_mid2) return -1;
			if (x_mid1 > x_mid2) return 1;
			return 0;
		}
	};

}

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

package ece351.f.parboiled;

import org.parboiled.Rule;

import ece351.common.ast.Constants;
import ece351.util.CommandLine;

//Parboiled requires that this class not be final
public /*final*/ class FParboiledRecognizer extends FBase implements Constants {

	
	public static void main(final String... args) {
		final CommandLine c = new CommandLine(args);
    	process(FParboiledRecognizer.class, c.readInputSpec());
    }

	@Override
	  public Rule Program()
	  {
//		Program → Formula+ $$
	    return Sequence(OneOrMore(Formula()),EOI);
	  }
	  
	  public Rule Formula()
	  {
		// read the ID first. Then clear the white spaces until "<=". Clear white space. Parse expression, clear white space. recogonize ";", Clear white space
	    // Fomula → Var ‘<=’ Expr ‘;’
		  return Sequence(Id(),W0(), "<=",  W0(), Expression(), W0(), Ch(';'), W0() );
	  }
	  
	  public Rule Expression()
	  {
		  // There can be multiple expressions or none. Each expression contains a Term
//		  Expr → Term (‘or’ Term)*
		  return OneOrMore(Term(), ZeroOrMore(Sequence(OR, Term())));
	  }
	  
	  public Rule Term()
	  {
		  // I dont know by adding new Object[0] fixes the problem ????
		  // For term this is how it goes
		  // Keep on calling Factor to eliminate multiple (). From that we get expressions which need to be reduced again
//		  Term → Factor (‘and’ Factor)*
		  return Sequence(Factor(), ZeroOrMore(AND, Factor()));
	  }
	  
	  public Rule Factor()
	  {	
//		  Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
	    return Sequence(W0(), FirstOf(Sequence(NOT,Factor()),Sequence(Ch('('),  W0(),Expression(), W0(), Ch(')')), Id(), Constant()), new Object[0]);
	  }
	  
	  public Rule Constant()
	  {
//		  Constant → ‘‘0’’ | ‘‘1’’
		  return Sequence(W0(), "'", FirstOf("1","0"), "'", W0());
	  }
	  
	  public Rule Id()
	  {
		 return Sequence(Letter(), ZeroOrMore(FirstOf(Letter(), CharRange('0', '9'), '_')));
	  }
	  
	  public Rule Letter()
	  {
	    return FirstOf(CharRange('a', 'z'),CharRange('A', 'Z'));
	  }
	}
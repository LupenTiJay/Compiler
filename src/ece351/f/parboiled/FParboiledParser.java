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
import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Constants;
import ece351.common.ast.Expr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;

// Parboiled requires that this class not be final
public /*final*/ class FParboiledParser extends FBase implements Constants {

	
	public static void main(final String[] args) {
    	final CommandLine c = new CommandLine(args);
    	final String input = c.readInputSpec();
    	final FProgram fprogram = parse(input);
    	assert fprogram.repOk();
    	final String output = fprogram.toString();
    	
    	// if we strip spaces and parens input and output should be the same
    	if (strip(input).equals(strip(output))) {
    		// success: return quietly
    		return;
    	} else {
    		// failure: make a noise
    		System.err.println("parsed value not equal to input:");
    		System.err.println("    " + strip(input));
    		System.err.println("    " + strip(output));
    		System.exit(1);
    	}
    }
	
	private static String strip(final String s) {
		return s.replaceAll("\\s", "").replaceAll("\\(", "").replaceAll("\\)", "");
	}
	
	public static FProgram parse(final String inputText) {
		final FProgram result = (FProgram) process(FParboiledParser.class, inputText).resultValue;
		assert result.repOk();
		return result;
	}

	@Override
	public Rule Program() {
// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
		
		//need to add fpprogram onto the stack
		// This pushes the [FProgram] onto the stack
		//Program → Formula+ $$
	      return Sequence(push(new FProgram()),Sequence(OneOrMore(Formula()),EOI));
	}
		
	  public Rule Formula()
	  {
		// read the ID first. Then clear the white spaces until "<=". Clear white space. Parse expression, clear white space. recogonize ";", Clear white space
		// When Id is called, it becomes [FProgram, Id]
		// When Expression is called it becomes [FProgram, Id, "<=",Expression]
		// The Id and "<=" are grouped as assignment statements. Thus the pop() is string and the second pop is Expression
//	    return Sequence(Id(),W0(), "<=",  W0(), Expression(),swap(),push(new AssignmentStatement((String)pop(), (Expr)pop())), swap(), push(((FProgram)pop()).append(pop())), W0(), Ch(';'), W0() );
	    return Sequence(Id(),W0(), "<=",  W0(), Expression(),swap(),push(new AssignmentStatement((VarExpr)pop(), (Expr)pop())), swap(), push(((FProgram)pop()).append(pop())), W0(), Ch(';'), W0() );

	  }
	  
	  public Rule Expression()
	  {
		  // There can be multiple expressions or none. Each expression contains a Term
		  // Expr -> Term ('or' Term)*
		  return Sequence(Term(), W0(), ZeroOrMore(Sequence(W0(),OR, W0(),Term(), swap(), push(new OrExpr((Expr)pop(), (Expr)pop())))));
	  }
	  
	  public Rule Term()
	  {
		  // I dont know by adding new Object[0] fixes the problem ????
		  // For term this is how it goes
		  // Keep on calling Factor to eliminate multiple (). From that we get expressions which need to be reduced again
//	    return Sequence(Factor(), ZeroOrMore(Sequence(AND, Factor(),swap(),push(new AndExpr((Expr)pop(),(Expr)pop())))));
		return Sequence(Factor(), ZeroOrMore(Sequence(W0(),AND, W0(), Factor(), swap(), push(new AndExpr((Expr)pop(),(Expr)pop())))));
	  }
	  
	  public Rule Factor()
	  {	
		// Factor (‘and’ Factor)*
//		  return Sequence
//		  Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
	    return Sequence(W0(), FirstOf(Sequence(NOT,Factor(),push(new NotExpr((Expr)pop()))),Sequence(Ch('('),  W0(),Expression(), W0(), Ch(')')), Id(), Constant()));
	  }
	  
	  public Rule Constant()
	  {
//		  Constant → ‘‘0’’ | ‘‘1’’
//		  return Sequence(W0(),Optional("0","1"),push(ConstantExpr.make(match())), W0());
//		  return Sequence(W0(), Optional("'0'","'1'"), push(ConstantExpr.make(match())));
		  
		  // The problem seem to be the FirstOf instead of optional
			return Sequence(W0(), "\'", FirstOf("1","0"),push(ConstantExpr.make(match())),"\'");
	  }
	  
	  public Rule Id()
	  {
		  // Forgot the treat the ID as a sequence and appended push to stack in a wrong manner
//		 return Sequence(Letter(), ZeroOrMore(FirstOf(Letter(), CharRange('0', '9'), '_')),push(new VarExpr(match())));
		  return Sequence(Sequence(Letter(), ZeroOrMore(FirstOf(Letter(), CharRange('0', '9')), '_')),push(new VarExpr(match())));


	  }
	  
	  public Rule Letter()
	  {
	    return FirstOf(CharRange('a', 'z'),CharRange('A', 'Z'));
	  }
	
	
}

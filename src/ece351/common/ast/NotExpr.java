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

package ece351.common.ast;

import ece351.common.visitor.ExprVisitor;

public final class NotExpr extends UnaryExpr{
	public NotExpr(Expr argument) {
		super(argument);
	}

	public NotExpr(Object pop) {
		this( (Expr)pop );
	}

	public NotExpr() { this(null); }
	
	@Override
    protected final Expr simplifyOnce() {	
		
		Expr test = expr.simplify();
		
		if(test.getClass().equals(NotExpr.class)){
			// idk what to return
			return ((NotExpr) test).expr;
		}
		if(test.equals(ConstantExpr.TrueExpr)){
			// return true;
			return ConstantExpr.FalseExpr;
		}
		
		if(test.equals(ConstantExpr.FalseExpr)){
//			return false expression
			return ConstantExpr.TrueExpr;
		}
		
		return new NotExpr( test);
		
		// check if the object equals to true expression
		
	
		
    	// simplify our child first
    			// !true = false
    			// !false = true
    		// !!x = x
    		// nothing changed
    		// something changed
//    	return this; // TODO: replace this stub
		
		// create expr obj
		
		//if obj.equals(!obj) return no !
		
		// otherwise do return operator
		
    }
	
    public Expr accept(final ExprVisitor v){
    	return v.visitNot(this);
    }
	
	@Override
	public String operator() {
		return Constants.NOT;
	}
	@Override
	public UnaryExpr newUnaryExpr(final Expr expr) {
		return new NotExpr(expr);
	}

}

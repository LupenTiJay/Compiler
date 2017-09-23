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

package ece351.f.techmapper;

import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kodkod.util.collections.IdentityHashSet;
import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.common.visitor.PostOrderExprVisitor;
import ece351.f.FParser;
import ece351.f.analysis.ExtractAllExprs;
import ece351.f.ast.FProgram;
import ece351.util.Examiner;

public final class TechnologyMapper extends PostOrderExprVisitor {

	/** Where we will write the output to. */
	private final PrintWriter out;
	
	/**
	 * Table of substitutions for common subexpression elimination. Note that
	 * this IdentityHashMap has non-deterministic iteration ordering. The staff
	 * solution iterates over substitutions.values(), but does not print those
	 * results directly: instead it stores those results in the nodes TreeSet
	 * (below) which sorts them before they are printed. When computing edges
	 * the staff solution uses this table only for lookups and does not iterate
	 * over the contents.
	 */
	private final IdentityHashMap<Expr,Expr> substitutions = new IdentityHashMap<Expr,Expr>();

	/**
	 * The set of nodes in our circuit diagram. (We'll produce a node for each
	 * .) We could just print the nodes directly to the output stream instead of
	 * building up this set, but then we might output the same node twice, and
	 * we might get a nonsensical order. The set uniqueness property ensure that
	 * we will ultimately print each node exactly once. TreeSet gives us
	 * deterministic iteration order: alphabetical.
	 */
	private final SortedSet<String> nodes = new TreeSet<String>();
	
	/**
	 * The set of edges in our circuit diagram. We could just print the edges
	 * directly to the output stream instead of building up this set, but then
	 * we might output the same edge twice. The set uniqueness property ensure
	 * that we will ultimately print each edge exactly once. LinkedHashSet gives
	 * us deterministic iteration order: insertion order. We need insertion
	 * order here because the elements will be inserted into this set by the
	 * post order traversal of the AST.
	 */
	private final Set<String> edges = new LinkedHashSet<String>();
	
	public TechnologyMapper(final PrintWriter out) {
		this.out = out;
	}
	
	public TechnologyMapper() {
		 this(new PrintWriter(System.out));
	}
	
	public static void main(final String arg) {
		main(new String[]{arg});
	}
	public static void main(final String[] args) {
		render(FParser.parse(args), new PrintWriter(System.out));
	}
	
	/**
	 * Translate an FProgram to Graphviz format.
	 */
	public static void render(final FProgram program, final PrintWriter out) {
		final TechnologyMapper tm = new TechnologyMapper(out);
		tm.render(program);
	}

	public void render(final FProgram program) {
		render(program, Examiner.Isomorphic);
	}

	/** Where the real work happens. */
	public void render(final FProgram program, final Examiner examiner) {
		header(out);
		
		// build a set of all of the exprs in the program
		// build substitutions by determining equivalences of exprs
		// create nodes for output vars
		// attach images to gates
		// ../../gates/not_noleads.png
		// ../../gates/or_noleads.png
		// ../../gates/and_noleads.png
		// compute edges
		// print nodes
		// print edges
// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
		
		// use the extractAllExprs to get all expressions and then do operations on them
		
		IdentityHashSet<Expr> expressions = ExtractAllExprs.allExprs(program);
		
		
		// iterate through each expression and generate svg
		int size = expressions.size();
		Object [] copy1 = expressions.toArray();
//		expressions.to
//		Expr []  copy2 = expressions.toArray();
		
		
//	    for (Expr a : expressions){
		for(int i = 0; i < size; i++){
			Expr a = (Expr)copy1[i];
//	    	for (Expr b : expressions) {
			for (int j = 0; j < size; j++ ){
				Expr b = (Expr)copy1[j];
				if(this.substitutions.get(b) == null && examiner.examine(a, b)){
					// we need to check if the substitution already exists in the Identity Hash Map
					this.substitutions.put(a, b);
				}
			}
	      
			// If a does not exist in th hashmap then add it
			if (substitutions.get(a) == null) {
				substitutions.put(a, a);
			}
	    }
	    
	    System.out.println(substitutions.toString());

	    // Traverse from a and create all the edges
	    // This will call the other function to add 
//	    program.formulas.
	    for(int k = 0; k < program.formulas.size(); k++){
	    	AssignmentStatement a = program.formulas.get(k);
	    	Expr e = traverseExpr(a.expr);
	    	edge(e, a.outputVar);
	    	visitVar(a.outputVar);
	    }


		for (String node : nodes) {
			out.println(node);
		}
		for (String edge : edges)  {
			out.println(edge);
		}

		
		// print footer
		footer(out);
		out.flush();
		
		// release memory
		substitutions.clear();
		nodes.clear();
		edges.clear();
	}

	
	private static void header(final PrintWriter out) {
		out.println("digraph g {");
		out.println("    // header");
		out.println("    rankdir=LR;");
		out.println("    margin=0.01;");
		out.println("    node [shape=\"plaintext\"];");
		out.println("    edge [arrowhead=\"diamond\"];");
		out.println("    // circuit ");
	}

	private static void footer(final PrintWriter out) {
		out.println("}");
	}

    /**
     * ConstantExpr follows the Singleton pattern, so we don't need
     * to look in the substitution table: we already know there are
     * only ConstantExpr objects in existence, one for True and one
     * for False.
     */
	@Override
	public Expr visitConstant(final ConstantExpr e) {
		node(e.serialNumber(), e.toString());
		return e;
	}

	@Override
	public Expr visitVar(final VarExpr e) {
		final Expr e2 = substitutions.get(e);
		assert e2 != null : "no substitution for " + e + " " + e.serialNumber();
		node(e2.serialNumber(), e2.toString());
		return e;
	}

	@Override
	public Expr visitNot(final NotExpr e) {
		edge(e.expr, e);
		return e;
	}

	@Override
	public Expr visitAnd(final AndExpr e) {
// TODO: short code snippet
		
//throw new ece351.util.Todo351Exception();
		// return e;
		AndExpr andExp = (AndExpr)substitutions.get(e);
		
		if(andExp == null){
//			throw new ece351.util.Todo351Exception();
			// throw substitution error
			System.out.println("no substitution for " + e + " " + e.serialNumber());
		}
		
		// create node
		node(andExp.serialNumber(), andExp.toString(), "../../gates/and_noleads.png");
		
		
		// create edge
	    edge(andExp.right, andExp);
	    edge(andExp.left, andExp);
		
		return e;
		
	}

	@Override
	public Expr visitOr(final OrExpr e) {
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();
		
		OrExpr orExp = (OrExpr)substitutions.get(e);
		
		if(orExp == null){
			System.out.println("no substitution for " + e + " " + e.serialNumber());
		}
		
		// create node
		node(orExp.serialNumber(), orExp.toString(), "../../gates/or_noleads.png");
		
		
		// create edge
	    edge(orExp.right, orExp);
	    edge(orExp.left, orExp);
		 return e;
	}
	
	@Override public Expr visitNaryAnd(final NaryAndExpr e) {
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();
		
		NaryAndExpr nAndExpr = (NaryAndExpr)substitutions.get(e);
		
		if(nAndExpr == null){
			System.out.println("no substitution for " + e + " " + e.serialNumber());
		}
		
		 node(nAndExpr.serialNumber(), nAndExpr.toString(),"../../gates/and_noleads.png" );
		
		// iterate through nAndExpr and add edges
		for(int i = 0; i < nAndExpr.children.size(); i++){
			
			edge(nAndExpr.children.get(i), nAndExpr);
			
		}
		
		 return e;
	}

	@Override public Expr visitNaryOr(final NaryOrExpr e) { 
		
		NaryOrExpr nOrExpr = (NaryOrExpr)substitutions.get(e);
		
		if(nOrExpr == null){
			System.out.println("no substitution for " + e + " " + e.serialNumber());
			// throw error
		}
		node(nOrExpr.serialNumber(), nOrExpr.toString(), "../../gates/and_noleads.png");
		for(int i = 0; i < nOrExpr.children.size(); i++){
			
			edge(nOrExpr.children.get(i), nOrExpr);
		}
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();
		 return e;
	}


	private void node(final String name, final String label) {
		nodes.add("    " + name + "[label=\"" + label + "\"];");
	}

	private void node(final String name, final String label, final String image) {
		nodes.add(String.format("    %s [label=\"%s\", image=\"%s\"];", name, label, image));
	}

	private void edge(final Expr source, final Expr target) {
		edge(substitutions.get(source).serialNumber(), substitutions.get(target).serialNumber());
	}
	
	private void edge(final String source, final String target) {
		edges.add("    " + source + " -> " + target + " ;");
	}
	
	@Override public Expr visitXOr(final XOrExpr e) { throw new IllegalStateException("TechnologyMapper does not support " + e.getClass()); }
	@Override public Expr visitNAnd(final NAndExpr e) { throw new IllegalStateException("TechnologyMapper does not support " + e.getClass()); }
	@Override public Expr visitNOr(final NOrExpr e) { throw new IllegalStateException("TechnologyMapper does not support " + e.getClass()); }
	@Override public Expr visitXNOr(final XNOrExpr e) { throw new IllegalStateException("TechnologyMapper does not support " + e.getClass()); }
	@Override public Expr visitEqual(final EqualExpr e) { throw new IllegalStateException("TechnologyMapper does not support " + e.getClass()); }

}

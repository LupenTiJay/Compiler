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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;

/**
 * An expression with multiple children. Must be commutative.
 */
public abstract class NaryExpr extends Expr {

	public final ImmutableList<Expr> children;

	public NaryExpr(final Expr... exprs) {
		Arrays.sort(exprs);
		ImmutableList<Expr> c = ImmutableList.of();
		for (final Expr e : exprs) {
			c = c.append(e);
		}
    	this.children = c;
	}
	
	public NaryExpr(final List<Expr> children) {
		final ArrayList<Expr> a = new ArrayList<Expr>(children);
		Collections.sort(a);
		this.children = ImmutableList.copyOf(a);
	}

	/**
	 * Each subclass must implement this factory method to return
	 * a new object of its own type. 
	 */
	public abstract NaryExpr newNaryExpr(final List<Expr> children);

	/**
	 * Construct a new NaryExpr (of the appropriate subtype) with 
	 * one extra child.
	 * @param e the child to append
	 * @return a new NaryExpr
	 */
	public NaryExpr append(final Expr e) {
		return newNaryExpr(children.append(e));
	}

	/**
	 * Construct a new NaryExpr (of the appropriate subtype) with 
	 * the extra children.
	 * @param list the children to append
	 * @return a new NaryExpr
	 */
	public NaryExpr appendAll(final List<Expr> list) {
		final List<Expr> a = new ArrayList<Expr>(children.size() + list.size());
		a.addAll(children);
		a.addAll(list);
		return newNaryExpr(a);
	}

	/**
	 * Check the representation invariants.
	 */
	public boolean repOk() {
		// programming sanity
		assert this.children != null;
		// should not have a single child: indicates a bug in simplification
		assert this.children.size() > 1 : "should have more than one child, probably a bug in simplification";
		// check that children is sorted
		int i = 0;
		for (int j = 1; j < this.children.size(); i++, j++) {
			final Expr x = this.children.get(i);
			assert x != null : "null children not allowed in NaryExpr";
			final Expr y = this.children.get(j);
			assert y != null : "null children not allowed in NaryExpr";
			assert x.compareTo(y) <= 0 : "NaryExpr.children must be sorted";
		}
		// no problems found
		return true;
	}

	/**
	 * The name of the operator represented by the subclass.
	 * To be implemented by each subclass.
	 */
	public abstract String operator();
	
	/**
	 * The complementary operation: NaryAnd returns NaryOr, and vice versa.
	 */
	abstract protected Class<? extends NaryExpr> getThatClass();
	

	/**
     * e op x = e for absorbing element e and operator op.
     * @return
     */
	public abstract ConstantExpr getAbsorbingElement();

    /**
     * e op x = x for identity element e and operator op.
     * @return
     */
	public abstract ConstantExpr getIdentityElement();


	@Override 
    public final String toString() {
    	final StringBuilder b = new StringBuilder();
    	b.append("(");
    	int count = 0;
    	for (final Expr c : children) {
    		b.append(c);
    		if (++count  < children.size()) {
    			b.append(" ");
    			b.append(operator());
    			b.append(" ");
    		}
    		
    	}
    	b.append(")");
    	return b.toString();
    }


	@Override
	public final int hashCode() {
		return 17 + children.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		if (!(obj instanceof Examinable)) return false;
		return examine(Examiner.Equals, (Examinable)obj);
	}
	
	@Override
	public final boolean isomorphic(final Examinable obj) {
		return examine(Examiner.Isomorphic, obj);
	}
	
	private boolean examine(final Examiner e, final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!this.getClass().equals(obj.getClass())) return false;
		final NaryExpr that = (NaryExpr) obj;
		
		// if the number of children are different, consider them not equivalent
		// since the n-ary expressions have the same number of children and they are sorted, just iterate and check
		// supposed to be sorted, but might not be (because repOk might not pass)
		// if they aren't the same elements in the same order return false
		// no significant differences found, return true
		
		// if size of this != size of that -> return false
		if(this.children.size() != that.children.size()){
			return false;
		}
		// call repOk() -> return the result
		if(this.repOk() == false){
			return false;
		}
		
		if(that.repOk() == false){
			return false;
		}
		// iterate, check if !this.children[i].equals(that.children[i] -> return false
		
		for(int i = 0; i < this.children.size(); i++){
			if(e.examine(this.children.get(i), that.children.get(i)) == false){
				return false;
			}
		}
		
		return true;
		//return true
		
		
		
		
		
// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
	}

	
	@Override
	protected final Expr simplifyOnce() {
		assert repOk();
		final Expr result = 
				simplifyChildren().
				mergeGrandchildren().
				foldIdentityElements().
				foldAbsorbingElements().
				foldComplements().
				removeDuplicates().
				simpleAbsorption().
				subsetAbsorption().
				singletonify();
		assert result.repOk();
		return result;
	}
	
	/**
	 * Call simplify() on each of the children.
	 */
	private NaryExpr simplifyChildren() {
		// note: we do not assert repOk() here because the rep might not be ok
		// the result might contain duplicate children, and the children
		// might be out of order
		
		List<Expr> naryList = new ArrayList<Expr>();

		for (Expr expr : children) {
			naryList.add(expr.simplify());
		}

		return newNaryExpr(naryList);
		
	}

	
	private NaryExpr mergeGrandchildren() {
		// extract children to merge using filter (because they are the same type as us)
			// if no children to merge, then return this (i.e., no change)
			// use filter to get the other children, which will be kept in the result unchanged
			// merge in the grandchildren
			// assert result.repOk():  this operation should always leave the AST in a legal state
		
//		NaryExpr test = newNaryExpr(ImmutableList.of());
		NaryExpr mergedEquiv;
		NaryExpr mergedFalse;
//		children.
		// get all the children
//		mergedEquiv = this.removeAll(children, Examiner.Equivalent); // this function does the filtering and gets all
		mergedEquiv = filter(this.getClass(), true);
		mergedFalse = filter(this.getClass(), false);
		ArrayList<Expr> test = new ArrayList<Expr>();
		
		// no children to merge, return with no change
		if(mergedEquiv.children.size() <0){
			return this;
		}
		// if mergedEquiv is greater then 1, call simplify again
		
		for(int i = 0; i < mergedEquiv.children.size(); i++){
			NaryExpr tmp = (NaryExpr)mergedEquiv.children.get(i);
			test.addAll(tmp.children);
		}
	
		mergedFalse = mergedFalse.appendAll(test);
		assert mergedFalse.repOk();
		return mergedFalse;
		// iterate through all the children
		// merge the leaves
		
//		return this; // TODO: replace this stub
	}


    private NaryExpr foldIdentityElements() {
    	// if we have only one child stop now and return self
    	// we have multiple children, remove the identity elements
    		// all children were identity elements, so now our working list is empty
    		// return a new list with a single identity element
    		// normal return
    	
    	
    	// one children
    	if(children.size() <= 1){
    		return this;
    	}
    	// removing identity elements
    	
//    	Expr identityElements = filter(this.getClass(), true);
    	NaryExpr test = this.filter(this.getIdentityElement(),Examiner.Equals, false);
//    	for(int i = 0; i < test.children.size(); i++){
//    		this.append(test.children.get(i));
//    	}
    	
    	if(test.children.size() < 1){
    		test = test.append(getIdentityElement());
    	}
    	
    	return test;
    	
    	
//		return this; // TODO: replace this stub
    	// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
    }

    private NaryExpr foldAbsorbingElements() {
    	NaryExpr test = newNaryExpr(ImmutableList.of());
    	
    	if (this.contains(this.getAbsorbingElement(), Examiner.Equals)){
    		test = test.append(this.getAbsorbingElement());
    		return test;
    	}
    	
    	return this;
    	

    }

	private NaryExpr foldComplements() {
		// collapse complements
		// !x . x . ... = 0 and !x + x + ... = 1
		// x op !x = absorbing element
		// find all negations
		NaryExpr neg = this.filter(NotExpr.class, true);
		// for each negation, see if we find its complement
				// found matching negation and its complement
				// return absorbing element
		
		for(int i = 0; i < neg.children.size(); i++){
			NotExpr cursor = (NotExpr)neg.children.get(i);
			if(this.contains(cursor.expr, Examiner.Equals)){
				// negation found
				// create new folded NaryExpr
				NaryExpr test = newNaryExpr(ImmutableList.of());
				test = test.append((Expr)this.getAbsorbingElement());
				return test;
			}
		}
		// no complements to fold
		
	
		return this; // TODO: replace this stub
    	// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
	}

	private NaryExpr removeDuplicates() {
		// remove duplicate children: x.x=x and x+x=x
		// since children are sorted this is fairly easy
			// no changes
			// removed some duplicates
		
		// create a NaryExpr obj with emity immutable list
		NaryExpr test = newNaryExpr(ImmutableList.of());
	    for (int i = 0; i < this.children.size() - 1; i++) {
	        if (!((Expr)this.children.get(i)).equals(this.children.get(i + 1))) {
	          test = test.append((Expr)this.children.get(i));
	        }
	      }
		Expr last = (Expr)this.children.get(this.children.size() - 1);
		test = test.append(last);
		return test; // TODO: replace this stub
    	// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
	}

	private NaryExpr simpleAbsorption() {

		ArrayList<Expr> rem = new ArrayList<Expr>();
		NaryExpr tht = this.filter(this.getThatClass(), true);
		NaryExpr ths = this.filter(this.getThatClass(), false);
		if(tht.children.size() == 0){
			return this;
		}

		for (Expr thtChild : tht.children) {
			for (Expr t : ((NaryExpr)thtChild).children) {
				if (ths.contains(t, Examiner.Equals)) {
					rem.add(thtChild);
				}
			}
		}

		return this.removeAll(rem, Examiner.Equals);
	}

	private NaryExpr subsetAbsorption() {
// TODO: replace this stub
		NaryExpr tht = filter(getThatClass(), true);
		ImmutableList<Expr> excludeSubset = ImmutableList.of();

		for (int i = 0; i < tht.children.size(); i++) {
			NaryExpr comparator1 = (NaryExpr)tht.children.get(i);
			for (int j = 0; j < tht.children.size(); j++) {
				Boolean subsetFlag = false;
				if(i != j){
					
					NaryExpr comparator2 = (NaryExpr)tht.children.get(j);
					
					// compare the children of 
					for(int k = 0; k < comparator1.children.size(); k++){
						if(!comparator2.contains(comparator1.children.get(k), Examiner.Equals)){
							subsetFlag = true;
							break;
						}
					}
					
					if(subsetFlag == false){
						if(comparator1.children.size() < comparator2.children.size()){
							excludeSubset = excludeSubset.append(comparator2);
						}
						else{
							excludeSubset = excludeSubset.append(comparator1);
						}
					}
				}
			}
		}
		

		return removeAll(excludeSubset, Examiner.Equals);
	}

	/**
	 * If there is only one child, return it (the containing NaryExpr is unnecessary).
	 */
	private Expr singletonify() {
		// if we have only one child, return it
		
		if (this.children.size() == 1){
			return this.children.get(0);
		}
		
		// having only one child is an illegal state for an NaryExpr
			// multiple children; nothing to do; return self
		return this; // TODO: replace this stub
	}

	/**
	 * Return a new NaryExpr with only the children of a certain type, 
	 * or excluding children of a certain type.
	 * @param filter
	 * @param shouldMatchFilter
	 * @return
	 */
	public final NaryExpr filter(final Class<? extends Expr> filter, final boolean shouldMatchFilter) {
		ImmutableList<Expr> l = ImmutableList.of();
		for (final Expr child : children) {
			if (child.getClass().equals(filter)) {
				if (shouldMatchFilter) {
					l = l.append(child);
				}
			} else {
				if (!shouldMatchFilter) {
					l = l.append(child);
				}
			}
		}
		return newNaryExpr(l);
	}

	public final NaryExpr filter(final Expr filter, final Examiner examiner, final boolean shouldMatchFilter) {
		ImmutableList<Expr> l = ImmutableList.of();
		for (final Expr child : children) {
			if (examiner.examine(child, filter)) {
				if (shouldMatchFilter) {
					l = l.append(child);
				}
			} else {
				if (!shouldMatchFilter) {
					l = l.append(child);
				}
			}
		}
		return newNaryExpr(l);
	}

	public final NaryExpr removeAll(final List<Expr> toRemove, final Examiner examiner) {
		NaryExpr result = this;
		for (final Expr e : toRemove) {
			result = result.filter(e, examiner, false);
		}
		return result;
	}

	public final boolean contains(final Expr expr, final Examiner examiner) {
		for (final Expr child : children) {
			if (examiner.examine(child, expr)) {
				return true;
			}
		}
		return false;
	}

}

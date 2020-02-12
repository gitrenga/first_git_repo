package org.sonar.samples.java.checks;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ListIterator;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.ExpressionUtils;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TryStatementTree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

@Rule(
		  key = "MandatoryReleaseResourceRule",
		  name = "Public Method not having finally in EJB",
		  description = "Public Method not having finally in EJB",
		  priority = Priority.CRITICAL,
		  tags = {"bug"})
public class MandatoryReleaseResourceRule extends IssuableSubscriptionVisitor{

	@Override
	public List<Kind> nodesToVisit() {
		return ImmutableList.of(Kind.TRY_STATEMENT);
	}
	
	@Override
	public void visitNode(Tree tree) {
		//if(isAPublicMethod(getMethodFromTree(tree))){
		boolean releaseResourceWritten = false;
		String enclosingMethodMissingReleaseResource = "";
			if(getFinallyBlock(tree) == null) {
				reportIssue(getMethodFromTree(tree).simpleName(), "Public Method not having finally in EJB");
			}
			else {
				ListIterator<StatementTree> statementTreeLst =  getFinallyBlock(tree).body().listIterator();
				
				while (statementTreeLst.hasNext()) {
					ExpressionStatementTree blockTree = (ExpressionStatementTree) statementTreeLst.next();
					if(!((ClassTree)ExpressionUtils.getEnclosingMethod(blockTree.expression()).parent()).simpleName().name().contains("EJB"))
						break;
					enclosingMethodMissingReleaseResource = ExpressionUtils.getEnclosingMethod(blockTree.expression()).simpleName().name();
					if(enclosingMethodMissingReleaseResource.equals("rengaTest")) {
						System.out.println("rengaTest");
					}
					if(!isAPublicMethod(ExpressionUtils.getEnclosingMethod(blockTree.expression()))) {
						releaseResourceWritten = true;
						break;
					}
					//System.out.println(ExpressionUtils.methodName(blockTree.));
					if (blockTree.expression().is(Tree.Kind.METHOD_INVOCATION)) {
						  MethodInvocationTree mit = (MethodInvocationTree) blockTree.expression();
						 // System.out.println(((ClassTree)ExpressionUtils.getEnclosingMethod(blockTree.expression()).parent()).simpleName().name().contains("EJB"));
						// System.out.println(ExpressionUtils.getEnclosingMethod(expr)mit.parent().getClass());
						if(mit.methodSelect().lastToken().text().matches("releaseResource")) {
							  releaseResourceWritten = true;
							  
							  break;
						}
					}
				} 
				if(!releaseResourceWritten) {
					System.out.println("caught method without finally: \n");
					System.out.println(enclosingMethodMissingReleaseResource);
					System.out.println("\n");
					reportIssue(getFinallyBlock(tree), "Public Method not having finally in EJB");
				}			
				 
				
			}
			
		//}
	}
	
	 

	private BlockTree getFinallyBlock(Tree tree) {
		boolean isTryBlock = tree.is(Kind.TRY_STATEMENT);
		if(isTryBlock) {
			TryStatementTree tryStatementTree = (TryStatementTree)tree;
			return tryStatementTree.finallyBlock();
		}
		return null;			
	}

	private MethodTree getMethodFromTree(Tree tree) {
		MethodTree method = (MethodTree) tree;
		return method;
	}

	private boolean isAPublicMethod(MethodTree method) {
		return ((ModifierKeywordTree)method.modifiers().modifiers().get(0)).modifier().name().equals("PUBLIC");
	}

}

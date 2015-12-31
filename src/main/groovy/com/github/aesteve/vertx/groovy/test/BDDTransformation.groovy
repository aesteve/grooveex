package com.github.aesteve.vertx.groovy.test

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * AST Transformation to create a Behaviour Driven test from a test declaration
 * Created by aesteve on 31/12/2015.
 */
@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class BDDTransformation implements ASTTransformation {

	@Override
	void visit(ASTNode[] nodes, SourceUnit source) {
		if (!checkNode(nodes)) return
		ClosureExpression whenExpression
		ClosureExpression thenExpression
		MethodNode method = nodes[1] as MethodNode
		BlockStatement statement = method.code as BlockStatement
		Parameter[] methodParams = method.parameters
		statement.statements.each {
			if (it instanceof ExpressionStatement) {
				ExpressionStatement rootStatement = it as ExpressionStatement
				Expression expr = rootStatement.expression
				if (expr && expr instanceof MethodCallExpression) {
					MethodCallExpression methodCall = expr as MethodCallExpression
					Expression methodExpr = methodCall.method
					if (methodExpr && methodExpr instanceof ConstantExpression) {
						ConstantExpression constantExpression = methodExpr as ConstantExpression
						switch (constantExpression.value) {
							case 'when':
								ArgumentListExpression args = methodCall.arguments as ArgumentListExpression
								if (args.size() == 1) whenExpression = args[0] as ClosureExpression
								break
							case 'then':
								ArgumentListExpression args = methodCall.arguments as ArgumentListExpression
								if (args.size() == 1) thenExpression = args[0] as ClosureExpression
								break
						}
					}
				}
			}
		}
		if (methodParams.size() != 1) throw new RuntimeException("Test methods should only have one parameter of type io.vertx.groovy.ext.unit.TestContext")
		Parameter methodContextParam = method.parameters[0]
		ClassNode methodWrapperClass = new ClassNode(MethodWrapper.class)
		List<ClosureExpression> exprs = transform(thenExpression)
		exprs.add(0, whenExpression)
		ArgumentListExpression paramsExpression = new ArgumentListExpression(exprs.collect { it as Expression })
		Expression construct = new ConstructorCallExpression(methodWrapperClass, paramsExpression)
		ArgumentListExpression args = new ArgumentListExpression()
		args.addExpression(new VariableExpression(methodContextParam.name))
		Expression callDSL = new MethodCallExpression(construct, "call", args)
		Statement callStatement = new ExpressionStatement(callDSL)
		Statement newMethodBody = new BlockStatement()
		newMethodBody.addStatement(callStatement)

		method.code = newMethodBody
	}

	private boolean checkNode(ASTNode[] astNodes) {
		if (!astNodes) return false
		if (!astNodes[0]) return false
		if (!astNodes[1]) return false
		true
	}

	private List<ClosureExpression> transform(ClosureExpression thenExpression) {
		BlockStatement content = thenExpression.code as BlockStatement
		content.statements?.findResults {
			if (it instanceof ExpressionStatement) {
				ExpressionStatement expr = it as ExpressionStatement
				if (expr.expression && expr.expression instanceof BinaryExpression) {
					return wrap(expr)
				}
			}
		} as List
	}

	private ClosureExpression wrap(ExpressionStatement statement) {
		ClosureExpression clos = new ClosureExpression([new Parameter(new ClassNode(Object.class), 'obj')] as Parameter[], statement)
		clos.setVariableScope(new VariableScope())
		clos
	}
}

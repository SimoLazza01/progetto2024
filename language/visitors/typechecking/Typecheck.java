package language.visitors.typechecking;

import static language.visitors.typechecking.AtomicType.*;

import language.environments.EnvironmentException;
import language.environments.GenEnvironment;
import language.parser.ast.Block;
import language.parser.ast.Exp;
import language.parser.ast.Stmt;
import language.parser.ast.StmtSeq;
import language.parser.ast.Variable;
import language.visitors.Visitor;

public class Typecheck implements Visitor<Type> {

	private final StaticEnv env = new StaticEnv();

    // useful to typecheck binary operations where operands must have the same type 
	private void checkBinOp(Exp left, Exp right, Type type) {
		type.checkEqual(left.accept(this));
		type.checkEqual(right.accept(this));
	}

	// static semantics for programs; no value returned by the visitor

	@Override
	public Type visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
		} catch (EnvironmentException e) { // undeclared variable
			throw new TypecheckerException(e);
		}
		return null;
	}

	// static semantics for statements; no value returned by the visitor

	@Override
	public Type visitAssignStmt(Variable var, Exp exp) {
		Type type = var.accept(this);
		type.checkEqual(exp.accept(this));
		return null;
	}

	@Override
	public Type visitPrintStmt(Exp exp) {
		exp.accept(this);
		return null;
	}

	@Override
	public Type visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
	}

	@Override
	public Type visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		BOOL.checkEqual(exp.accept(this));
		thenBlock.accept(this);
		if (elseBlock != null) elseBlock.accept(this);
	  return null;
	}

	@Override
	public Type visitForStmt(Variable var, Exp dict, Block block) {
		// We declare the variable, the new variable'type will be a pair (INT, Type of dict)
		// With getDictType() we get the dict's type and check that it is a dict at the same time
		env.dec(var, new PairType(INT, dict.accept(this).getDictType()));
		block.accept(this);
	  return null;
	}

	@Override
	public Type visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
	  stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// static semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Type visitEmptyStmtSeq() {
	  return null;
	}

	@Override
	public Type visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// static semantics of expressions; a type is returned by the visitor

	@Override
	public AtomicType visitAdd(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public AtomicType visitIntLiteral(int value) {
	  return INT;
	}

	@Override
	public AtomicType visitMul(Exp left, Exp right) {
	  checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public AtomicType visitSign(Exp exp) {
		INT.checkEqual(exp.accept(this));
	  return INT;
	}

	@Override
	public Type visitVariable(Variable var) {
	  return env.lookup(var);
	}

	@Override
	public AtomicType visitNot(Exp exp) {
		BOOL.checkEqual(exp.accept(this));
		return BOOL;
	}

	@Override
	public AtomicType visitAnd(Exp left, Exp right) {
	  checkBinOp(left, right, BOOL);
		return BOOL;
	}

	@Override
	public AtomicType visitBoolLiteral(boolean value) {
	  return BOOL;
	}

	@Override
	public AtomicType visitEq(Exp left, Exp right) {
		Type type = left.accept(this);
		type.checkEqual(right.accept(this));
		return BOOL;
	}

	@Override
	public PairType visitPairLit(Exp left, Exp right) {
		return new PairType(left.accept(this), right.accept(this));
	}

	@Override
	public Type visitFst(Exp exp) {
	  return exp.accept(this).getFstPairType();
	}

	@Override
	public Type visitSnd(Exp exp) {
	  return exp.accept(this).getSndPairType();
	}

	@Override
	public DictType visitDictCons(Exp left, Exp right) {
		INT.checkEqual(left.accept(this));
		return new DictType(right.accept(this));
	}

	@Override
	public DictType visitDictDel(Exp left, Exp right) {
		INT.checkEqual(right.accept(this));
		return left.accept(this).checkIsDictType();
	}

	@Override
	public Type visitDictGet(Exp left, Exp right) {
		INT.checkEqual(right.accept(this));
		return left.accept(this).getDictType();
	}

	@Override
	public DictType visitDictPut(Exp left, Exp middle, Exp right) {
		INT.checkEqual(middle.accept(this));
		var dictTy = left.accept(this).checkIsDictType();
		dictTy.getDictType().checkEqual(right.accept(this));
		return dictTy;
	}

}

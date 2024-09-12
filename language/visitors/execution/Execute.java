package language.visitors.execution;

import java.io.PrintWriter;

import language.environments.EnvironmentException;
import language.environments.GenEnvironment;
import language.parser.ast.Block;
import language.parser.ast.Exp;
import language.parser.ast.Stmt;
import language.parser.ast.StmtSeq;
import language.parser.ast.Variable;
import language.visitors.Visitor;
import java.util.TreeMap;

import static java.util.Objects.requireNonNull;

public class Execute implements Visitor<Value> {

	private final DynamicEnv env = new DynamicEnv();
	private final PrintWriter printWriter; // output stream used to print values

	public Execute() {
		printWriter = new PrintWriter(System.out, true);
	}

	public Execute(PrintWriter printWriter) {
		this.printWriter = requireNonNull(printWriter);
	}

	// dynamic semantics for programs; no value returned by the visitor

	@Override
	public Value visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
			// possible runtime errors
			// EnvironmentException: undefined variable
		} catch (EnvironmentException e) {
			throw new InterpreterException(e);
		}
		return null;
	}

	// dynamic semantics for statements; no value returned by the visitor

	@Override
	public Value visitAssignStmt(Variable var, Exp exp) {
		env.update(var, exp.accept(this));
	  return null;
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		printWriter.println(exp.accept(this));
		return null;
	}

	@Override
	public Value visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
	}

	@Override
	public Value visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		// Soluzione semplice del professore
		// if (exp.accept(this).toBool()) thenBlock.accept(this);
		// else if (elseBlock != null) elseBlock.accept(this);
	  if (elseBlock == null) {
			if (exp.accept(this).toBool()) {
				thenBlock.accept(this);
			}
		} else {
			if (exp.accept(this).toBool()) {
				thenBlock.accept(this);
			} else {
				elseBlock.accept(this);
			}
		}
		return null;
	}

	@Override
	public Value visitForStmt(Variable var, Exp dict, Block block) {
		TreeMap<Integer,Value> map = dict.accept(this).toDict();
		Integer[] keys = map.keySet().toArray(new Integer[0]);
		StmtSeq stmtSeq = block.getStmtSeq();
		env.enterScope();
		env.dec(var, new PairValue(new IntValue(keys[0]), map.get(keys[0])));
		stmtSeq.accept(this);
		for (int i = 1; i < keys.length; i++) {
			env.update(var, new PairValue(new IntValue(keys[i]), map.get(keys[i])));
			stmtSeq.accept(this);
		}
		env.exitScope();
		return null;
	}

	@Override
	public Value visitBlock(StmtSeq stmtSeq) {
	  env.enterScope();
	  stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// dynamic semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Value visitEmptyStmtSeq() {
	  return null;
	}

	@Override
	public Value visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
	 	first.accept(this);
		rest.accept(this);
		return null;
	}

	// dynamic semantics of expressions; a value is returned by the visitor

	@Override
	public IntValue visitAdd(Exp left, Exp right) {
		return new IntValue(left.accept(this).toInt() + right.accept(this).toInt());
	}

	@Override
	public IntValue visitIntLiteral(int value) {
	  return new IntValue(value);
	}

  @Override
	public IntValue visitMul(Exp left, Exp right) {
	  return new IntValue(left.accept(this).toInt() * right.accept(this).toInt());
	}
    
	@Override
	public IntValue visitSign(Exp exp) {
	  return new IntValue(-exp.accept(this).toInt());
	}

	@Override
	public Value visitVariable(Variable var) {
	  return env.lookup(var);
	}

	@Override
	public BoolValue visitNot(Exp exp) {
		return new BoolValue(!exp.accept(this).toBool());
	}

	@Override
	public BoolValue visitAnd(Exp left, Exp right) {
	  return new BoolValue(left.accept(this).toBool() && right.accept(this).toBool());
	}

	@Override
	public BoolValue visitBoolLiteral(boolean value) {
	  return new BoolValue(value);
	}

	@Override
	public BoolValue visitEq(Exp left, Exp right) {
	  return new BoolValue(left.accept(this).equals(right.accept(this)));
	}

	@Override
	public PairValue visitPairLit(Exp left, Exp right) {
	  return new PairValue(left.accept(this), right.accept(this));
	}

	@Override
	public Value visitFst(Exp exp) {
	  return exp.accept(this).toPair().getFstVal();
	}

	@Override
	public Value visitSnd(Exp exp) {
	  return exp.accept(this).toPair().getSndVal();
	}

	@Override
	public DictValue visitDictCons(Exp left, Exp right) {
		DictValue dict = new DictValue();
		Integer key = left.accept(this).toInt();
		Value value = right.accept(this);
		dict.toDict().put(key, value);
		return dict;
	}

	@Override
	public DictValue visitDictDel(Exp left, Exp right) {
		DictValue dict = (DictValue) left.accept(this);
		dict = dict.getClone();
		Integer key = right.accept(this).toInt();
		if (dict.toDict().containsKey(key)) {
			dict.toDict().remove(key);
		} else {
			throw new InterpreterException("Missing key " + key);
		}
		return dict;
	}

	@Override
	public Value visitDictGet(Exp left, Exp right) {
		DictValue dict = (DictValue) left.accept(this);
		dict = dict.getClone();
		Integer key = right.accept(this).toInt();
		if (dict.toDict().containsKey(key)) {
			return dict.toDict().get(key);
		} else {
			throw new InterpreterException("Missing key " + key);
		}
	}

	@Override
	public DictValue visitDictPut(Exp left, Exp middle, Exp right) {
		DictValue dict = (DictValue) left.accept(this);
		dict = dict.getClone();
		Integer key = middle.accept(this).toInt();
		Value value = right.accept(this);
		dict.toDict().put(key, value);
		return dict;
	}
}


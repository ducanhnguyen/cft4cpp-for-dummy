package com.fit.testdatagen.se;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fit.normalizer.ClassvsStructNormalizer;
import com.fit.testdatagen.se.memory.BasicSymbolicVariable;
import com.fit.testdatagen.se.memory.ISymbolicVariable;
import com.fit.testdatagen.se.memory.IVariableNodeTable;
import com.fit.testdatagen.se.memory.LogicCell;
import com.fit.testdatagen.se.memory.OneDimensionSymbolicVariable;
import com.fit.testdatagen.se.memory.OneLevelSymbolicVariable;
import com.fit.testdatagen.se.memory.Reference;
import com.fit.testdatagen.se.memory.SimpleStructureSymbolicVariable;
import com.fit.testdatagen.se.memory.TwoDimensionSymbolicVariable;
import com.fit.testdatagen.se.memory.TwoLevelSymbolicVariable;
import com.fit.testdatagen.se.memory.UnionSymbolicVariable;
import com.fit.testdatagen.se.normalization.PointerAccessNormalizer;
import com.fit.testdatagen.testdatainit.VariableTypes;
import com.fit.testdatagen.testdatainit.VariablesSize;
import com.fit.testdatagen.testdatainit.VariablesSize.BASIC;
import com.fit.tree.object.IVariableNode;
import com.fit.tree.object.UnionNode;
import com.fit.utils.IRegex;
import com.fit.utils.Utils;

public class ExpressionRewriterUtils {

	/**
	 * Shorten expression in pair of parentheses <br/>
	 * We have expression "(1+2)+x>0" ---shorten---> "3+x>0"
	 *
	 * @param expression
	 * @return
	 */
	public static String shortenExpressionInParentheses(String expression) {
		Map<String, String> tokens = new HashMap<>();
		/*
		 * The number of iteration is very important. For example, we have
		 * "(1+(1+2))>x". In this case, we need at least two iterations to shorten this
		 * expression as much as possible.
		 * 
		 * In this first iteration, the expression becomes "(1+3)>x". And also after the
		 * second iteration, we get the final expression "4>x".
		 * 
		 * By default, the value of maximum iterations should not be small.
		 */
		final int MAX_ITERATION = 4;
		for (int i = 0; i < MAX_ITERATION; i++) {
			Pattern pattern = Pattern.compile(IRegex.EXPRESSION_IN_PARETHENESS);
			Matcher matcher = pattern.matcher(expression);
			if (matcher.find()) {
				String str = matcher.group(0);
				String value = new CustomJeval().evaluate(str);

				/*
				 * Ex: transform negative expressison into another expression, e.g., "(-1.1)"
				 */
				final String NEGATIVE = "-";
				if (value.startsWith(NEGATIVE)) {
					String replacement = tokens.size() + "@@@";
					tokens.put("(" + value + ")", replacement);
					expression = expression.replace(str, replacement);
				} else
					expression = expression.replace(str, value);
			}
		}
		/*
		 * Restore tokens
		 */
		for (String key : tokens.keySet())
			expression = expression.replace(tokens.get(key), key);
		return expression;
	}

	/**
	 * Shorten expression in pair of brackets <br/>
	 * We have expression "a[1+2]+x>0" ---shorten---> "a[3]+x>0"
	 *
	 * @param expression
	 * @return
	 */
	public static String shortenExpressionInBracket(String expression) {
		Pattern pattern = Pattern.compile(IRegex.EXPRESSION_IN_BRACKET);
		Matcher matcher = pattern.matcher(expression);
		if (matcher.find()) {
			String str = matcher.group(1);
			String value = new CustomJeval().evaluate(str);
			expression = expression.replace(str, (int) Double.parseDouble(value) + "");
		}

		return expression;
	}

	/**
	 * Convert pointer access to array access
	 *
	 * @param expression
	 *            Expression that the index of pointer does not put in pairs or
	 *            parentheses. Ex: p1[0] is available. But p1[0+(1+0)] is
	 *            unavailable
	 * @return
	 */
	public static String convertOneLevelPointerItemToArrayItem(String expression) {
		PointerAccessNormalizer norm = new PointerAccessNormalizer();
		norm.setOriginalSourcecode(expression);
		norm.normalize();
		return norm.getNormalizedSourcecode();
	}

	/**
	 * Check later
	 *
	 * @param expression
	 * @return
	 */
	public static String convertTwoLevelPointerItemToArrayItem(String expression) {
		String expectClosingBracket = "[^\\)]*";
		String regex = IRegex.CLOSING_PARETHENESS + IRegex.POINTER + IRegex.OPENING_PARETHENESS
				+ ExpressionRewriterUtils.group(IRegex.NAME_REGEX)
				+ ExpressionRewriterUtils.group("[+-]" + expectClosingBracket) + IRegex.CLOSING_PARETHENESS;
		expression = expression.replaceAll(regex, "($1[0$2]");

		/*
		 * **P2 --> P2[0][0]
		 */
		expression = expression.replaceAll("^" + IRegex.POINTER + IRegex.POINTER + "(" + IRegex.NAME_REGEX + ")",
				"$1[0][0]");
		expression = expression.replaceAll("\\(" + IRegex.POINTER + IRegex.POINTER + "(" + IRegex.NAME_REGEX + ")",
				"($1[0][0]");

		/*
		 * **(P2) --> P2[0][0]
		 */
		expression = expression.replaceAll("([^a-zA-Z0-9_\\)\\]])" + IRegex.POINTER + IRegex.POINTER
				+ IRegex.OPENING_PARETHENESS + "(" + IRegex.NAME_REGEX + ")" + IRegex.CLOSING_PARETHENESS,
				"$1$2[0][0]");
		expression = expression.replaceAll("^" + IRegex.POINTER + IRegex.POINTER + "\\(([a-zA-Z0-9_]+)\\)", "$1[0][0]");
		expression = expression.replaceAll(
				"([^a-zA-Z0-9_\\)\\]])" + IRegex.POINTER + IRegex.POINTER + "(" + IRegex.NAME_REGEX + ")",
				"$1$2[0][0]");

		return expression;
	}

	private static String group(String s) {
		return "(" + s + ")";
	}

	/**
	 * Ex: 'a' -----convert---> 65
	 *
	 * @param expression
	 * @return
	 */
	public static String convertCharToNumber(String expression) {
		Pattern p = Pattern.compile("'(.{1})'");

		Matcher m = p.matcher(expression);
		StringBuffer sb = new StringBuffer(expression.length());

		while (m.find()) {
			String str = m.group(1);
			m.appendReplacement(sb, Utils.getASCII(str.toCharArray()[0]) + "");
		}

		m.appendTail(sb);
		return sb.toString();
	}

	private static String replaceBasicSymbolicVariable(BasicSymbolicVariable var, String expression,
			IVariableNodeTable table) throws Exception {
		String value = var.getSymbolicValue();

		if (expression.startsWith("&")) {
			/*
			 * &a is ignored
			 */
			expression = expression.replace("&", " ahahah ");

			/*
			 * Replace the name of variable with its corresponding value
			 */
			if (value.matches(IRegex.NAME_REGEX))
				expression = expression.replaceAll("\\b" + var.getName() + "\\b", "(" + var.getSymbolicValue() + ")");
			else
				/*
				 * We put the value in "(...)" to prevent unexpected errors. For example, we
				 * have expression "1-x>0", and the value of x is -1. The rewrote expression is
				 * "1-(-1)>0".
				 */
				expression = expression.replaceAll("\\b" + var.getName() + "\\b", "(" + var.getSymbolicValue() + ")");

			expression = expression.replace(" ahahah ", "&");

		} else if (expression.startsWith("*")) {
			/*
			 * Ex: this case happens when parsing statement "int z = *px;"
			 */
			expression = ExpressionRewriterUtils.convertOneLevelPointerItemToArrayItem(expression);
			expression = ExpressionRewriterUtils.rewrite(table, expression);
		} else
			expression = expression.replaceAll("\\b" + var.getName() + "\\b", "(" + var.getSymbolicValue() + ")");
		return expression;
	}

	private static String replaceOneDimensionSymbolicVariable(OneDimensionSymbolicVariable var, String expression) {
		/*
		 * Replace the name of variable with the name of its block
		 */
		String newName = var.getBlock().getName();
		expression = expression.replaceAll("\\b" + var.getName() + "\\b", newName);

		for (LogicCell item : var.getBlock()) {
			String index = item.getIndex();
			String fullNameItem = newName + IRegex.SPACES + IRegex.OPENING_BRACKET + IRegex.SPACES + index
					+ IRegex.SPACES + IRegex.CLOSING_BRACKET + IRegex.SPACES;
			expression = expression.replaceAll(fullNameItem, item.getPhysicalCell().getValue());
		}

		return expression;

	}

	private static String replaceSimpleStructureSymbolicVariable(SimpleStructureSymbolicVariable var, String expression)
			throws Exception {
		if (var instanceof UnionSymbolicVariable) {
			/*
			 * Notice that all variables in union variable have the same value at every
			 * time.
			 */
			UnionNode unionNode = (UnionNode) var.getNode();
			List<IVariableNode> attributes = unionNode.getAttributes();

			final String DELIMITER = ClassvsStructNormalizer.DELIMITER;
			/*
			 * Replace all attributes in the expression with the first attribute because the
			 * value of all attributes in union instance is the same
			 */
			if (attributes.size() > 0) {
				String newName = var.getName() + DELIMITER + attributes.get(0).getName();
				for (IVariableNode attribute : attributes) {
					String currentName = var.getName() + DELIMITER + attribute.getName();
					expression = expression.replace(currentName, newName);
				}
			}
		} else if (var instanceof SimpleStructureSymbolicVariable) {
			SimpleStructureSymbolicVariable cast = (SimpleStructureSymbolicVariable) var;
			for (ISymbolicVariable castItem : cast.getAttributes())
				if (castItem instanceof BasicSymbolicVariable)
					if (expression.contains(var.getName() + "." + castItem.getName())) {
						expression = expression.replace(var.getName() + "." + castItem.getName(),
								((BasicSymbolicVariable) castItem).getSymbolicValue());
					}
		} else
			throw new Exception("Dont support this type: " + var.getType());
		return expression;

	}

	private static String replaceTwoLevelSymbolicVariable(TwoLevelSymbolicVariable var, String expression,
			IVariableNodeTable table) {

		if (var.getReference() != null) {
			Reference ref = var.getReference();
			expression = ExpressionRewriterUtils.convertTwoLevelPointerItemToArrayItem(expression);

			for (LogicCell logicCell : ref.getBlock()) {
				String realNameCell = var.getName() + logicCell.getIndex();
				expression = expression.replaceAll(Utils.toRegex(realNameCell), logicCell.getPhysicalCell().getValue());
			}

			/*
			 * If the pointer does not point to any block, we also replace its variable name
			 * with alias.
			 * 
			 * The position of pointer is updated in addition to the start index
			 */
			expression = expression.replaceAll("\\b" + Utils.toRegex(var.getName() + "["),
					ref.getBlock().getName() + "[" + ref.getStartIndex() + "+");
		} else {
			/*
			 * In this case, the pointer does not point to any location. We only replace the
			 * name of pointer with it alias!
			 */
			String nameVarRegex = "\\b" + var.getName() + "\\b";
			String symbolicNameVar = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName();
			expression = expression.replaceAll(nameVarRegex, symbolicNameVar);
		}
		return expression;
	}

	private static String replaceTwoDimensionSymbolicVariable(TwoDimensionSymbolicVariable var, String expression) {
		/*
		 * Replace the name of variable with the name of its block
		 */
		String newName = var.getBlock().getName();
		expression = expression.replaceAll("\\b" + var.getName() + "\\b", newName);

		for (LogicCell logicCell : var.getBlock()) {
			String index = logicCell.getIndex();
			String oldName = newName + IRegex.SPACES + Utils.toRegex(index) + IRegex.SPACES;
			expression = expression.replaceAll(oldName, logicCell.getPhysicalCell().getValue());
		}

		return expression;
	}

	private static String replaceOneLevelSymbolicVariable(OneLevelSymbolicVariable var, String expression) {
		if (var.getReference() != null) {
			String newName = var.getReference().getBlock().getName();
			expression = ExpressionRewriterUtils.convertOneLevelPointerItemToArrayItem(expression);

			for (LogicCell item : var.getReference().getBlock())
				/*
				 * In case: *p1==2 (where p1 is pointer, and a is primitive variable; we assign
				 * p1=&a before).
				 */
				if (VariableTypes.isBasic(var.getType())) {
					String oldIndex = "0";

					String oldItemName = "\\b" + var.getName() + IRegex.SPACES + IRegex.OPENING_BRACKET + IRegex.SPACES
							+ oldIndex + IRegex.SPACES + IRegex.CLOSING_BRACKET + IRegex.SPACES;

					String newItemName = newName;

					expression = expression.replaceAll(oldItemName, newItemName);

					expression = expression.replaceAll(newItemName, item.getPhysicalCell().getValue());

				} else if (VariableTypes.isOneLevel(var.getType()) || VariableTypes.isTwoLevel(var.getType())) {
					String oldIndex = item.getIndex();

					String newIndex = item.getIndex() + "+" + var.getReference().getStartIndex();

					String oldItemName = "\\b" + var.getName() + IRegex.SPACES + IRegex.OPENING_BRACKET + IRegex.SPACES
							+ oldIndex + IRegex.SPACES + IRegex.CLOSING_BRACKET + IRegex.SPACES;

					String newItemName = newName + "[" + newIndex + "]";

					expression = expression.replaceAll(oldItemName, newItemName);

					expression = expression.replace(newItemName, "(" + item.getPhysicalCell().getValue() + ")");
				} else {
					// nothing to to
				}

			/*
			 * If the pointer does not point to any block, we also replace it variable name
			 * with alias.
			 * 
			 * The position of pointer is updated in addition with the start index.
			 * 
			 * 
			 */
			expression = expression.replaceAll("\\b" + var.getName() + "\\b" + IRegex.SPACES + IRegex.OPENING_BRACKET,
					var.getReference().getBlock().getName() + "[" + var.getReference().getStartIndex() + "+");
		} else {
			/*
			 * In this case, the pointer does not point to any location. We only replace the
			 * name of pointer with it alias!
			 */
			String oldItemName = "\\b" + var.getName() + "\\b";

			expression = expression.replaceAll(oldItemName, ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName());
		}
		return expression;
	}

	/**
	 * Rewrite the expression based on the table of variable.
	 * <p>
	 * <br/>
	 * For example, the expression is "x>2". But in the table, the value of x is
	 * equivalent to 5. So the expression is rewrote as "5>2".
	 * <p>
	 * <br/>
	 * Another example, we have two pointers named p1 and p2. But "p1 = p2 + 1;" (it
	 * means that pointer p1 points to the second element in p2). We have the
	 * expression "*(p1)==*(p2+2)". It will be transformed as "p2[1]==p2[2]".
	 *
	 * @param table
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static String rewrite(IVariableNodeTable table, String expression) throws Exception {
		expression = ExpressionRewriterUtils.transformFloatPositiveE(expression);
		expression = ExpressionRewriterUtils.transformFloatNegativeE(expression);
		expression = ExpressionRewriterUtils.convertCharToNumber(expression);
		expression = ExpressionRewriterUtils.transformSizeof(expression);
		String rExcludingSingleQuote = "[^']";
		expression = expression.replaceAll(ExpressionRewriterUtils.group(rExcludingSingleQuote) + IRegex.SPACES, "$1");

		expression = ExpressionRewriterUtils.shortenExpressionInParentheses(expression);
		expression = ExpressionRewriterUtils.tranformNegative(expression);

		/*
		 * For all variables in the table
		 */
		for (ISymbolicVariable symbolicVariable : table.getVariables())
			if (expression.contains(symbolicVariable.getName()))
				/*
				 * If the variable belongs to basic type, we replace its name with its
				 * corresponding value
				 */
				if (symbolicVariable instanceof BasicSymbolicVariable)
					expression = ExpressionRewriterUtils
							.replaceBasicSymbolicVariable((BasicSymbolicVariable) symbolicVariable, expression, table);
				else
				/*
				 * If the variable type belongs to one dimension, Ex: int[2]
				 */
				if (symbolicVariable instanceof OneDimensionSymbolicVariable)
					expression = ExpressionRewriterUtils.replaceOneDimensionSymbolicVariable(
							(OneDimensionSymbolicVariable) symbolicVariable, expression);
				else
				/*
				 * If the variable type belongs to one level pointer, Ex: int*
				 */
				if (symbolicVariable instanceof OneLevelSymbolicVariable)
					expression = ExpressionRewriterUtils
							.replaceOneLevelSymbolicVariable((OneLevelSymbolicVariable) symbolicVariable, expression);
				else
				/*
				 * If the variable type belongs to structure (class, struct, union)
				 */
				if (symbolicVariable instanceof SimpleStructureSymbolicVariable)
					expression = ExpressionRewriterUtils.replaceSimpleStructureSymbolicVariable(
							(SimpleStructureSymbolicVariable) symbolicVariable, expression);
				else if (symbolicVariable instanceof TwoLevelSymbolicVariable)
					expression = ExpressionRewriterUtils.replaceTwoLevelSymbolicVariable(
							(TwoLevelSymbolicVariable) symbolicVariable, expression, table);
				else if (symbolicVariable instanceof TwoDimensionSymbolicVariable)
					expression = ExpressionRewriterUtils.replaceTwoDimensionSymbolicVariable(
							(TwoDimensionSymbolicVariable) symbolicVariable, expression);
				else {
					// dont handle
				}

		expression = ExpressionRewriterUtils.shortenExpressionInBracket(expression);
		expression = new CustomJeval().evaluate(expression);
		return expression;
	}

	/**
	 * Convert negative number x. For example, (-3) ----convert-----> (-1)*3
	 *
	 * @param expression
	 * @return
	 */
	private static String tranformNegative(String expression) {
		String output = "";
		expression = "@" + expression;
		output = expression.replaceAll("@\\s*-{1}(\\s*[a-zA-Z0-9_.]+)", "((-1)*$1)");
		output = output.replaceAll("([\\+\\-\\*\\/$])\\s*-{1}(\\s*[a-zA-Z0-9_.]+)", "$1((-1)*$2)");
		output = output.replaceAll("--(\\s*[a-zA-Z0-9_\\.]+)", "+$1");
		if (output.startsWith("@"))
			output = output.replaceFirst("@", "");
		return output;
	}

	/**
	 * Ex: "size of(int)" ------> "4"
	 *
	 * @param expression
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static String transformSizeof(String expression) throws IllegalArgumentException, IllegalAccessException {
		if (expression.contains("size ") || expression.contains("size"))
			for (String type : VariableTypes.getAllBasicFieldNames(BASIC.class)) {
				int size = VariablesSize.getSizeofTypeInByte(type);
				String sizeOf = "size of ( " + type + " )";
				expression = expression.replaceAll(Utils.toRegex(sizeOf), size + "");
			}
		return expression;
	}

	/**
	 * Ex:"5E2"------> "5*100"
	 *
	 * @param expression
	 * @return
	 */
	private static String transformFloatPositiveE(String expression) {
		String regex = "[eE]\\+(" + IRegex.POSITIVE_INTEGER_REGEX + ")";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(expression);

		while (m.find()) {
			String eE = m.group(1);

			int shorten = 1;
			int eEInt = Utils.toInt(eE);
			for (int i = 1; i < eEInt; i++)
				shorten *= 10;
			expression = expression.replace(m.group(0), "*" + shorten);
		}

		return expression;
	}

	/**
	 * Ex:"5E-2"------> "5*0.01"
	 *
	 * @param expression
	 * @return
	 */
	private static String transformFloatNegativeE(String expression) {
		String regex = "[eE]-(" + IRegex.POSITIVE_INTEGER_REGEX + ")";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(expression);

		while (m.find()) {
			String eE = m.group(1);

			String shorten = "0.";
			int eEInt = Utils.toInt(eE);
			for (int i = 1; i < eEInt; i++)
				shorten += "0";
			shorten += "1";
			expression = expression.replace(m.group(0), "*" + shorten);
		}

		return expression;
	}
}

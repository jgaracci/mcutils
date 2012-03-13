package org.rtd.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class ProblemTypeFactory {
	private static Map<String, ProblemTypeFactory> factoriesByType = new HashMap<String, ProblemTypeFactory>();
	
	static {
		factoriesByType.put("MATH", new MathProblemTypeFactory());
	}

	public static ProblemTypeFactory createFactoryFor(String type) {
		return factoriesByType.get( type.toUpperCase() );
	}

	public abstract Problem createProblemFor(String type, Integer level);
	
	static class MathProblemTypeFactory extends ProblemTypeFactory {
		private static Random random = new Random();
		
		private Map<String, ProblemFactory> factoriesByType = new HashMap<String, ProblemFactory>();
		{
			factoriesByType.put("+", new AdditionProblemFactory());
			factoriesByType.put("-", new SubtractionProblemFactory());
			factoriesByType.put("*", new MultiplicationProblemFactory());
			factoriesByType.put("/", new DivisionProblemFactory());
		}
		
		@Override
		public Problem createProblemFor(String type, Integer level) {
			return factoriesByType.get(type).createProblem(level);
		}
		
		private static class AdditionProblemFactory implements ProblemFactory {
			@Override
			public Problem createProblem(Integer level) {
				return new MathProblem(createArgument(level), createArgument(level), "+");
			}
		}
		
		private static class SubtractionProblemFactory implements ProblemFactory {
			@Override
			public Problem createProblem(Integer level) {
				return new MathProblem(createArgument(level), createArgument(level), "-");
			}
		}
		
		private static class MultiplicationProblemFactory implements ProblemFactory {
			@Override
			public Problem createProblem(Integer level) {
				return new MathProblem(createArgument(level), createArgument(level), "*");
			}
		}
		
		private static class DivisionProblemFactory implements ProblemFactory {
			@Override
			public Problem createProblem(Integer level) {
				int arg1 = createArgument(level);
				int arg2 = createArgument(level) + 1;
				int actualArg1 = arg1 * arg2;
				int actualArg2 = arg2;
				return new MathProblem(actualArg1, actualArg2, "/");
			}
		}
		
		private static int createArgument(Integer level) {
			return random.nextInt( 1 << (3 * level) );
		}
	}
	
	static class MathProblem implements Problem {
		private int arg1;
		private int arg2;
		private String operator;
		
		MathProblem(int arg1, int arg2, String operator) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.operator = operator;
		}
		
		@Override
		public String toDisplayString() {
			return arg1 + " " + operator + " " + arg2;
		}

		@Override
		public boolean isCorrectAnswer(String answer) {
			return getAnswer().equals(answer);
		}

		@Override
		public String getAnswer() {
			return "+".equals(operator) ? String.valueOf(arg1 + arg2) :
				"-".equals(operator) ? String.valueOf(arg1 - arg2) :
				"*".equals(operator) ? String.valueOf(arg1 * arg2) :
				"/".equals(operator) ? String.valueOf(arg1 / arg2) : "";
		}
	}
}

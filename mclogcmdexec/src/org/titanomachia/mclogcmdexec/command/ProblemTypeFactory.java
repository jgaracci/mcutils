package org.titanomachia.mclogcmdexec.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class ProblemTypeFactory<T extends Problem<?>> {
	private static Map<String, ProblemTypeFactory<?>> factoriesByType = new HashMap<String, ProblemTypeFactory<?>>();
	
	static {
		factoriesByType.put("MATH", new MathProblemTypeFactory());
	}

	public static ProblemTypeFactory<?> createFactoryFor(String type) {
		return factoriesByType.get( type.toUpperCase() );
	}

	public abstract T createProblemFor(String type, Integer level);
	
	static class MathProblemTypeFactory extends ProblemTypeFactory<MathProblem> {
		private static Random random = new Random();
		
		private Map<String, ProblemFactory<MathProblem>> factoriesByType = new HashMap<String, ProblemFactory<MathProblem>>();
		{
			factoriesByType.put("+", new AdditionProblemFactory());
			factoriesByType.put("-", new SubtractionProblemFactory());
			factoriesByType.put("*", new MultiplicationProblemFactory());
			factoriesByType.put("/", new DivisionProblemFactory());
		}
		
		@Override
		public MathProblem createProblemFor(String type, Integer level) {
			return factoriesByType.get(type).createProblem(level);
		}
		
		private static class AdditionProblemFactory implements ProblemFactory<MathProblem> {
			@Override
			public MathProblem createProblem(Integer level) {
				return new MathProblem(createArgument(level), createArgument(level), level, 1, "+");
			}
		}
		
		private static class SubtractionProblemFactory implements ProblemFactory<MathProblem> {
			@Override
			public MathProblem createProblem(Integer level) {
				return new MathProblem(createArgument(level), createArgument(level), level, 1, "-");
			}
		}
		
		private static class MultiplicationProblemFactory implements ProblemFactory<MathProblem> {
			@Override
			public MathProblem createProblem(Integer level) {
				return new MathProblem(createArgument(level), createArgument(level), level, 2, "*");
			}
		}
		
		private static class DivisionProblemFactory implements ProblemFactory<MathProblem> {
			@Override
			public MathProblem createProblem(Integer level) {
				int arg1 = createArgument(level);
				int arg2 = createArgument(level) + 1;
				int actualArg1 = arg1 * arg2;
				int actualArg2 = arg2;
				return new MathProblem(actualArg1, actualArg2, level, 3, "/");
			}
		}
		
		private static int createArgument(Integer level) {
			return random.nextInt( 1 << (3 * level) );
		}
	}
	
	static class MathProblem implements Problem<MathProblem> {
		private int arg1;
		private int arg2;
		private Integer level;
		private String operator;
		private Integer baseValue;
		
		MathProblem(int arg1, int arg2, Integer level, Integer baseValue, String operator) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.level = level;
			this.baseValue = baseValue;
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

		@Override
        public Integer getLevel() {
			return level;
		}
		
		public String getOperator() {
			return operator;
		}

		@Override
		public Integer calculatePoints(List<MathProblem> streak) {
			int equalProblemLevelStreak = 1;
			for (MathProblem problem : streak) {
				if (problem.getLevel().equals(level) && problem.operator.equals(operator)) {
					equalProblemLevelStreak++;
				}
			}
			
			if (equalProblemLevelStreak > 10) {
			    equalProblemLevelStreak = 10;
			}
			
			return equalProblemLevelStreak * level * baseValue;
		}
	}
}

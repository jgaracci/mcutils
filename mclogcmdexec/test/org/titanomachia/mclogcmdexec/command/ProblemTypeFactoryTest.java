package org.titanomachia.mclogcmdexec.command;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.titanomachia.mclogcmdexec.command.ProblemTypeFactory.MathProblem;

public class ProblemTypeFactoryTest {

	@Test
	public void test() {
		MathProblem problem = new MathProblem(0, 0, 1, 1, "+");
		List<MathProblem> streak = new ArrayList<MathProblem>();
		Assert.assertEquals(1 * 1 * 1, (int)problem.calculatePoints(streak));
		
		streak.add(new MathProblem(0, 0, 1, 1, "+"));
		Assert.assertEquals(2 * 1 * 1, (int)problem.calculatePoints(streak));
		
		streak.add(new MathProblem(0, 0, 1, 1, "+"));
		Assert.assertEquals(3 * 1 * 1, (int)problem.calculatePoints(streak));
		
		problem = new MathProblem(0, 0, 1, 2, "*");
		streak.add(new MathProblem(0, 0, 1, 2, "*"));
		Assert.assertEquals(2 * 1 * 2, (int)problem.calculatePoints(streak));

		problem = new MathProblem(0, 0, 1, 3, "/");
		Assert.assertEquals(1 * 1 * 3, (int)problem.calculatePoints(streak));
		
		streak.add(new MathProblem(0, 0, 1, 3, "/"));
		Assert.assertEquals(3 * 1 * 2, (int)problem.calculatePoints(streak));
	}

}

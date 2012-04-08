package org.titanomachia.mclogcmdexec.command;

import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Slots extends Command {
	enum Outcome {
		// See the slots_odds.ods Spreadsheet for adjusting these
		Bell("Bell", 			16, 95, 96, 95),
		DoubleBar("Double Bar", 10, 93, 94, 65),
		Grapes("Grapes", 		12, 90, 91, 42), 
		Orange("Orange", 		 8, 80, 87, 40), 
		Apple("Apple", 			 4, 80, 80, 20), 
		Bar("Bar", 				 5, 72, 72, 30),
		Cherry("Cherry", 		 1, 68, 68, 70), 
		Lemon("Lemon", 			 3, 60, 60, 45), 
		Melon("Melon", 			 2, 54,  0, 30),
		Banana("Banana", 		 1,  0, 40,  0),
		;
		
		private String name;
		private int payout;
		private int[] probs;

		private Outcome(String name, int payout, int... probs) {
			this.name = name;
			this.payout = payout;
			this.probs = probs;
		}

		public String getName(){
			return name;
		}

		public int getPayout() {
			return payout;
		}
		
		public int[] getProbs() {
			return probs;
		}
	};

	private Outcome determineOutcome(int prob, int col) {
		Outcome determined = null;
		for(Outcome outcome : Outcome.values()) {
			if(outcome.getProbs()[col] <= prob) {
				determined = outcome;
				break;
			}
		}	
		return determined;
	}

	public int determinePayout(Outcome[] outcomes, int wager, boolean report) {
		int payout = 0;
		
		Integer jackpot = ApplicationContext.getValue("slots.jackpot");

		// three of a kind
		if (outcomes[0] == outcomes[1] && outcomes[1] == outcomes[2]) {
			// Triple Bell wins the current jackpot
			if (outcomes[0] == Outcome.Bell) {
				payout = jackpot;

				// Only do this stuff if we aren't determining for payout report
				if (!report) {
					CommandUtils.writeToConsole("*** Jackpot!!! ***", getUser());
					
					ApplicationContext.setValue("slots.jackpot", 100);
				}
			}
			else if (outcomes[0] == Outcome.Cherry) {
				// Triple Cherry has special payout
				payout = 12 * wager;
			}
			else {
				// Any other three of a kind is 3 times wager times payout
				payout = outcomes[0].getPayout() * 3 * wager;
			}
		}
		else if (outcomes[0] == outcomes[1]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[0] == Outcome.Bar || outcomes[0] == Outcome.DoubleBar || outcomes[2] == Outcome.Cherry) {
				payout = outcomes[0].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[2] == Outcome.Bar || outcomes[2] == Outcome.DoubleBar || outcomes[0] == Outcome.Cherry) {
				payout = outcomes[0].getPayout() * 2 * wager;
			}
		}
		else if (outcomes[1] == outcomes[2]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[1] == Outcome.Bar || outcomes[1] == Outcome.DoubleBar || outcomes[0] == Outcome.Cherry) {
				payout = outcomes[1].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[0] == Outcome.Bar || outcomes[0] == Outcome.DoubleBar || outcomes[1] == Outcome.Cherry) {
				payout = outcomes[1].getPayout() * 2 * wager;
			}
		}
		else if (outcomes[0] == outcomes[2]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[0] == Outcome.Bar || outcomes[0] == Outcome.DoubleBar || outcomes[1] == Outcome.Cherry) {
				payout = outcomes[0].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[1] == Outcome.Bar || outcomes[1] == Outcome.DoubleBar || outcomes[0] == Outcome.Cherry) {
				payout = outcomes[0].getPayout() * 2 * wager;
			}
		}
		else {
			// Any single Cherry basically returns the wager
			boolean cherry = outcomes[0] == Outcome.Cherry ||
				outcomes[1] == Outcome.Cherry ||
				outcomes[2] == Outcome.Cherry;
			if (cherry) {
				payout = Outcome.Cherry.getPayout() * wager;
			}
		}

		return payout;
	}
	
	@Override
	public String getDescription() {
		return "Plays slots by wagering your points, e.g. /slots 50";
	}

	@Override
	public void execute() {
		Integer jackpot = ApplicationContext.getValue("slots.jackpot");
		if (null == jackpot || jackpot < 100) {
			jackpot = 100;
		}
		
		if (StringUtils.isEmpty(getArgs().trim())) {
			CommandUtils.writeToConsole("The current jackpot is " + jackpot, getUser());
			return;
		}
		
		if ("POINTS".equals(getArgs().toUpperCase())) {
			Integer points = ApplicationContext.getValue("slots.points." + getUser());
			if (null == points) {
				points = 100;
			}
			else {
				points += 100;
			}
			ApplicationContext.setValue("slots.points." + getUser(), points);
			CommandUtils.writeToConsole("You now have " + points + " points", getUser());
			return;
		}
		
		if ("PAYOUT".equals(getArgs().toUpperCase())) {
			CommandUtils.writeToConsole("Slots Payouts", getUser());
			int payout = 0;
			for(Outcome outcome : Outcome.values()) {
				payout = determinePayout(new Outcome[]{outcome,outcome,outcome}, 1, true);
				if (Outcome.Bell == outcome) {
					CommandUtils.writeToConsole("3 " + outcome.getName() + " --> *** JACKPOT ***", getUser());
				}
				else {
					CommandUtils.writeToConsole("3 " + outcome.getName() + " --> " + payout, getUser());
				}
				if (Outcome.Bar != outcome && Outcome.DoubleBar != outcome) {
					payout = determinePayout(new Outcome[]{outcome,outcome,Outcome.Bar}, 1, true);
					CommandUtils.writeToConsole("Any 2 " + outcome.getName() + " + Bar(s) --> " + payout, getUser());
				}
				else {
					payout = determinePayout(new Outcome[]{outcome,outcome,Outcome.Banana}, 1, true);
					CommandUtils.writeToConsole("2 " + outcome.getName() + " + Any --> " + payout, getUser());
				}
			}	
			payout = determinePayout(new Outcome[]{Outcome.Banana, Outcome.Cherry, Outcome.Banana}, 1, true);
			CommandUtils.writeToConsole("Any + " + Outcome.Cherry + " --> " + payout, getUser());
			return;
		}
		
		if ("ODDS".equals(getArgs().toUpperCase())) {
			CommandUtils.writeToConsole("Slots Odds", getUser());
			int totalOdds = 0;
			for(Outcome outcome : Outcome.values()) {
				int odds = (100 - outcome.getProbs()[0]) * (100 - outcome.getProbs()[1]) * (100 - outcome.getProbs()[2]);
				if (Outcome.Banana == outcome) {
					odds = 1000000 - totalOdds;
				}
				else {
					totalOdds += odds;
				}
				CommandUtils.writeToConsole(outcome.getName() + " --> 1 in " + Math.round(1000000 / (double)odds), getUser());
			}	
			return;
		}
		
		Integer points = ApplicationContext.getValue("slots.points." + getUser());
		if (null == points || points == 0) {
			CommandUtils.writeToConsole("You have no points to play with", getUser());
			return;
		}
		
		Integer wager = null;
		try {
			wager = Integer.valueOf(getArgs());
			if (wager > points) {
				CommandUtils.writeToConsole("You don't have that many points", getUser());
				return;
			}
		}
		catch (NumberFormatException e) {
			showUsage();
			return;
		}
		
		Random rnd = new Random();

		Outcome[] outcomes = new Outcome[3];
		outcomes[0] = determineOutcome(rnd.nextInt(100), 0);
		outcomes[1] = determineOutcome(rnd.nextInt(100), 1);
		outcomes[2] = determineOutcome(rnd.nextInt(100), 2);
		
		CommandUtils.writeToConsole("[ " + outcomes[0].getName() + " | " + outcomes[1].getName() + " | " + outcomes[2].getName() + " ]", getUser());

		points -= wager;
		int payout = determinePayout(outcomes, wager, false);
		if (payout > 0) {
			CommandUtils.writeToConsole("You won " + payout, getUser());
			points += payout;
			CommandUtils.writeToConsole("You now have " + points + " points", getUser());
		}
		else {
			CommandUtils.writeToConsole("Sorry, try again", getUser());
			CommandUtils.writeToConsole("You now have " + points + " points", getUser());
			if (wager > 10) {
				jackpot += 10;
			}
			else {
				jackpot += wager;
			}
			ApplicationContext.setValue("slots.jackpot", jackpot);
		}
		
		ApplicationContext.setValue("slots.points." + getUser(), points);
	}

	private void showUsage() {
		CommandUtils.writeToConsole("Usage: /slots {wager}", getUser());
		CommandUtils.writeToConsole("/slots by itself will show the current jackpot", getUser());
	}
}

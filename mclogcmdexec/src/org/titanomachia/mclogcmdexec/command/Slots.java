package org.titanomachia.mclogcmdexec.command;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.commons.lang.StringUtils;
import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Slots extends Command {
	enum Outcome {
		// See the slots_odds.ods Spreadsheet for adjusting these
		Creeper("Creeper", 		16), 
		Cow("Cow",              10),
		Skeleton("Skeleton", 	12),
		Zombie("Zombie", 		 8),
		Pig("Pig", 		         5),
		Apple("Apple", 			 4), 
		Chicken("Chicken", 		 1),
		Sheep("Sheep", 			 3),
		Melon("Melon", 			 2),
		Spider("Spider", 		 1),
		;
		
		private String name;
		private int payout;

		private Outcome(String name, int payout) {
			this.name = name;
			this.payout = payout;
		}

		public String getName(){
			return name;
		}

		public int getPayout() {
			return payout;
		}
	};
	
	public Map<Outcome, int[]> getProbs() {
		Map<Outcome, int[]> probs = new HashMap<Outcome, int[]>();
		
		probs.put(Outcome.Creeper,   new int[] { 90, 90, 90 });
		probs.put(Outcome.Cow,       new int[] { 85, 85, 85 });
		probs.put(Outcome.Skeleton,  new int[] { 80, 80, 80 });
		probs.put(Outcome.Zombie,    new int[] { 70, 70, 70 });
		probs.put(Outcome.Pig,       new int[] { 65, 65, 65 });
		probs.put(Outcome.Apple,     new int[] { 60, 60, 60 });
		probs.put(Outcome.Chicken,   new int[] { 55, 55, 55 });
		probs.put(Outcome.Sheep,     new int[] { 60, 60,  0 });
		probs.put(Outcome.Melon,     new int[] { 45,  0, 45 });
		probs.put(Outcome.Spider,    new int[] {  0, 40, 40 });
		
		return probs;
	}

	private Outcome determineOutcome(int prob, int col) {
		Outcome determined = null;
		for(Outcome outcome : Outcome.values()) {
			if(getProbs().get(outcome)[col] <= prob) {
				determined = outcome;
				break;
			}
		}	
		return determined;
	}

	public int determinePayout(Outcome[] outcomes, int wager) {
		int payout = 0;
		
		Integer jackpot = getContextValue("slots.jackpot");
		if (null == jackpot) {
			jackpot = 100;
		}
		
		jackpot += (wager + 4) / 5;

		// three of a kind
		if (outcomes[0] == outcomes[1] && outcomes[1] == outcomes[2]) {
			// Triple Bell wins the current jackpot
			if (outcomes[0] == Outcome.Creeper) {
				payout = jackpot;

				displayMessage("*** Jackpot!!! ***", getUser());
				
				jackpot = 100;
			}
			else if (outcomes[0] == Outcome.Chicken) {
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
			if (outcomes[0] == Outcome.Pig || outcomes[0] == Outcome.Cow || outcomes[2] == Outcome.Chicken) {
				payout = outcomes[0].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[2] == Outcome.Pig || outcomes[2] == Outcome.Cow) {
				payout = outcomes[0].getPayout() * 2 * wager;
			}
			else if (outcomes[0] == Outcome.Chicken) {
			    payout = outcomes[0].getPayout() * 4 * wager;
			}
		}
		else if (outcomes[1] == outcomes[2]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[1] == Outcome.Pig || outcomes[1] == Outcome.Cow || outcomes[0] == Outcome.Chicken) {
				payout = outcomes[1].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[0] == Outcome.Pig || outcomes[0] == Outcome.Cow) {
				payout = outcomes[1].getPayout() * 2 * wager;
			}
            else if (outcomes[0] == Outcome.Chicken) {
                payout = outcomes[1].getPayout() * 4 * wager;
            }
		}
		else if (outcomes[0] == outcomes[2]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[0] == Outcome.Pig || outcomes[0] == Outcome.Cow || outcomes[1] == Outcome.Chicken) {
				payout = outcomes[0].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[1] == Outcome.Pig || outcomes[1] == Outcome.Cow) {
				payout = outcomes[0].getPayout() * 2 * wager;
			}
            else if (outcomes[0] == Outcome.Chicken) {
                payout = outcomes[0].getPayout() * 4 * wager;
            }
		}
		else {
			// Any single Cherry basically returns the wager
			boolean cherry = outcomes[0] == Outcome.Chicken ||
				outcomes[1] == Outcome.Chicken ||
				outcomes[2] == Outcome.Chicken;
			if (cherry) {
				payout = Outcome.Chicken.getPayout() * wager;
			}
		}
		
		setContextValue("slots.jackpot", jackpot);

		return payout;
	}

	@Override
	public String getDescription() {
		return "Plays slots by wagering your points, e.g. /slots 50";
	}

	@Override
	public void execute() {
		Integer points = getContextValue("quiz.points." + getUser());
		if (null == points) {
			points = 0;
		}
		
		if (StringUtils.isEmpty(getArgs().trim())) {
			Integer jackpot = getContextValue("slots.jackpot");
			if (null == jackpot) {
				jackpot = 100;
			}
			showJackpot(jackpot);
			displayMessage("You have " + points + " points", getUser());
			return;
		}
		
		if ("PAYOUT".equals(getArgs().toUpperCase())) {
			List<String> payouts = new ArrayList<String>();
			
			payouts.add("    ============ Slots Payouts ============");
			payouts.add("    CREEPER CREEPER CREEPER   [JACKPOT]");
            payouts.add("    THREE MATCHING                [3 * Value]" );
            payouts.add("    CHICKEN CHICKEN CHICKEN    [12]" );
			payouts.add("    ANY TWO COW|PIG                [COW|PIG Value]" );
            payouts.add("    TWO MATCHING + COW|PIG      [2 * Value]" );
            payouts.add("    TWO CHICKEN    [" + 4 * Outcome.Chicken.payout + "]     ANY CHICKEN    [" + Outcome.Chicken.payout + "]");
            payouts.add("        ------------ Payout Values ------------");
            int i = 0;
            String payoutStr = "";
            for(Outcome outcome : Outcome.values()) {
                if (outcome != Outcome.Chicken) {
                    payoutStr += StringUtils.rightPad(outcome.getName() + " [" + outcome.getPayout() + "]", 8);
                    i++;
                    if (i % 3 == 0) {
                        payouts.add("     " + payoutStr);
                        payoutStr = "";
                    }
                    else {
                        payoutStr += "     ";
                    }
                }
			}	
			
			displayMessages(payouts.toArray(new String[0]), getUser());
			return;
		}
		
		if ("ODDS".equals(getArgs().toUpperCase())) {
			List<String> outcomes = new ArrayList<String>();
			outcomes.add("    ============= Slots Odds =============");
            String oddsStr = "";
            int j = 0;
			for(int i = 0; i < Outcome.values().length;) {
				int probs[] = getProbs().get(Outcome.values()[i / 2 + j]);
				int prob0 = probs[0];
				int prob1 = probs[1];
				int prob2 = probs[2];
				int odds = 1000000 / ((100 - prob0) * (100 - prob1) * (100 - prob2));
				oddsStr += StringUtils.rightPad(Outcome.values()[i / 2 + j].getName(), 8) + " [1 in " + odds + "]";
				i++;
                if (i % 2 == 0) {
                    outcomes.add("     " + oddsStr);
                    oddsStr = "";
                }
                else {
                    oddsStr += "     ";
                }
                j += 5;
                j %= 10;
			}	
			
			displayMessages(outcomes.toArray(new String[0]), getUser());
			return;
		}
		
		String args = getArgs();
		StringTokenizer tokenizer = new StringTokenizer(args, " ");
		
		Integer wager = null;
		try {
			wager = Integer.valueOf(tokenizer.nextToken());
			if (wager > points) {
				displayMessage("You don't have that many points", getUser());
				return;
			}
		}
		catch (NumberFormatException e) {
			showUsage();
			return;
		}
		
		Integer turns = null;
		if (tokenizer.hasMoreTokens()) {
			try {
				turns = Integer.valueOf(tokenizer.nextToken());
			}
			catch (NumberFormatException e) {
				showUsage();
				return;
			}
		}
		
		Random rnd = new Random();

		if (null == turns) {
			turns = 1;
		}
		
		int i = 0;
		int net = 0;
		List<String> messages = new ArrayList<String>();
		for ( ; i < turns && points >= wager; i++) {
			Outcome[] outcomes = new Outcome[3];
			outcomes[0] = determineOutcome(rnd.nextInt(100), 0);
			outcomes[1] = determineOutcome(rnd.nextInt(100), 1);
			outcomes[2] = determineOutcome(rnd.nextInt(100), 2);
			
			String outcomeString = "[ " + outcomes[0].getName() + " | " + outcomes[1].getName() + " | " + outcomes[2].getName() + " ]";

			if (turns == 1) {
				messages.add(outcomeString);
			}
			else {
				showOutcome(outcomeString, getUser());
			}
			
			net -= wager;
			points -= wager;
			int payout = determinePayout(outcomes, wager);
			if (payout > 0) {
				points += payout;
				net += payout;
				if (turns == 1) {
					messages.add("You won " + payout);
				}
			}
			else {
				if (turns == 1) {
					messages.add("Sorry, try again");
				}
			}
		}
				
		if (i < turns) {
			messages.add("Not enough points to continue...");
		}
		
		if (turns > 1) {
			messages.add("Your net winnings => " + net + " points");
		}
		
		displayMessages(messages.toArray(new String[messages.size()]), getUser());

		if (turns > 1) {
			Integer jackpot = getContextValue("slots.jackpot");
			showJackpot(jackpot);
			
			showPoints(points);
		}
		
		setContextValue("quiz.points." + getUser(), points);
	}

	protected void showJackpot(Integer jackpot) {
		displayMessage("The current jackpot is now " + jackpot + " points", getUser());
	}

	private void showUsage() {
		String[] messages = new String[] {
				"Usage: /slots {wager}",
				"/slots by itself will show the current jackpot"
		};
		displayMessages(messages, getUser());
	}
	
	protected void showOutcome(String message, String user) {
//		CommandUtils.writeToConsole(message, user);
	}
	
	protected void showPoints(Integer points) {
		displayMessage("You now have " + points + " points", getUser());
	}
	
	protected void displayMessage(String message, String user) {
		CommandUtils.writeToConsole(message, user);
	}
	
	protected void displayMessages(String[] messages, String user) {
		for(String message : messages) {
			CommandUtils.writeToConsole(message, user);
		}
	}

	@SuppressWarnings("unchecked")
	protected <E> E getContextValue(String name) {
		return (E)ApplicationContext.getValue(name);
	}
	
	protected void setContextValue(String name, Object value) {
		ApplicationContext.setValue(name, value);
	}
	
	public static void main(String[] args) {
		try {
			final SlotsModel model = new SlotsModel();
			model.initialize();
			
			final SlotsView view = new SlotsView();
			view.setModel(model);
			
			final Slots slots = new Slots() {
				private Map<String, Object> context = new HashMap<String, Object>();
				
				@Override
				protected void displayMessage(String message, String user) {
					model.setMessages(new String[] {message});
				}
				
				@Override
				protected void displayMessages(String[] messages, String user) {
					model.setMessages(messages);
				}

				@Override
				protected void showOutcome(String message, String user) {
					StringTokenizer tokenizer = new StringTokenizer(message, "[]|");
					model.setOutcome1(tokenizer.nextToken().trim());
					model.setOutcome2(tokenizer.nextToken().trim());
					model.setOutcome3(tokenizer.nextToken().trim());
				}

				@Override
				protected void showPoints(Integer points) {
					model.setCoins(points);
				}

				@Override
				protected void showJackpot(Integer points) {
					model.setJackpot(points);
				}

				@Override
				@SuppressWarnings("unchecked")
				protected <E> E getContextValue(String name) {
					return (E)context.get(name);
				}

				@Override
				protected void setContextValue(String name, Object value) {
					context.put(name, value);
				}
			};
			
			view.setPullAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					slots.setUser("test");
					slots.setArgs(model.getWager());
					slots.execute();
				}
			});
			
			view.setOddsAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					slots.setUser("test");
					slots.setArgs("ODDS");
					slots.execute();
				}
			});
			
			view.setAddCoinsAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer points = slots.getContextValue("quiz.points.test");
					if (null == points) {
						points = 0;
					}
					slots.setContextValue("quiz.points.test", points + 1000);
					slots.showPoints(points + 1000);
					slots.setArgs("");
					slots.setUser("test");
					slots.execute();
				}
			});
			
			model.setJackpot(100);
			
			JFrame frame = new JFrame("Slots");
			frame.getContentPane().add(view.getComponent());
			frame.setSize(240, 200);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
	
	static class SlotsModel {
		private Document coinsModel;
		private Document outcome1Model;
		private Document outcome2Model;
		private Document outcome3Model;
		private Document messagesModel;
		private Document wagerModel;
		private Document jackpotModel;

		public void initialize() {
			coinsModel = new PlainDocument();
			outcome1Model = new PlainDocument();
			outcome2Model = new PlainDocument();
			outcome3Model = new PlainDocument();
			wagerModel = new PlainDocument();
			jackpotModel = new PlainDocument();
			messagesModel = new PlainDocument();
		}

		public Document getCoinsModel() {
			return coinsModel;
		}

		public Document getOutcome1Model() {
			return outcome1Model;
		}

		public Document getOutcome2Model() {
			return outcome2Model;
		}

		public Document getOutcome3Model() {
			return outcome3Model;
		}
		
		public Document getMessagesModel() {
			return messagesModel;
		}

		public Document getWagerModel() {
			return wagerModel;
		}

		public Document getJackpotModel() {
			return jackpotModel;
		}

		public Integer getCoins() {
			return getInteger(coinsModel);
		}

		public void setCoins(Integer coins) {
			setText(coinsModel, String.valueOf(coins));
		}
		
		public void setOutcome1(String outcome) {
			setText(outcome1Model, outcome);
		}
		
		public void setOutcome2(String outcome) {
			setText(outcome2Model, outcome);
		}
		
		public void setOutcome3(String outcome) {
			setText(outcome3Model, outcome);
		}
		
		public void setMessages(final String[] messages) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						messagesModel.remove(0, messagesModel.getLength());
						int offset = 0;
						for(String message : messages) {
							messagesModel.insertString(offset, message + "\n", null);
							offset += message.length() + 1;
						}
					} catch (BadLocationException e) {
					}
				}
			});
		}
		
		private Integer getInteger(Document document) {
			Integer integer = null;
			
			try {
				integer = Integer.valueOf(getText(document));
			}
			catch(NumberFormatException e) {
			}
			
			return integer;
		}
		
		public String getWager() {
			return getText(wagerModel);
		}
		
		public void setWager(Integer wager) {
			setText(wagerModel, String.valueOf(wager));
		}
		
		public void setJackpot(Integer jackpot) {
			setText(jackpotModel, String.valueOf(jackpot));
		}
		
		private void setText(final Document document, final String text) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						document.remove(0, document.getLength());
						document.insertString(0, text, null);
					} catch (BadLocationException e) {
					}
				}
			});
		}
		
		private String getText(Document document) {
			String text = null;
			try {
				text = document.getText(0, document.getLength());
			} catch (BadLocationException e) {
			}
			return text;
		}
	}
	
	static class SlotsView {
		private JPanel panel;
		private JTextField txtFldCoins;
		private JTextField txtFldWager;
		private JTextField txtFldJackpot;
		private JTextField txtFldOutcome1;
		private JTextField txtFldOutcome2;
		private JTextField txtFldOutcome3;
		private JTextArea txtAreaMessages;
		private JButton btnPull;
		private JButton btnOdds;
		private JButton btnAddCoins;
		
		SlotsView() {
			initialize();
			layoutComponents();
		}
		
		public void setPullAction(Action action) {
			btnPull.setAction(action);
			btnPull.setText("Pull");
		}
		
		public void setAddCoinsAction(Action action) {
			btnAddCoins.setAction(action);
			btnAddCoins.setText("+ $$");
		}
		
		public void setOddsAction(Action action) {
			btnOdds.setAction(action);
			btnOdds.setText("Odds");
		}

		public void setModel(SlotsModel model) {
			txtFldCoins.setDocument(model.getCoinsModel());
			txtFldWager.setDocument(model.getWagerModel());
			txtFldJackpot.setDocument(model.getJackpotModel());
			txtFldOutcome1.setDocument(model.getOutcome1Model());
			txtFldOutcome2.setDocument(model.getOutcome2Model());
			txtFldOutcome3.setDocument(model.getOutcome3Model());
			txtAreaMessages.setDocument(model.getMessagesModel());
		}

		public void initialize() {
			panel = new JPanel();
			
			txtFldCoins = new JTextField();
			txtFldCoins.setFocusable(false);
			
			txtFldWager = new JTextField();
			
			txtFldJackpot = new JTextField(10);
			txtFldJackpot.setFocusable(false);
			txtFldJackpot.setBorder(null);
			
			txtFldOutcome1 = new JTextField(6);
			txtFldOutcome1.setFocusable(false);
			txtFldOutcome1.setBorder(null);
			
			txtFldOutcome2 = new JTextField(6);
			txtFldOutcome2.setFocusable(false);
			txtFldOutcome2.setBorder(null);
			
			txtFldOutcome3 = new JTextField(6);
			txtFldOutcome3.setFocusable(false);
			txtFldOutcome3.setBorder(null);
			
			txtAreaMessages = new JTextArea(8, 20);
			txtAreaMessages.setFocusable(false);
			
			btnPull = new JButton();
			
			btnAddCoins = new JButton();
			
			btnOdds = new JButton();
		}
		
		public void layoutComponents() {
			panel.setLayout(new BorderLayout());
			
			JPanel coinsPanel = new JPanel(new BorderLayout());
			JPanel labelPanel = new JPanel();
			labelPanel.add(new JLabel("Coins"));
			labelPanel.add(Box.createHorizontalStrut(6));
			coinsPanel.add(labelPanel, BorderLayout.WEST);
			
			coinsPanel.add(txtFldCoins);
			
			labelPanel = new JPanel();
			labelPanel.add(Box.createHorizontalStrut(6));
			labelPanel.add(btnAddCoins);
			coinsPanel.add(labelPanel, BorderLayout.EAST);
			
			JPanel wagerPanel = new JPanel(new BorderLayout());
			labelPanel = new JPanel();
			labelPanel.add(new JLabel("Wager"));
			labelPanel.add(Box.createHorizontalStrut(6));
			wagerPanel.add(labelPanel, BorderLayout.WEST);
			wagerPanel.add(txtFldWager);
			labelPanel = new JPanel();
			labelPanel.add(Box.createHorizontalStrut(6));
			labelPanel.add(btnPull, BorderLayout.EAST);
			labelPanel.add(Box.createHorizontalStrut(20));
			labelPanel.add(btnOdds, BorderLayout.EAST);
			wagerPanel.add(labelPanel, BorderLayout.EAST);

			JPanel messagesPanel = new JPanel(new BorderLayout());
			JPanel outcomePanel = new JPanel();
			outcomePanel.add(txtFldOutcome1);
			outcomePanel.add(txtFldOutcome2);
			outcomePanel.add(txtFldOutcome3);
			outcomePanel.add(Box.createHorizontalStrut(20));
			outcomePanel.add(new JLabel("Jackpot"));
			outcomePanel.add(Box.createHorizontalStrut(4));
			outcomePanel.add(txtFldJackpot);
			
			messagesPanel.add(outcomePanel, BorderLayout.NORTH);
			messagesPanel.add(new JScrollPane(txtAreaMessages));
			
			panel.add(coinsPanel, BorderLayout.NORTH);
			panel.add(messagesPanel);
			panel.add(wagerPanel, BorderLayout.SOUTH);
		}
		
		public JComponent getComponent() {
			return panel;
		}
	}
}

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
		Bell("Bell", 			16),
		DoubleBar("=Bar=",      10),
		Grapes("Grapes", 		12), 
		Orange("Orange", 		 8), 
		Apple("Apple", 			 4), 
		Bar("-Bar-", 		     5),
		Cherry("Cherry", 		 1), 
		Lemon("Lemon", 			 3), 
		Melon("Melon", 			 2),
		Banana("Banana", 		 1),
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
		
		probs.put(Outcome.Bell,      new int[] { 95, 96, 95 });
		probs.put(Outcome.DoubleBar, new int[] { 93, 92, 65 });
		probs.put(Outcome.Grapes,    new int[] { 88, 89, 41 });
		probs.put(Outcome.Orange,    new int[] { 80, 87, 40 });
		probs.put(Outcome.Apple,     new int[] { 80, 80, 20 });
		probs.put(Outcome.Bar,       new int[] { 72, 72, 30 });
		probs.put(Outcome.Cherry,    new int[] { 68, 68, 70 });
		probs.put(Outcome.Lemon,     new int[] {  0, 60, 30 });
		probs.put(Outcome.Melon,     new int[] { 60, 30,  0 });
		probs.put(Outcome.Banana,    new int[] { 30, 0,  60 });
		
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

	public int determinePayout(Outcome[] outcomes, int wager, boolean report) {
		int payout = 0;
		
		Integer jackpot = getContextValue("slots.jackpot");

		// three of a kind
		if (outcomes[0] == outcomes[1] && outcomes[1] == outcomes[2]) {
			// Triple Bell wins the current jackpot
			if (outcomes[0] == Outcome.Bell) {
				payout = jackpot;

				// Only do this stuff if we aren't determining for payout report
				if (!report) {
					displayMessage("*** Jackpot!!! ***", getUser());
					
					setContextValue("slots.jackpot", 100);
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
			else if (outcomes[2] == Outcome.Bar || outcomes[2] == Outcome.DoubleBar) {
				payout = outcomes[0].getPayout() * 2 * wager;
			}
			else if (outcomes[0] == Outcome.Cherry) {
			    payout = outcomes[0].getPayout() * 4 * wager;
			}
		}
		else if (outcomes[1] == outcomes[2]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[1] == Outcome.Bar || outcomes[1] == Outcome.DoubleBar || outcomes[0] == Outcome.Cherry) {
				payout = outcomes[1].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[0] == Outcome.Bar || outcomes[0] == Outcome.DoubleBar) {
				payout = outcomes[1].getPayout() * 2 * wager;
			}
            else if (outcomes[0] == Outcome.Cherry) {
                payout = outcomes[1].getPayout() * 4 * wager;
            }
		}
		else if (outcomes[0] == outcomes[2]) {
			// Any two Bar or two DoubleBar or Cherry pays wager * outcome payout
			if (outcomes[0] == Outcome.Bar || outcomes[0] == Outcome.DoubleBar || outcomes[1] == Outcome.Cherry) {
				payout = outcomes[0].getPayout() * wager;
			}
			// Any two with Bar or Double Bar, or Any two Cherry pays double wager * outcome payout
			else if (outcomes[1] == Outcome.Bar || outcomes[1] == Outcome.DoubleBar) {
				payout = outcomes[0].getPayout() * 2 * wager;
			}
            else if (outcomes[0] == Outcome.Cherry) {
                payout = outcomes[0].getPayout() * 4 * wager;
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
		Integer jackpot = getContextValue("slots.jackpot");
		if (null == jackpot || jackpot < 100) {
			jackpot = 100;
		}
		
		Integer points = getContextValue("quiz.points." + getUser());
		if (null == points) {
			points = 0;
		}
		
		if (StringUtils.isEmpty(getArgs().trim())) {
			displayMessages(new String[] {
					"The current jackpot is " + jackpot + " points",
					"You have " + points + " points" }, getUser());
			return;
		}
		
		if ("PAYOUT".equals(getArgs().toUpperCase())) {
			List<String> payouts = new ArrayList<String>();
			
			payouts.add("    ======== Slots Payouts ========");
			payouts.add("    BELL   BELL   BELL --> *** JACKPOT ***");
            payouts.add("    THREE MATCHING --> 3 * Value" );
            payouts.add("    CHERRY CHERRY CHERRY --> 12" );
			payouts.add("    ANY TWO *BAR* --> *Bar* Value" );
            payouts.add("    TWO MATCHING + *BAR* --> 2 * Value" );
            payouts.add("    TWO CHERRY --> " + 4 * Outcome.Cherry.payout + "     ANY CHERRY --> " + Outcome.Cherry.payout);
            payouts.add("        ------------ Payout Values ------------");
            int i = 0;
            String payoutStr = "";
            for(Outcome outcome : Outcome.values()) {
                if (outcome != Outcome.Cherry) {
                    payoutStr += outcome.getName() + " --> " + outcome.getPayout();
                    i++;
                    if (i % 3 == 0) {
                        payouts.add("    " + payoutStr);
                        payoutStr = "";
                    }
                    else {
                        payoutStr += "      ";
                    }
                }
			}	
			
			displayMessages(payouts.toArray(new String[0]), getUser());
			return;
		}
		
		if ("ODDS".equals(getArgs().toUpperCase())) {
			List<String> outcomes = new ArrayList<String>();
			outcomes.add("    ======== Slots Odds ========");
			int totalOdds = 0;
            String oddsStr = "";
            int j = 0;
			for(int i = 0; i < Outcome.values().length;) {
				int probs[] = getProbs().get(Outcome.values()[i / 2 + j]);
				int odds = (100 - probs[0]) * (100 - probs[1]) * (100 - probs[2]);
				if (Outcome.Banana == Outcome.values()[i / 2 + j]) {
					odds = 1000000 - totalOdds;
				}
				else {
					totalOdds += odds;
				}
				oddsStr += Outcome.values()[i / 2 + j].getName() + " --> 1 in " + Math.round(1000000 / (double)odds);
				i++;
                if (i % 2 == 0) {
                    outcomes.add("    " + oddsStr);
                    oddsStr = "";
                }
                else {
                    oddsStr += "      ";
                }
                j += 5;
                j %= 10;
			}	
			
			displayMessages(outcomes.toArray(new String[0]), getUser());
			return;
		}
		
		Integer wager = null;
		try {
			wager = Integer.valueOf(getArgs());
			if (wager > points) {
				displayMessage("You don't have that many points", getUser());
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
		
		showOutcome("[ " + outcomes[0].getName() + " | " + outcomes[1].getName() + " | " + outcomes[2].getName() + " ]", getUser());

		points -= wager;
		int payout = determinePayout(outcomes, wager, false);
		if (payout > 0) {
			points += payout;
			displayMessage("You won " + payout, getUser());
			showPoints(points);
		}
		else {
			displayMessage("Sorry, try again", getUser());
			showPoints(points);
			if (wager > 10) {
				jackpot += 10;
			}
			else {
				jackpot += wager;
			}
			setContextValue("slots.jackpot", jackpot);
		}
		
		setContextValue("quiz.points." + getUser(), points);
	}

	private void showUsage() {
		String[] messages = new String[] {
				"Usage: /slots {wager}",
				"/slots by itself will show the current jackpot"
		};
		displayMessages(messages, getUser());
	}
	
	protected void showOutcome(String message, String user) {
		CommandUtils.writeToConsole(message, user);
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
			
			view.setAddCoinsAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer points = slots.getContextValue("quiz.points.test");
					if (null == points) {
						points = 0;
					}
					slots.setContextValue("quiz.points.test", points + 100);
					slots.setArgs("");
					slots.setUser("test");
					slots.execute();
				}
			});
			
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

		public void initialize() {
			coinsModel = new PlainDocument();
			outcome1Model = new PlainDocument();
			outcome2Model = new PlainDocument();
			outcome3Model = new PlainDocument();
			wagerModel = new PlainDocument();
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
		private JTextField txtFldOutcome1;
		private JTextField txtFldOutcome2;
		private JTextField txtFldOutcome3;
		private JTextArea txtAreaMessages;
		private JButton btnPull;
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

		public void setModel(SlotsModel model) {
			txtFldCoins.setDocument(model.getCoinsModel());
			txtFldWager.setDocument(model.getWagerModel());
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
			wagerPanel.add(btnPull, BorderLayout.EAST);

			JPanel messagesPanel = new JPanel(new BorderLayout());
			JPanel outcomePanel = new JPanel();
			outcomePanel.add(txtFldOutcome1);
			outcomePanel.add(txtFldOutcome2);
			outcomePanel.add(txtFldOutcome3);
			messagesPanel.add(outcomePanel, BorderLayout.NORTH);
			messagesPanel.add(txtAreaMessages);
			
			panel.add(coinsPanel, BorderLayout.NORTH);
			panel.add(messagesPanel);
			panel.add(wagerPanel, BorderLayout.SOUTH);
		}
		
		public JComponent getComponent() {
			return panel;
		}
	}
}

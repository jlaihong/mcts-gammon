/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.mctsgammon.util;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.mctsgammon.players.mcts.UCTWeightPlayer;
import org.mctsgammon.players.mcts.nodes.IChanceOrLeafNode;
import org.mctsgammon.players.mcts.nodes.MaxMinNode;
import org.mctsgammon.states.MoveState;

public class TreeGUI {

	public static void main(String[] args) throws InterruptedException {
		UCTWeightPlayer p = new UCTWeightPlayer(5000, 3);
//		MCTSPlayer p = new MaxdistrUCTPlayer(25000, 3, 1);
		Display display = new Display();

		TreeGUI tree = new TreeGUI(display);

		MoveState state = MoveState.getRandomStartState();
		MaxMinNode<?> nodes = p.doMCTS(state);
		tree.readRoot(nodes);

		System.out.println("Handling GUI events.");
		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		System.out.println("Done");
	}

	private final Display display;
	private Tree tree;

	public TreeGUI(final Display display) {
		this.display = display;
		display.syncExec(new Runnable() {

			public void run() {
				Shell shell = new Shell(display);
				shell.addShellListener(new ShellAdapter() {
					@Override
					public void shellClosed(ShellEvent e) {
						e.doit = false;
					}
				});
				shell.setSize(800, 600);
				shell.setMinimumSize(500, 400);
				shell.setLayout(new FillLayout());
				shell.setText("Game Tree Browser");
				tree = new Tree(shell, SWT.BORDER | SWT.H_SCROLL
						| SWT.V_SCROLL);
				tree.setHeaderVisible(true);

				TreeColumn column = new TreeColumn(tree, SWT.LEFT);
				column.setText("Player");
				column.setWidth(350);

				column = new TreeColumn(tree, SWT.CENTER);
				column.setText("Action");
				column.setWidth(140);

				column = new TreeColumn(tree, SWT.CENTER);
				column.setText("P(Action)");
				column.setWidth(70);

				column = new TreeColumn(tree, SWT.CENTER);
				column.setText("Samples");
				column.setWidth(70);

				column = new TreeColumn(tree, SWT.CENTER);
				column.setText("EV");
				column.setWidth(70);

				shell.pack();
				shell.open();
			}

		});
	}


	private <T> void readRoot(final MaxMinNode<T> root) {
		display.syncExec(new Runnable(){
			@Override
			public void run() {
				try {
					System.out.println("Reading in MCTS tree.");
					tree.removeAll();
					List<IChanceOrLeafNode<T>> nodes = root.getChildrenOrNull();
					for(int i=0;i<nodes.size();i++){
						System.out.println((1+i)+"/"+nodes.size());
						onThrowState(i, nodes.get(i), null);
					}
					System.out.println("Redrawing tree.");
					tree.redraw();
				} catch (Throwable e) {
					System.out.println("OOPS");
					e.printStackTrace();
				}
			}
		});
	}

	private <T> void onMoveState(MaxMinNode<T> move, TreeItem previous) {
		if(move!=null){
			TreeItem newItem = previous==null? new TreeItem(tree, SWT.NONE):new TreeItem(previous, SWT.NONE);
			newItem.setText(new String[] { 
					""+(move.moveState.isBlackTurn? "black":"red"),
					"throw "+move.moveState.diceThrow.toString(),
					prob(move.moveState.diceThrow.prob),
					""+move.getNbSamples(),
					""+move.getEV()

			});
			newItem.setBackground(0, (move.moveState.isBlackTurn ? new Color(display, 220, 220, 220):new Color(display, 255, 200, 200)));
			List<IChanceOrLeafNode<T>> children = move.getChildrenOrNull();
			if(children!=null && move.getNbSamples()>1){
				for(int i=0;i<children.size();i++){
					onThrowState(i,children.get(i), newItem);
				}
			}
		}
	}
	
	private String prob(double p) {
		return ""+Math.round(100*p)+"%";
	}


	private <T> void onThrowState(int j, IChanceOrLeafNode<T> chanceNode, TreeItem previous) {
		if(chanceNode!=null){
			TreeItem newItem = previous==null? new TreeItem(tree, SWT.NONE):new TreeItem(previous, SWT.NONE);
			newItem.setText(new String[] { 
					""+(chanceNode.getThrowState().isBlackTurn? "black":"red"),
					"move "+j,
					"",
					""+chanceNode.getNbSamples(),
					""+chanceNode.getEV()

			});
			newItem.setBackground(0, (chanceNode.getThrowState().isBlackTurn ? 
					new Color(display, 220, 220, 220):new Color(display, 255, 200, 200)));
			List<MaxMinNode<T>> children = chanceNode.getChildrenOrNull();
			if(children!=null && chanceNode.getNbSamples()>1){
				for(int i=0;i<children.size();i++){
					onMoveState(children.get(i), newItem);
				}
			}
		}
	}

}

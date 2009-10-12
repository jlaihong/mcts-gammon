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
import org.mctsgammon.MCTSMoveState;
import org.mctsgammon.MCTSPlayer;
import org.mctsgammon.MCTSThrowState;
import org.mctsgammon.MoveState;
import org.mctsgammon.ThrowState;
import org.mctsgammon.players.MaxdistrUCTPlayer;

public class TreeGUI {

	public static void main(String[] args) throws InterruptedException {
		//MCTSPlayer p = new UCTPlayer(5000, 3);
		MCTSPlayer p = new MaxdistrUCTPlayer(25000, 3, 1);
		Display display = new Display();

		TreeGUI tree = new TreeGUI(display);

		MoveState state = MoveState.getRandomStartState();
		ThrowState[] nodes = p.doMCTS(state);
		tree.readNodes(nodes);

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


	private void readNodes(final ThrowState[] nodes) {
		display.syncExec(new Runnable(){
			@Override
			public void run() {
				try {
					System.out.println("Reading in MCTS tree.");
					tree.removeAll();
					for(int i=0;i<nodes.length;i++){
						System.out.println((1+i)+"/"+nodes.length);
						onThrowState(i, (MCTSThrowState)nodes[i], null);
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

	private void onMoveState(MCTSMoveState move, TreeItem previous) {
		if(move!=null){
			TreeItem newItem = previous==null? new TreeItem(tree, SWT.NONE):new TreeItem(previous, SWT.NONE);
			newItem.setText(new String[] { 
					""+(move.isBlackTurn? "black":"red"),
					"throw "+move.diceThrow.toString(),
					prob(move.diceThrow.prob),
					""+move.getNbSamples(),
					""+move.getEV()

			});
			newItem.setBackground(0, (move.isBlackTurn ? new Color(display, 220, 220, 220):new Color(display, 255, 200, 200)));
			ThrowState[] children = move.getChildrenIfAny();
			if(children!=null && move.getNbSamples()>1){
				for(int i=0;i<children.length;i++){
					onThrowState(i,(MCTSThrowState)children[i], newItem);
				}
			}
		}
	}
	
	private String prob(double p) {
		return ""+Math.round(100*p)+"%";
	}


	private void onThrowState(int j, MCTSThrowState move, TreeItem previous) {
		if(move!=null){
			TreeItem newItem = previous==null? new TreeItem(tree, SWT.NONE):new TreeItem(previous, SWT.NONE);
			newItem.setText(new String[] { 
					""+(move.isBlackTurn? "black":"red"),
					"move "+j,
					"",
					""+move.getNbSamples(),
					""+move.getEV()

			});
			newItem.setBackground(0, (move.isBlackTurn ? new Color(display, 220, 220, 220):new Color(display, 255, 200, 200)));
			MoveState[] children = move.getChildrenIfAny();
			if(children!=null && move.getNbSamples()>1){
				for(int i=0;i<children.length;i++){
					onMoveState((MCTSMoveState)children[i], newItem);
				}
			}
		}
	}

}

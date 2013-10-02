import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LinesComponent extends JComponent {

	private static class Line {
		final int x1;
		final int y1;
		final int x2;
		final int y2;
		final Color color;

		public Line(int x1, int y1, int x2, int y2, Color color) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.color = color;
		}
	}

	private final LinkedList<Line> lines = new LinkedList<Line>();

	public void addLine(int x1, int x2, int x3, int x4) {
		addLine(x1, x2, x3, x4, Color.black);
	}

	public void addLine(int x1, int x2, int x3, int x4, Color color) {
		lines.add(new Line(x1, x2, x3, x4, color));
		repaint();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearLines() {
		lines.clear();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Line line : lines) {
			g.setColor(line.color);
			g.drawLine(line.x1, line.y1, line.x2, line.y2);
			
		}
		System.out.println();
	}

	public static void main(String[] args) {
		JFrame testFrame = new JFrame();
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final LinesComponent comp = new LinesComponent();
		comp.setPreferredSize(new Dimension(320, 200));
		testFrame.getContentPane().add(comp, BorderLayout.CENTER);
		JPanel buttonsPanel = new JPanel();
		JButton newLineButton = new JButton("New Line");
		JButton clearButton = new JButton("Clear");
		buttonsPanel.add(newLineButton);
		buttonsPanel.add(clearButton);
		testFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		newLineButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BugTrapMaze bugTrapMaze = new BugTrapMaze();
				List<POINT> result = null;
				try {
					result = bugTrapMaze.run();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int x1 = (int) (Math.random() * 320);
				int x2 = (int) (Math.random() * 320);
				int y1 = (int) (Math.random() * 200);
				int y2 = (int) (Math.random() * 200);
				POINT start = result.get(0);
				for (int j = 1; j < result.size(); j++) {
					POINT temp = result.get(j);
					System.out.println(temp.x+" "+temp.y);
					Color randomColor = new Color((float) Math.random(),
							(float) Math.random(), (float) Math.random());
					
					comp.addLine(start.x * 30, start.y * 30, temp.x * 30,
							temp.y * 30, randomColor);
                    
					start = temp;
					
				}

			}
		});
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				comp.clearLines();
			}
		});
		testFrame.pack();
		testFrame.setVisible(true);
	}
}
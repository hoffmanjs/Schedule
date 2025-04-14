package usace.wm.schedule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ScheduleHelp extends JDialog {
	private static final long serialVersionUID = 6030726048381946688L;
	private JTextPane textPane = new JTextPane();
	private JScrollPane textScrollPane = null;

	public ScheduleHelp() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setLayout(new BorderLayout());
		setTitle("Schedule Program Help");
		setSize(new Dimension(880, 820));
		textPane.setFont(new Font("Times New Roman", 0, 14));
		textPane.setEditable(false);

		SimpleAttributeSet black = new SimpleAttributeSet();
		StyleConstants.setForeground(black, Color.black);
		SimpleAttributeSet bold = new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		StyleConstants.setForeground(bold, Color.blue);

		JEditorPane editorPane = createEditorPane();
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(22);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));

		textPane.setBounds(new Rectangle(5, 5, 750, 950));
		textPane.setAutoscrolls(true);
		textScrollPane = new JScrollPane(textPane);
		textScrollPane.setSize(new Dimension(755, 875));

		add(editorScrollPane);
		textPane.setVisible(true);
	}

	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setSize(860, 800);
		editorPane.setContentType("text/html");
//		URL classURL = getClass().getProtectionDomain().getCodeSource().getLocation();
		URL helpURL = getClass().getResource("/Help.html");
		if (helpURL != null) {
			try {
				editorPane.setPage(helpURL);
			} catch (IOException e) {
				System.err.println("Attempted to read a bad URL: " + helpURL);
			}
		} else {
			System.err.println("Couldn't find file: Help.html");
		}
		return editorPane;
	}
}

package usace.wm.schedule;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class PrintText extends JFrame {
	private static final long serialVersionUID = 5971089626048970552L;
	private JTextPane textPane = new JTextPane();
	private JTable data = null;
	private JScrollPane textScrollPane = null;
	private String winHeaderText = null;

	public PrintText(JTable data, String winText) {
		try {
			setData(data);
			setWinHeaderText(winText);
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		getContentPane().setLayout(null);
		setSize(new Dimension(760, 920));
		setTitle(getWinHeaderText());
		setResizable(false);
		textPane.setFont(new Font("Times New Roman", 0, 14));
		textPane.setEditable(false);

		SimpleAttributeSet black = new SimpleAttributeSet();
		StyleConstants.setForeground(black, Color.black);
		SimpleAttributeSet bold = new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		StyleConstants.setForeground(bold, Color.blue);

		textPane.setBounds(new Rectangle(5, 5, 750, 950));
		textPane.setAutoscrolls(true);
		textScrollPane = new JScrollPane(textPane);
		textScrollPane.setSize(new Dimension(755, 875));

		getContentPane().add(textScrollPane, null);
		JTable table = getData();
		for (int row = 0; row < table.getRowCount(); row++) {
			String data = Util.getTableText(table, row);
			if (data.contains("~~")) {
				data = data.replace("~~", "\t");
			}
			if (data.contains("~")) {
				data = data.replace("~", "\t");
			}
			if (data.endsWith("true")) {
				append(data.substring(0, data.indexOf("true")).concat("\n"), bold);
			}
			if (data.endsWith("false")) {
				append(data.substring(0, data.indexOf("false")).concat("\n"), black);
			}
		}
		textPane.setVisible(true);

		MessageFormat mfFooter = new MessageFormat(Util.getDateString());
		MessageFormat mfHeader = new MessageFormat(" Schedule by: " + getUser());
		Printable pt = textPane.getPrintable(mfHeader, mfFooter);
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(pt);
	}

	protected void append(String s, AttributeSet attributes) {
		Document d = textPane.getDocument();
		try {
			d.insertString(d.getLength(), s, attributes);
		} catch (BadLocationException localBadLocationException) {
		}
	}

	private JTable getData() {
		return data;
	}

	private void setData(JTable table) {
		data = table;
	}

	private String getUser() {
		String username = System.getProperty("user.name");
		return username;
	}

	private String getWinHeaderText() {
		return winHeaderText;
	}

	private void setWinHeaderText(String winHeaderTxt) {
		winHeaderText = winHeaderTxt;
	}
}

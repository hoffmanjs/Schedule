package usace.wm.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

class SelectAllRenderer extends DefaultTableCellRenderer {
	private Color selectionBackground;
	private Border editBorder = BorderFactory.createLineBorder(Color.BLACK);
	private boolean isPaintSelection;

	public SelectAllRenderer() {
		this(UIManager.getColor("TextField.selectionBackground"));
	}

	public SelectAllRenderer(Color selectionBackgrnd) {
		selectionBackground = selectionBackgrnd;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if ((hasFocus) && (table.isCellEditable(row, column)) && (!getText().equals(""))) {
			isPaintSelection = true;
		} else {
			isPaintSelection = false;
		}
		return this;
	}

	protected void paintComponent(Graphics g) {
		if (isPaintSelection) {
			setBorder(editBorder);
			g.setColor(UIManager.getColor("Table.focusCellBackground"));
			g.fillRect(0, 0, getSize().width, getSize().height);

			g.setColor(Color.YELLOW);
			g.fillRect(0, 0, getPreferredSize().width, getSize().height);
			setOpaque(false);
		}
		super.paintComponent(g);
		setOpaque(true);
	}
}

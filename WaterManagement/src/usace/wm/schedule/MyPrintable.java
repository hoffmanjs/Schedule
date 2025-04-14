package usace.wm.schedule;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import javax.swing.JTable;

class MyPrintable implements Printable {
	JTable str;

	public MyPrintable(JTable getStr) {
		str = getStr;
	}

	public int print(Graphics g, PageFormat pf, int pageIndex) {
		if (pageIndex != 0) {
			return 1;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(pf.getImageableX(), pf.getImageableY());
		Rectangle componentBounds = str.getBounds(null);
		g2.translate(-componentBounds.x, -componentBounds.y);
		g2.scale(1.0D, 1.0D);
		boolean wasBuffered = str.isDoubleBuffered();
		str.paint(g2);
		str.setDoubleBuffered(wasBuffered);
		return 0;
	}
}

package usace.wm.schedule;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class WaterManagementUI_AboutBoxPanel extends JPanel {
	private static final long serialVersionUID = 7526472295622776147L;
	private JLabel labelTitle = new JLabel();
	private JLabel labelVersion = new JLabel();
	private JLabel labelAuthor = new JLabel();
	private JLabel labelCopyright = new JLabel();
	private JLabel labelCompany = new JLabel();
	private GridBagLayout layoutMain = new GridBagLayout();
	private Border border = BorderFactory.createEtchedBorder();

	public WaterManagementUI_AboutBoxPanel() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setLayout(layoutMain);
		setBorder(border);		

		labelTitle.setText("Water Management Tools");
		labelVersion.setText("Version: 2011");
		labelAuthor.setText("Author: SH");
		labelCopyright.setText("Copyright: USACE");
		labelCompany.setText("Company: USACE");

		add(labelTitle, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 15, 0, 15), 0, 0));
		add(labelVersion, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 15, 0, 15), 0, 0));
		add(labelAuthor, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 15, 0, 15), 0, 0));
		add(labelCopyright, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 15, 0, 15), 0, 0));
		add(labelCompany, new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 15, 5, 15), 0, 0));
	}
}

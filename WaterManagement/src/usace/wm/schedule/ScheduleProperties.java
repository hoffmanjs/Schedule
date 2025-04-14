package usace.wm.schedule;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScheduleProperties extends JDialog {
	private static final long serialVersionUID = 7526472295622776147L;
	private JPanel propertiesPanel = new JPanel();
	private JLabel onDayLabel;
	private JTextField oneDayAgoPercent;
	private JLabel twoDayLabel;
	private JTextField twoDayAgoPercent;
	private JLabel schfileLocationLabel;
	private JTextField defaultSchfileLocation;
	private JButton schfileLocationButton;
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();

	public ScheduleProperties() {
		getContentPane().setLayout(null);
		setSize(new Dimension(300, 300));
		setTitle("Schedule Properties");
		setResizable(false);

		onDayLabel = new JLabel("FTRA One Day Ago Percent:");
		oneDayAgoPercent = new JTextField(Util.getFTRAOneDayAgoPercentage().toString());
		twoDayLabel = new JLabel("FTRA Two Day Ago Percent:");
		twoDayAgoPercent = new JTextField(Util.getFTRATwoDayAgpPercentage().toString());
		schfileLocationLabel = new JLabel("Default Schfile Location:");
		defaultSchfileLocation = new JTextField(Util.getDefaultProgramLocation());
		schfileLocationButton = new JButton("...");
		schfileLocationButton.setMnemonic('.');

		propertiesPanel.setLayout(null);
		propertiesPanel.setSize(new Dimension(280, 280));
		propertiesPanel.setBounds(new Rectangle(2, 24, 200, 200));
		propertiesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		propertiesPanel.add(onDayLabel, null);
		propertiesPanel.add(oneDayAgoPercent, "East");
		propertiesPanel.add(twoDayLabel, null);
		propertiesPanel.add(twoDayAgoPercent, "East");
		propertiesPanel.add(schfileLocationLabel, null);
		propertiesPanel.add(defaultSchfileLocation, "East");
		propertiesPanel.add(schfileLocationButton, "East");
		getContentPane().add(propertiesPanel, "propertiesPanel");
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setSize(new Dimension(475, 250));
		setBounds(new Rectangle(10, 10, 475, 225));
		setResizable(false);
		setModal(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				scheduleProperties_windowClosed(e);
			}
		});
		propertiesPanel.setLayout(null);
		propertiesPanel.setBounds(new Rectangle(0, 0, 508, 300));
		onDayLabel.setBounds(new Rectangle(10, 25, 160, 25));
		oneDayAgoPercent.setBounds(new Rectangle(170, 20, 40, 25));

		twoDayLabel.setBounds(new Rectangle(10, 60, 160, 25));
		twoDayAgoPercent.setBounds(new Rectangle(170, 60, 40, 25));

		schfileLocationLabel.setBounds(new Rectangle(10, 90, 150, 25));
		defaultSchfileLocation.setBounds(new Rectangle(170, 90, 240, 25));
		defaultSchfileLocation.setEditable(false);
		schfileLocationButton.setBounds(new Rectangle(420, 90, 35, 25));
		schfileLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schfileLocationButton_actionPerformed(e);
			}
		});
		okButton.setText("OK");
		okButton.setBounds(new Rectangle(155, 165, 75, 25));
		okButton.setSize(new Dimension(75, 25));
		okButton.setToolTipText("OK");
		okButton.setMnemonic('O');
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok_actionPerformed(e);
			}
		});
		cancelButton.setText("Cancel");
		cancelButton.setBounds(new Rectangle(255, 165, 75, 25));
		cancelButton.setToolTipText("Cancel");
		cancelButton.setSize(new Dimension(75, 25));
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel_actionPerformed(e);
			}
		});
		propertiesPanel.add(cancelButton, null);
		propertiesPanel.add(okButton, null);
		propertiesPanel.add(onDayLabel, "Center");
		propertiesPanel.add(oneDayAgoPercent, "West");
		propertiesPanel.add(twoDayLabel, "East");
		propertiesPanel.add(twoDayAgoPercent, "North");
		propertiesPanel.add(schfileLocationLabel, "North");
		propertiesPanel.add(defaultSchfileLocation, "South");
		propertiesPanel.add(schfileLocationButton, "North");
		getContentPane().add(propertiesPanel, null);
	}

	private void schfileLocationButton_actionPerformed(ActionEvent e) {
		if (Util.getFileChooser(this)) {
			defaultSchfileLocation.setText(Util.getDefaultProgramLocation());
		}
	}

	private void ok_actionPerformed(ActionEvent e) {
		boolean errors = false;
		try {
			Util.setFTRAOneDayAgoPercentage(Double.valueOf(Double.parseDouble(oneDayAgoPercent.getText())));
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Please enter a valid One Day Ago Percent", "Invalid Data", 0);
			oneDayAgoPercent.setText("0.6");
			oneDayAgoPercent.requestFocus();
			errors = true;
		}
		try {
			Util.setFTRATwoDayAgpPercentage(Double.valueOf(Double.parseDouble(twoDayAgoPercent.getText())));
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Please enter a valid Two Day Ago Percent", "Invalid Data", 0);
			twoDayAgoPercent.setText("0.4");
			twoDayAgoPercent.requestFocus();
			errors = true;
		}
		if (!errors) {
			setVisible(false);
		}
	}

	private void cancel_actionPerformed(ActionEvent e) {
		setVisible(false);
	}

	private void scheduleProperties_windowClosed(WindowEvent e) {
	}
}

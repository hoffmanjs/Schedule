package usace.wm.schedule;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

class SplashScreen extends JWindow {
  static JProgressBar progressBar = new JProgressBar();
  static int count = 1, TIMER_PAUSE = 600, PROGBAR_MAX = 100;
  static Timer progressBarTimer;
  ActionListener al = new ActionListener() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      progressBar.setValue(count);
      if (PROGBAR_MAX == count) {
        progressBarTimer.stop();
        SplashScreen.this.setVisible(false);
      }
      count++;
    }
  };

  public SplashScreen() {
    Container container = getContentPane();    

    JPanel panel = new JPanel();
    panel.setBorder(new EtchedBorder());
    container.add(panel, BorderLayout.CENTER);

    JLabel label = new JLabel("Schedule Program Is Loading");
    label.setFont(new Font("Verdana", Font.BOLD, 14));
    panel.add(label);

    progressBar.setMaximum(PROGBAR_MAX);
    container.add(progressBar, BorderLayout.SOUTH);
    pack();
    
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

	int winWidth = panel.getSize().width;
	int winHeight = panel.getSize().height;

	int x = (screen.width - winWidth) / 2;
	int y = (screen.height - winHeight) / 2;

	this.setLocation(x, y);
    
    setVisible(true);
    startProgressBar();
  }
  private void startProgressBar() {
    progressBarTimer = new Timer(TIMER_PAUSE, al);
    progressBarTimer.start();
  }
}
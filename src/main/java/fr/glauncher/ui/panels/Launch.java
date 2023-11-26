package fr.glauncher.ui.panels;

import com.sun.management.OperatingSystemMXBean;
import fr.glauncher.Controller;
import fr.glauncher.game.Setup;
import fr.glauncher.ui.frames.AdditionalModList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Launch extends JPanel implements ActionListener, ChangeListener
{
	private final Controller   ctrl;
	private final JButton      btnDisconnect;
	private final JButton      btnPlay;
	private final JButton      btnAdditional;
	private final JSpinner     ramSelector;
	private final JProgressBar progressBar;
	private final JLabel       lblRamInfo;
	private final JPanel       panelBot;
	private final Image        imgFond;
	private Image              imgHead;

	public Launch( Controller ctrl )
	{
		this.imgFond = getToolkit().getImage ( getClass().getResource("/background.png") );
		this.ctrl    = ctrl;

		this.setLayout( new BorderLayout() );

		SpinnerModel ramModel = new SpinnerNumberModel(4096, 1024, 16384, 256);

		long memorySize = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize();

		this.btnDisconnect = new JButton("Déconnexion");
		this.btnPlay       = new JButton("Jouer");
		this.btnAdditional = new JButton("Mods supplémentaires");
		this.ramSelector   = new JSpinner(ramModel);
		this.progressBar   = new JProgressBar(0, 10000);
		this.lblRamInfo    = new JLabel(String.format(" Mémoire RAM disponible : %,d Mo ", memorySize / ( 1_024 * 1_024 )));

		this.progressBar.setStringPainted(true);
		this.progressBar.setValue(0);

		this.btnPlay      .addActionListener( this );
		this.btnDisconnect.addActionListener( this );
		this.btnAdditional.addActionListener( this );

		this.ramSelector.setEditor(new JSpinner.NumberEditor(this.ramSelector, "# Mo"));
		this.ramSelector.addChangeListener(this);

		Border border = BorderFactory.createLineBorder(Color.darkGray, 2);

		this.lblRamInfo.setBorder(border);
		this.lblRamInfo.setBackground( Color.lightGray );
		this.lblRamInfo.setOpaque(true);

		String ramValue = ctrl.getSaver().get("ram");
		if (ramValue != null && ramValue.matches("\\d+")) this.ramSelector.setValue( Integer.parseInt(ramValue) );

		JPanel panelTop = new JPanel();
		this.panelBot = new JPanel();

		panelTop.setOpaque( false );
		this.panelBot.setOpaque( false );

		panelTop.add( this.btnAdditional );
		panelTop.add( this.btnDisconnect );
		panelTop.add( this.btnPlay       );

		this.panelBot.add(this.ramSelector);
		this.panelBot.add(this.lblRamInfo);

		this.add(panelTop, BorderLayout.NORTH );
		this.add( this.panelBot, BorderLayout.SOUTH );
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.drawImage ( this.imgFond, 0, 0, this );

		if (this.imgHead != null) g.drawImage ( this.imgHead, 50, 200, this );
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getSource() == this.btnPlay )
		{
			this.ctrl.getLogger().info("Preparing launch !");

			new Thread(() ->  Setup.setup( this.ctrl, this ) ).start();
		}
		else if ( e.getSource() == this.btnAdditional )
		{
			new AdditionalModList( this.ctrl );
		}
		else if ( e.getSource() == this.btnDisconnect )
		{
			this.ctrl.getSaver().remove("msAccessToken");
			this.ctrl.getSaver().remove("msRefreshToken");
			this.ctrl.getSaver().save();

			this.ctrl.switchLogin();
		}
	}

	public void setInLoading( boolean in )
	{
		this.panelBot.removeAll();

		if (in)
		{
			//this.panelBot.add(this.ramSelector);
			this.panelBot.add(this.progressBar);

			this.btnPlay      .setEnabled( false );
			this.btnAdditional.setEnabled( false );
			this.btnDisconnect.setEnabled( false );
		}
		else
		{
			this.panelBot.add(this.ramSelector);
			this.panelBot.add(this.lblRamInfo);
			this.progressBar.setValue(0);

			this.btnPlay      .setEnabled( true );
			this.btnAdditional.setEnabled( true );
			this.btnDisconnect.setEnabled( true );
		}

		this.repaint();
		this.revalidate();
	}

	public void setLoadingProgress( int progress )
	{
		this.progressBar.setValue( progress );
	}

	public void refreshHeadImg()
	{
		String name = this.ctrl.getAuth().getAuthInfos().getUsername();

		try
		{
			URI url = new URI(String.format("https://mc-heads.net/avatar/%s/100.png", name));

			this.ctrl.getLogger().info(url.toURL().toString());
			this.imgHead = new ImageIcon(url.toURL()).getImage();
		}
		catch (URISyntaxException | MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == this.ramSelector)
		{
			int value = (int) this.ramSelector.getValue();

			this.ctrl.getSaver().set("ram", String.valueOf(value));
			this.ctrl.getSaver().save();
		}
	}
}

package fr.glauncher.ui.panels;

import fr.glauncher.Controller;
import fr.glauncher.game.Setup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Launch extends JPanel implements ActionListener, ChangeListener
{
	private Controller ctrl;
	private Image      imgFond;
	private Image      imgHead;
	private JButton    btnDisconnect;
	private JButton    btnPlay;
	private JSpinner   ramSelector;

	public Launch( Controller ctrl )
	{
		this.imgFond = getToolkit().getImage ( getClass().getResource("/background.png") );
		this.ctrl    = ctrl;

		this.setLayout( new BorderLayout() );

		SpinnerModel ramModel = new SpinnerNumberModel(4096, 1024, 16384, 256);

		this.btnDisconnect = new JButton("Déconnexion");
		this.btnPlay       = new JButton("Jouer");
		this.ramSelector   = new JSpinner(ramModel);

		this.btnPlay      .addActionListener( this );
		this.btnDisconnect.addActionListener( this );

		this.ramSelector.setEditor(new JSpinner.NumberEditor(this.ramSelector, "# Mo"));
		this.ramSelector.addChangeListener(this);

		String ramValue = ctrl.getSaver().get("ram");
		if (ramValue != null && ramValue.matches("\\d+")) this.ramSelector.setValue( Integer.parseInt(ramValue) );

		JPanel panelTop = new JPanel();
		JPanel panelBot = new JPanel();

		panelTop.setOpaque( false );
		panelBot.setOpaque( false );

		panelTop.add( this.btnDisconnect );
		panelTop.add( this.btnPlay       );

		panelBot.add(this.ramSelector);

		this.add( panelTop, BorderLayout.NORTH );
		this.add( panelBot, BorderLayout.SOUTH );
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
			this.ctrl.getLogger().info("Préparation !");
			Setup.setup( this.ctrl);
		}
		else
		{
			this.ctrl.getSaver().remove("msAccessToken");
			this.ctrl.getSaver().remove("msRefreshToken");
			this.ctrl.getSaver().save();

			this.ctrl.switchLogin();
		}
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

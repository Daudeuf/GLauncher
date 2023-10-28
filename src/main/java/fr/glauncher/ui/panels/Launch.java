package fr.glauncher.ui.panels;

import fr.glauncher.Controller;
import fr.glauncher.game.Setup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Launch extends JPanel implements ActionListener
{
	private Controller ctrl;
	private Image      imgFond;
	private Image      imgHead;
	private JButton    btnDisconnect;
	private JButton    btnPlay;
	public Launch( Controller ctrl )
	{
		this.imgFond = getToolkit().getImage ( getClass().getResource("/background.png") );
		this.ctrl    = ctrl;

		this.setLayout( new BorderLayout() );

		this.btnDisconnect = new JButton("Déconnexion");
		this.btnPlay       = new JButton("Jouer");

		this.btnPlay      .addActionListener( this );
		this.btnDisconnect.addActionListener( this );

		JPanel p = new JPanel();

		p.setOpaque( false );

		p.add( this.btnDisconnect );
		p.add( this.btnPlay       );

		this.add( p, BorderLayout.CENTER );
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
			Setup.setup( this.ctrl );
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
}

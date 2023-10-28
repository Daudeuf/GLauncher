package fr.glauncher.ui.panels;

import fr.glauncher.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JPanel implements ActionListener
{
	private JCheckBox      cb;
	private JLabel         lblUsername;
	private JLabel         lblMail;
	private JLabel         lblPassword;
	private JPasswordField txtPassword;
	private JTextField     txtMail;
	private JTextField     txtUsername;
	private JButton        btnValider;
	private Image          imgFond;

	private JPanel         panelMid;
	private JPanel         panelBot;

	private Controller     ctrl;

	public Login( Controller ctrl )
	{
		this.imgFond     = getToolkit().getImage ( getClass().getResource("/background.png") );
		this.ctrl        = ctrl;

		this.cb          = new JCheckBox("Mode Hors Ligne", false);
		this.lblUsername = new JLabel("Nom d'Utilisateur");
		this.lblMail     = new JLabel("Adresse Mail");
		this.lblPassword = new JLabel("Mot De Passe");
		this.txtUsername = new JTextField(24);
		this.txtMail     = new JTextField(48);
		this.txtPassword = new JPasswordField(24);
		this.btnValider  = new JButton("Connexion");

		this.panelMid    = new JPanel();
		this.panelBot    = new JPanel();

		this.setLayout( new BorderLayout() );

		this.btnValider.addActionListener( this );
		this.cb        .addActionListener( this );

		this.cb      .setOpaque( false );
		this.panelMid.setOpaque( false );
		this.panelBot.setOpaque( false );

		this.add(this.panelMid, BorderLayout.CENTER);
		this.add(this.panelBot, BorderLayout.SOUTH);

		this.panelBot.add(this.cb);

		this.panelMid.add(this.lblMail);
		this.panelMid.add(this.txtMail);
		this.panelMid.add(this.lblPassword);
		this.panelMid.add(this.txtPassword);

		this.panelBot.add(this.btnValider);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.drawImage ( this.imgFond, 0, 0, this );
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.cb) // changer crack / pas crack
		{
			this.panelMid.removeAll();
			this.panelBot.removeAll();
			// this.removeAll();

			this.ctrl.getLogger().info(this.cb.isSelected() ? "switch : hors ligne" : "switch :   en ligne");

			if ( this.cb.isSelected() )
			{
				this.txtUsername.setText( ctrl.getSaver().get("offline-username") != null ? ctrl.getSaver().get("offline-username") : "" );

				this.panelBot.add( this.cb         );
				this.panelBot.add( this.btnValider );

				this.panelMid.add(this.lblUsername );
				this.panelMid.add(this.txtUsername );
			}
			else
			{
				this.txtPassword.setText("");
				this.txtMail    .setText("");

				this.panelBot.add( this.cb         );
				this.panelBot.add( this.btnValider );

				this.panelMid.add(this.lblMail     );
				this.panelMid.add(this.txtMail     );
				this.panelMid.add(this.lblPassword );
				this.panelMid.add(this.txtPassword );
			}

			this.repaint();
			this.revalidate();
		}
		else if (e.getSource() == this.btnValider) // btn connexion
		{
			String username = this.txtUsername.getText();
			String mail     = this.txtMail    .getText();
			char[] password = this.txtPassword.getPassword();

			if (this.cb.isSelected() && !username.isEmpty()) this.loginOffline(username);

			if (!this.cb.isSelected() && !mail.isEmpty() && password.length >= 3)
			{
				this.loginOnline(mail, password);
			}
		}
	}

	public void loginOffline(String username)
	{
		this.ctrl.getSaver().set("offline-username", username);
		this.ctrl.getSaver().save();

		if ( this.ctrl.getAuth().isAuth( false ) )
		{
			this.ctrl.getLogger().info("Connexion [ Mode : Hors Ligne] !");
			this.ctrl.getLogger().info(this.ctrl.getAuth().getAuthInfos().getUsername());
			this.ctrl.switchLogin();
		}
		else
		{
			this.ctrl.getLogger().err("Echec de la connexion [ Mode : En Ligne] !");
		}
	}

	public void loginOnline(String mail, char[] password)
	{
		this.ctrl.getAuth().auth(mail, password);

		if ( this.ctrl.getAuth().isAuth( true ) )
		{
			this.ctrl.getLogger().info("Connexion [ Mode : En Ligne] !");
			this.ctrl.getLogger().info(this.ctrl.getAuth().getAuthInfos().getUsername());
			this.ctrl.switchLogin();

			this.txtPassword.setText("");
			this.txtMail    .setText("");
		}
		else
		{
			this.ctrl.getLogger().err("Echec de la connexion [ Mode : En Ligne] !");
		}
	}

	public void checkOnline()
	{
		// lock
		this.btnValider .setEnabled( false );
		this.txtMail    .setEnabled( false );
		this.txtPassword.setEnabled( false );
		this.txtUsername.setEnabled( false );

		if ( this.ctrl.getAuth().isAuth( true ) )
		{
			this.ctrl.getLogger().info("Connexion (Auto) [ Mode : En Ligne] !");
			this.ctrl.getLogger().info(this.ctrl.getAuth().getAuthInfos().getUsername());
			this.ctrl.switchLogin();

			this.txtPassword.setText("");
			this.txtMail    .setText("");
		}

		// unlock
		this.btnValider .setEnabled( true );
		this.txtMail    .setEnabled( true );
		this.txtPassword.setEnabled( true );
		this.txtUsername.setEnabled( true );
	}
}